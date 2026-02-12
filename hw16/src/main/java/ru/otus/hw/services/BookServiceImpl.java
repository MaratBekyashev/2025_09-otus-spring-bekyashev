package ru.otus.hw.services;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import java.util.List;

//@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final Counter booksCreated;


    public BookServiceImpl(AuthorRepository authorRepository,
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
    public BookDto findById(Long id) {
        return new BookDto(bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id))));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return BookDto.toDtoList(bookRepository.findAll());
    }

    @Override
    @Transactional
    public BookDto insert(BookDto bookIn) {
        Book createdBook = save(null, bookIn.getTitle(), bookIn.getAuthor().getId(), bookIn.getGenre().getId());
        booksCreated.increment();
        return new BookDto(createdBook);
    }

    @Override
    @Transactional
    public BookDto update(BookDto book) {
        Book updatedBook = save(book.getId(), book.getTitle(), book.getAuthor().getId(), book.getGenre().getId());
        return new BookDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
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
