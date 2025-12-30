package ru.otus.hw.controller;

import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Юнит тесты Rest контроллера для работы с книгами")
@WebFluxTest
@ContextConfiguration(classes = BookRestController.class)
class BookRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Getter
    private static List<Book> books;

    private static Book newBook;

    private static Book changeBook;


    @BeforeAll
    static void setUp() {
        books = BookTestData.getDbBooks();
        newBook = BookTestData.getNewBook();
        changeBook = BookTestData.getChangeBook();
    }

    @Test
    @DisplayName("Список всех книг")
    void getListBooksPage() {
        when(bookService.findAll()).thenReturn(Flux.fromIterable(BookDto.toDtoList(books)));

        webTestClient.get()
                .uri("/api/books")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(BookDto.class)
                .hasSize(3)
                .isEqualTo(BookDto.toDtoList(books));

        verify(bookService, times(1)).findAll();
    }

    static Stream<Book> getDbBooks() {
        return books.stream();
    }

    @ParameterizedTest
    @MethodSource("getDbBooks")
    @DisplayName("Поиск книги по ID")
    void getBookById(Book book) {
        when(bookService.findById(book.getId())).thenReturn(Mono.just(new BookDto(book)));
        webTestClient.get()
                .uri("/api/books/{id}", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(book));
    }

    @Test
    @DisplayName("Редактирование книги")
    void updateBook() {
        BookDto changeBookDto = new BookDto(changeBook);
        when(bookService.updateBook(changeBookDto)).thenReturn(Mono.just(changeBookDto));
        webTestClient.put()
                .uri("/api/books/{id}", changeBookDto.getId())
                .bodyValue(changeBookDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(changeBook));
    }

    @Test
    @DisplayName("Создание новой книги")
    void createBook() {
        BookDto newBookDto = new BookDto(newBook);
        when(bookService.insertBook(newBookDto)).thenReturn(Mono.just(newBookDto));

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(newBookDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BookDto.class)
                .isEqualTo(new BookDto(newBook));
    }

    @Test
    @DisplayName("Удаление книги по ID")
    void deleteBook() {
        Book bookToDelete = books.get(0);
        when(bookService.deleteById(bookToDelete.getId())).thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/api/books/{id}", bookToDelete.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}