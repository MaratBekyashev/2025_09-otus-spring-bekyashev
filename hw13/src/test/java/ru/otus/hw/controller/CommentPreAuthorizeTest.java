package ru.otus.hw.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.dto.CommentInsertUpdateDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.services.CommentService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("Тесты безопасности на основе доменной модели при работе с комментариями к книгам")
@SpringBootTest
@EnableMethodSecurity
@Transactional
class CommentPreAuthorizeTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    private static Book book;
    @BeforeAll
    public static void beforeAll() {
        book = new Book(1L, "Book1", new Author(1L, "f"), new Genre(1L,"f"),"?");
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("Изменение комментария разрешено для его создателя")
    void update_allowed_for_owner() {

        Comment comment = new Comment();
        comment.setContent("Test");
        comment.setBook(book);
        comment.setCreateUser("user1");
        comment = commentRepository.save(comment);

        CommentInsertUpdateDto dto = new CommentInsertUpdateDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setBookId(book.getId());

        assertDoesNotThrow(() -> commentService.update(dto));
    }

    @Test
    @WithMockUser(username = "user2")
    @DisplayName("Обновление комментария запрещено для НЕ создателя")
    void update_denied_for_not_owner() {
        Comment comment = new Comment();
        comment.setContent("Test");
        comment.setBook(book);
        comment.setCreateUser("user1");
        comment = commentRepository.save(comment);

        CommentInsertUpdateDto dto = new CommentInsertUpdateDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setBookId(book.getId());

        assertThrows(AccessDeniedException.class,
                () -> commentService.update(dto));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Изменение комментария разрешено для админа")
    void update_allowed_for_admin() {
        Comment comment = new Comment();
        comment.setContent("Test");
        comment.setBook(book);
        comment.setCreateUser("user1");
        comment = commentRepository.save(comment);

        CommentInsertUpdateDto dto = new CommentInsertUpdateDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setBookId(book.getId());

        assertDoesNotThrow(() -> commentService.update(dto));
    }
}