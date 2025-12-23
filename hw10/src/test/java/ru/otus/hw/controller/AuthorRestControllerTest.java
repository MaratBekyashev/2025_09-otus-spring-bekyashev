package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.AuthorTestData;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Юнит тесты Rest контроллера для работы с авторами")
@WebMvcTest
@ContextConfiguration(classes = AuthorRestController.class)
class AuthorRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthorService authorService;

    private List<Author> allAuthors;

    @BeforeEach
    void setUp() {
        allAuthors = AuthorTestData.getDbAuthors();
    }

    @Test
    @DisplayName("Получить список всех авторов")
    void getListAuthors() throws Exception{

        when(authorService.findAll()).thenReturn(allAuthors);

        mvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json(objectMapper.writeValueAsString(allAuthors)));
    }
}