package ru.otus.hw.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.List;

@DisplayName("Защита ресурсов при работе с контроллерами для книг")
@WebMvcTest
@ContextConfiguration(classes = {
        BookController.class,
        CommentController.class,
        AuthorController.class,
        GenreController.class,
        HomeController.class })
@Import(SecurityConfiguration.class)
public class SecurityOfEndpointsTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        when(bookService.findById(anyLong())).thenReturn(new BookDto(1L, "Test book", new Author(1L, "Author"),new Genre(1L,"Genre")));
    }

    @DisplayName("Страница авторизации должна быть доступна всем")
    @Test
    void loginPageShouldBeAccessibleWithoutAuth() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @DisplayName("Редирект на страницу авторизации для неавторизованного пользователя")
    @Test
    void allControllersShouldBeProtected() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(get("/authors"))
                .andExpect(status().is3xxRedirection());

        mvc.perform(get("/genres"))
                .andExpect(status().is3xxRedirection());
    }

}
