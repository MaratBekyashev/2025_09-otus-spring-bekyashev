package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final R2dbcEntityTemplate template;

    static final String FIND_BOOK_SQL = """
            select b.id as book_id,
                   b.title, 
                   a.id as author_id, 
                   a.full_name author_name, 
                   g.id as genre_id, 
                   g.name as genre_name
              from books b
              join authors a
                on b.author_id = a.id
              join genres g
                on g.id = b.genre_id
                               
            """;

    @Override
    public Mono<BookDto> findById(Long bookId) {

        var result = template
                .getDatabaseClient()
                .sql(FIND_BOOK_SQL + "where b.id = :bookId")
                .bind("bookId", bookId)
                .map((row, meta) -> new BookDto(
                        row.get("book_id", Long.class),
                        row.get("title", String.class),
                        row.get("author_id", Long.class),
                        row.get("author_name", String.class),
                        row.get("genre_id", Long.class),
                        row.get("genre_name", String.class)
                ))
                .one();

        return result;
    }

    @Override
    public Flux<BookDto> findAll() {
        var result = template
                .getDatabaseClient()
                .sql(FIND_BOOK_SQL)
                .map((row, meta) -> new BookDto(
                        row.get("book_id", Long.class),
                        row.get("title", String.class),
                        row.get("author_id", Long.class),
                        row.get("author_name", String.class),
                        row.get("genre_id", Long.class),
                        row.get("genre_name", String.class)
                ))
                .all();

        return result;
    }

    @Override
    public Mono<BookDto> insertBook(BookDto book) {
        var result = Mono.zip(
                authorRepository
                        .findById(book.getAuthor().getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s is not found".formatted(book.getAuthor().getId())))),
                genreRepository
                        .findById(book.getGenre().getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre with id %s is not found".formatted(book.getGenre().getId()))))
           ).flatMap(data -> {
            String title = book.getTitle();
            Author author = data.getT1();
            Genre genre = data.getT2();
            return bookRepository.save(new Book(null, title, author.getId(), genre.getId()));
        }).map(BookDto::new);

        return result;
    }

    @Override
    public Mono<BookDto> updateBook(BookDto book) {
        var result = Mono.zip(
                bookRepository
                        .findById(book.getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Book with id %s is not found".formatted(book.getId())))),
                authorRepository
                        .findById(book.getAuthor().getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Author with id %s is not found".formatted(book.getAuthor().getId())))),
                genreRepository
                        .findById(book.getGenre().getId())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre with id %s is not found".formatted(book.getGenre().getId()))))
        ).flatMap(data -> {
            String changedTitle = book.getTitle();
            Author author = data.getT2();
            Genre genre = data.getT3();
            return bookRepository.save(new Book(book.getId(), changedTitle, author.getId(), genre.getId()));
        }).map(BookDto::new);

        return result;
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return bookRepository.deleteById(id);
    }

}
