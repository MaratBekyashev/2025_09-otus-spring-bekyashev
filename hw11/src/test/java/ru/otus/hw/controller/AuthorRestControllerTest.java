package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Юнит тесты Rest контроллера для работы с авторами")
@WebFluxTest
@ContextConfiguration(classes = AuthorRestController.class)
class AuthorRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    private List<Author> allAuthors;

    @BeforeEach
    void setUp() {
        allAuthors = AuthorTestData.getDbAuthors();
    }

    @Test
    @DisplayName("Получить список всех авторов")
    void getListAuthors() {

        when(authorService.findAll()).thenReturn(Flux.fromIterable(allAuthors));

        webTestClient.get()
                .uri("/api/authors")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Author.class)
                .hasSize(3)
                .isEqualTo(new ArrayList<Author>(allAuthors));

    }
}