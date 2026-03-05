package ru.otus.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.dto.BookDto;
import ru.otus.exception.EntityNotFoundException;
import ru.otus.repository.UserRepository;
import ru.otus.exception.ServiceNotAvailableException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class BookServiceImpl implements BookService {
    private final UserRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final Counter booksCreated;


    public BookServiceImpl(UserRepository authorRepository,
                           GenreRepository genreRepository,
                           BookRepository bookRepository,
                           MeterRegistry registry) {
        this.authorRepository = authorRepository;
        this.genreRepository = genreRepository;
        this.bookRepository = bookRepository;
        this.booksCreated = Counter
                .builder("books.created")
                .description("Number of created books")
                .register(registry);
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindBookById")
    public BookDto findById(Long id) {
        return new BookDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    private BookDto fallbackFindBookById(Long id, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for findById({})", id, ex);
        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional(readOnly = true)
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackFindAllBooks")
    public List<BookDto> findAll() {
        return BookDto.toDtoList(bookRepository.findAll());
    }

    private List<BookDto> fallbackFindAllBooks(Throwable ex) {
        log.error("DB unavailable, fallback triggered", ex);
        return Collections.emptyList();
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForSaveBook")
    public BookDto insert(BookDto bookIn) {
        Book createdBook = save(null, bookIn.getTitle(), bookIn.getAuthor().getId(), bookIn.getGenre().getId());
        booksCreated.increment();
        return new BookDto(createdBook);
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForSaveBook")
    public BookDto update(BookDto book) {
        Book updatedBook = save(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        return new BookDto(updatedBook);
    }

    private BookDto fallbackForSaveBook(BookDto book, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for save book (book.id={})", book.getId(), ex);

        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    @Override
    @Transactional
    @Retry(name = "dbRetry")
    @CircuitBreaker(name = "dbCircuitBreaker", fallbackMethod = "fallbackForDeleteBook")
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    private void fallbackForDeleteBook(Long id, Throwable ex) throws ServiceNotAvailableException {
        log.error("Fallback triggered for deleting book (id={})", id, ex);

        throw new ServiceNotAvailableException("Database is temporarily unavailable");
    }

    private Book save(Long id, String title, Long authorId, Long genreId) {
        var author = authorRepository
                .findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genre = genreRepository
                .findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %d not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookRepository.save(book);
    }
}
