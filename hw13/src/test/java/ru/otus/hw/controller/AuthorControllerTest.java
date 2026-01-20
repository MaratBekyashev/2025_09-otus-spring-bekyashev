package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.GenreTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@DisplayName("Контроллер для работы с авторами")
@WebMvcTest
@ContextConfiguration(classes = AuthorController.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    private static List<Author> dbAuthors;
    @BeforeAll
    static void setUp() {
        dbAuthors = AuthorTestData.getDbAuthors();
    }

    @DisplayName("Загрузка списка авторов")
    @WithMockUser("user")
    @Test
    void listAuthorsPage() throws Exception {
        given(authorService.findAll()).willReturn(dbAuthors);
        mvc.perform(get("/library/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authorsList"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attribute("authors", dbAuthors));
    }

}