package ru.otus.hw.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.GenreService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = GenreController.class)
@Import(SecurityConfiguration.class)
class GenreControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    @Test
    @WithMockUser("user")
    @DisplayName("Возвращает результат для авторизованного пользователя")
    void listGenresPage_autorizedOk() throws Exception{
        mvc.perform(get("/library/genres"))
                .andExpect(status().isOk());
    }

    @DisplayName("Редирект на страницу авторизации для неавторизованного пользователя")
    @Test
    void listAuthorsPage_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/genres"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}