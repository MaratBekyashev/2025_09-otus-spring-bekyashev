package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
@ContextConfiguration(classes = BookController.class)
@Import(SecurityConfiguration.class)
class BookControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @Test
    @WithMockUser("user")
    void listBooksPage_authorizedOK() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());
        mvc.perform(get("/library/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("booksList"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void listBooksPage_notAuthorizedRedirect()  throws Exception{
        mvc.perform(get("/library/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser("user")
    void createPage_authorizedOK()  throws Exception{
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mvc.perform(get("/library/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("bookCreate"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    void createPage_notAuthorizedRedirect() throws Exception{
        mvc.perform(get("/library/books/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser("user")
    void createBook_authorizedOK() throws Exception{
        mvc.perform(post("/library/books/create")
                        .param("title", "Book")
                        .param("authorId", "1")
                        .param("genreId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books"));

        verify(bookService).insert(any(BookInsertUpdateDto.class));
    }

    @Test
    void createBook_notAuthorizedRedirect()  throws Exception{
        mvc.perform(post("/library/books/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
   }

    @Test
    @WithMockUser("user")
    void editPage_authorized_ok() throws Exception {
        // given
        var book = new BookDto(BookTestData.getDbBooks().get(0));
        when(bookService.findById(1L)).thenReturn(book);
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        // when / then
        mvc.perform(get("/library/books/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("bookEdit"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));
    }

    @Test
    void editPage_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/books/edit/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser("user")
    void editBook_authorizedOk() throws Exception {
        mvc.perform(post("/library/books/edit/{id}", 1L)
                        .param("id", "1")
                        .param("title", "Updated book")
                        .param("authorId", "1")
                        .param("genreId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books"));

        verify(bookService).update(any(BookInsertUpdateDto.class));
    }

    @Test
    void editBook_notAuthorizedRedirect() throws Exception {
        mvc.perform(post("/library/books/edit/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
        verify(bookService, never()).update(any());
    }

    @Test
    @WithMockUser("user")
    void deleteBook_authorizedOk() throws Exception {
        mvc.perform(post("/library/books/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/books"));

        verify(bookService).deleteById(1L);
    }

    @Test
    void deleteBook_notAuthorizedRedirect() throws Exception {
        mvc.perform(post("/library/books/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}