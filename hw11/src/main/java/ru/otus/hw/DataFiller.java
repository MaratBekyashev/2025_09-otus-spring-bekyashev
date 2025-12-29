package ru.otus.hw;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Scheduler;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataFiller implements ApplicationRunner {
    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final GenreRepository genreRepository;

    private final Scheduler workerPool;


    @Override
    public void run(ApplicationArguments args) {

        authorRepository.saveAll(List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        )).publishOn(workerPool).subscribe(author -> log.info("saved author:{}", author));

        genreRepository.saveAll(List.of(
                new Genre(null,"Genre_1"),
                new Genre(null,"Genre_2"),
                new Genre(null,"Genre_3")
        )).publishOn(workerPool).subscribe(genre -> log.info("saved genre:{}", genre));

        bookRepository.saveAll(Arrays.asList(
                new Book(null, "Book1_Title", 1L, 1L),
                new Book(null, "Book2_Title", 2L, 2L),
                new Book(null, "Book3_Title", 3L, 3L)
        )).publishOn(workerPool)
                .subscribe(savedBook -> {
                    log.info("saved book:{}", savedBook);
                    if (savedBook.getId() == 1L){
                        commentRepository.saveAll(
                                        Arrays.asList(
                                                new Comment(null, "txt_1_" + savedBook.getId(), savedBook.getId()),
                                                new Comment(null, "txt_2_" + savedBook.getId(), savedBook.getId()))
                                )
                                .publishOn(workerPool)
                                .subscribe(savedBookComments -> log.info("saved comments:{}", savedBookComments));

                    }
                    else if (savedBook.getId() == 2L) {
                        commentRepository.saveAll(
                                        Arrays.asList(
                                                new Comment(null, "txt_1_" + savedBook.getId(), savedBook.getId()),
                                                new Comment(null, "txt_2_" + savedBook.getId(), savedBook.getId()))
                                )
                                .publishOn(workerPool)
                                .subscribe(savedBookComments -> log.info("saved comments:{}", savedBookComments));
                    }

                });

    }
}
