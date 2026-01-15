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
import ru.otus.hw.services.AuthorService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = AuthorController.class)
@Import(SecurityConfiguration.class)
class AuthorControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @WithMockUser("user")
    @DisplayName("Возвращает результат для авторизованного пользователя")
    void listAuthorsPage_postivite() throws Exception{
        mvc.perform(get("/library/authors"))
                .andExpect(status().isOk());
    }

    @DisplayName("Редирект на страницу авторизации для неавторизованного пользователя")
    @Test
    void listAuthorsPage_negative() throws Exception {
        mvc.perform(get("/library/authors"))
                .andExpect(status().is3xxRedirection());

    }
}