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
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@DisplayName("Юнит тесты Rest контроллера для работы с жанрами")
@WebFluxTest
@ContextConfiguration(classes = GenreRestController.class)
class GenreRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    private static List<Genre> allGenres;

    @BeforeEach
    void setUp() {
        allGenres = GenreTestData.getDbGenres();
    }

    @Test
    @DisplayName("Получить список всех жанров")
    void getListAuthors() {

        when(genreService.findAll()).thenReturn(Flux.fromIterable(allGenres));

        webTestClient.get()
                .uri("/api/genres")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Genre.class)
                .hasSize(3)
                .isEqualTo(new ArrayList<Genre>(allGenres));

    }
}