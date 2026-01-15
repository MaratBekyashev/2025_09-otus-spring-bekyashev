package ru.otus.hw.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest
@ContextConfiguration(classes = CommentController.class)
@Import(SecurityConfiguration.class)
class CommentControllerSecurityTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CommentService commentService;

    @Test
    @WithMockUser("user")
    void listCommentsPage_authorizedOk() throws Exception {
        when(commentService.findAllByBookId(1L)).thenReturn(List.of());
        when(bookService.findById(1L))
                .thenReturn(new BookDto(1L, "Book", new Author(11L, "Author"), new Genre(11L, "Genre")));

        mvc.perform(get("/library/comments")
                        .param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("commentListForBook"))
                .andExpect(model().attributeExists("comments"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void listCommentsPage_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser()
    void createPage_authorizedOk() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mvc.perform(get("/library/comments/create").param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("commentCreate"))
                .andExpect(model().attributeExists("comment"))
                .andExpect(model().attributeExists("books"));
    }

    @Test
    void createPage_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments/create").param("bookId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));    }

    @Test
    @WithMockUser("user")
    void createComment_authorizedOk() throws Exception {
    }

    @Test
    void createComment_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser("user")
    void editPage_authorizedOk() throws Exception {
        var cmt = CommentTestData.getChangeComment();
        when(commentService.findById(1L)).thenReturn(Optional.of(cmt));
        when(bookService.findAll()).thenReturn(BookDto.toDtoList(BookTestData.getDbBooks()));

        mvc.perform(get("/library/comments/edit/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("commentEdit"))
                .andExpect(model().attributeExists("comment"));
    }

    @Test
    void editPage_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser()
    void editComment_authorizedOk() throws Exception {
        var comment = CommentTestData.getChangeComment();
        var commentDto = CommentInsertUpdateDto.toDto(comment);
        when(commentService.update(commentDto)).thenReturn(comment);
        Long bookId = comment.getBook().getId();
        mvc.perform(post("/library/comments/edit/{id}", comment.getId())
                        .param("content", comment.getContent())
                        .param("bookId", bookId.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/comments?bookId=" + bookId));

       verify(commentService, times(1)).update(any(CommentInsertUpdateDto.class));
    }

    @Test
    void editComment_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser()
    void deleteComment_authorizedOk() throws Exception {
       var bookId = 1L;
        mvc.perform(post("/library/comments/delete/{id}", bookId)
                        .param("bookId", String.valueOf(bookId))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/library/comments?bookId=" + bookId));
        verify(commentService).deleteById(1L);
    }

    @Test
    void deleteComment_notAuthorizedRedirect() throws Exception {
        mvc.perform(get("/library/comments/delete/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
     }
}