package ru.otus.hw;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.modelMongo.BookDocument;
import ru.otus.hw.modelMongo.CommentDocument;

import jakarta.persistence.EntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MigrationIntegrationTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job migrateBooksJob;

    @Autowired
    private EntityManager em;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testMigrationJob() throws Exception {
        // Запускаем job
        jobLauncher.run(
                migrateBooksJob,
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters()
        );

        // Проверяем количество книг
        Long h2BooksCount = em.createQuery("select count(b) from Book b", Long.class).getSingleResult();
        long mongoBooksCount = mongoTemplate.getCollection("books").countDocuments();
        assertEquals(h2BooksCount, mongoBooksCount, "Количество книг должно совпадать");

        // Проверяем отношения Author и Genre
        List<BookDocument> mongoBooks = mongoTemplate.findAll(BookDocument.class, "books");
        for (BookDocument bookDoc : mongoBooks) {
            assertNotNull(bookDoc.getAuthor(), "Автор книги не должен быть null");
            assertNotNull(bookDoc.getGenre(), "Жанр книги не должен быть null");
        }

        // Проверяем комментарии
        for (Book book : em.createQuery("select b from Book b", Book.class).getResultList()) {
            BookDocument mongoBook = mongoTemplate.findById(book.getId().toString(), BookDocument.class, "books");
            assertNotNull(mongoBook);

            // H2 комментарии
            List<Comment> comments = em.createQuery("select c from Comment c where c.book.id = :bookId", Comment.class)
                    .setParameter("bookId", book.getId())
                    .getResultList();

            // Mongo комментарии
            List<CommentDocument> mongoComments = mongoBook.getComments();
            assertEquals(comments.size(), mongoComments.size(), "Комментарии должны совпадать по количеству");
        }
    }
}
