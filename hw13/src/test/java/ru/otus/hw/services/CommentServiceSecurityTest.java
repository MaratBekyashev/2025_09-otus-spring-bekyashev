package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class CommentServiceSecurityTest {

    @Autowired
    private CommentService commentService;

    Book book;
    Comment adminsComment;
    Comment readersComment;

    @BeforeEach
    void setUp() {
        book = BookTestData.getDbBooks().get(1);
        adminsComment = CommentTestData.getDbMapBooksComments().get(book.getId()).get(1);
        readersComment = CommentTestData.getDbMapBooksComments().get(book.getId()).get(2);
    }

    @Test
    @WithMockUser(username = "guest", roles = {"GUEST"})
    void createCommentByAnyUser() {
        CommentInsertUpdateDto comment = new CommentInsertUpdateDto (
                null,
                "New comment by any user",
                1L);

        assertDoesNotThrow(() -> commentService.insert(comment));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "READER"})
    void editComment_byAdmin() {
        var comment = new CommentInsertUpdateDto (
                readersComment.getId(),
                readersComment.getContent(),
                readersComment.getBook().getId());

        assertDoesNotThrow(() -> commentService.update(comment));
    }

    @Test
    @WithMockUser(username = "reader", roles = {"READER"})
    void editComment_byReader() throws Exception {
        var comment = new CommentInsertUpdateDto (
                adminsComment.getId(),
                adminsComment.getContent(),
                adminsComment.getBook().getId());
        assertThrows(AuthorizationDeniedException.class, () -> commentService.update(comment));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "READER"})
    void deleteComment_byAdmin() {
          assertDoesNotThrow(() -> commentService.findById(readersComment.getId()));
    }

    @Test
    @WithMockUser(username = "reader", roles = {"READER"})
    void deleteAdminsComment_byReader() throws Exception {
        assertThrows(AuthorizationDeniedException.class, () -> commentService.deleteById(adminsComment.getId()));
    }
}