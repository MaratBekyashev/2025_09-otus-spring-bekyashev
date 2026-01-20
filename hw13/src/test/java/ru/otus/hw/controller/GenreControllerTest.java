package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер для работы с авторами")
@WebMvcTest
@ContextConfiguration(classes = GenreController.class)
class GenreControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    void listGenresPage() {
    }

    @DisplayName("Загрузка списка жанров")
    @WithMockUser()
    @Test
    void listAuthorsPage() throws Exception {
        var genresList = GenreTestData.getDbGenres();
        given(genreService.findAll()).willReturn(genresList);
        mvc.perform(get("/library/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genresList"))
                .andExpect(model().attributeExists("genres"))
                .andExpect(model().attribute("genres", genresList));
    }
}