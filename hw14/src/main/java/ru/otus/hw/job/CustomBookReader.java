package ru.otus.hw.job;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import ru.otus.hw.models.Book;

import java.util.Collections;
import java.util.List;

@Slf4j
public class CustomBookReader extends AbstractItemStreamItemReader<Book> implements ItemStreamReader<Book> {

    private static final String CURRENT_INDEX_KEY = "book.reader.current.index";
    private static final String CURRENT_PAGE_KEY  = "book.reader.current.page";

    private final EntityManagerFactory emf;
    private final int pageSize;

    private EntityManager em;
    private int currentIndex = 0;
    private int currentPage = 0;

    private List<Book> buffer = Collections.emptyList();
    private boolean noMoreData = false;

    public CustomBookReader(EntityManagerFactory emf, int pageSize) {
        this.emf = emf;
        this.pageSize = pageSize;
        setName("customBookReader");
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        this.em = emf.createEntityManager();

        if (executionContext.containsKey(CURRENT_INDEX_KEY)) {
            this.currentIndex = executionContext.getInt(CURRENT_INDEX_KEY);
            this.currentPage  = executionContext.getInt(CURRENT_PAGE_KEY);
            log.info("Restarting reader from page={}, index={}", currentPage, currentIndex);
        } else {
            log.info("Starting reader from beginning");
        }
    }

    @Override
    public Book read() {
        if (noMoreData) {
            return null;
        }

        if (currentIndex >= buffer.size()) {
            loadNextPage();
            currentIndex = 0;

            if (buffer.isEmpty()) {
                noMoreData = true;
                return null;
            }
        }

        Book book = buffer.get(currentIndex++);
        return book;
    }

    private void loadNextPage() {
        log.info("Loading page {}", currentPage);

        TypedQuery<Book> query = em.createQuery(
                "select b from Book b order by b.id",
                Book.class
        );

        // EntityGraph
        EntityGraph<?> graph = em.getEntityGraph("book-with-author-and-genre-graph");
        query.setHint("jakarta.persistence.fetchgraph", graph);

        query.setFirstResult(currentPage * pageSize);
        query.setMaxResults(pageSize);

        buffer = query.getResultList();
        currentPage++;
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt(CURRENT_INDEX_KEY, currentIndex);
        executionContext.putInt(CURRENT_PAGE_KEY, currentPage);

        log.info("Saving state: page={}, index={}", currentPage, currentIndex);
    }

    @Override
    public void close() throws ItemStreamException {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
}
