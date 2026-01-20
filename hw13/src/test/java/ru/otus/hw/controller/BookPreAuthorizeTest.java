package ru.otus.hw.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.services.BookService;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@DisplayName("Тесты безопасности на основе доменной модели при работе с книгами")
@SpringBootTest
@EnableMethodSecurity
@Transactional
class BookPreAuthorizeTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("Обновление книги разрешено для админа")
    void update_allowed_for_owner() {
        Book book = new Book();
        book.setTitle("Test");
        book.setAuthor(new Author(1L, "Author1"));
        book.setGenre(new Genre(1L, "Genre1"));
        book.setCreateUser("user1");
        book = bookRepository.save(book);

        BookInsertUpdateDto dto = new BookInsertUpdateDto();
        dto.setId(book.getId());
        dto.setTitle("Updated");
        dto.setAuthorId(book.getAuthor().getId());
        dto.setGenreId(book.getGenre().getId());

        assertDoesNotThrow(() -> bookService.update(dto));
    }

    @Test
    @WithMockUser(username = "user2")
    @DisplayName("Обновление запрещено для НЕ создателя книги")
    void update_denied_for_not_owner() {
        Book book = new Book();
        book.setTitle("Test");
        book.setAuthor(new Author(1L, "Author1"));
        book.setGenre(new Genre(1L, "Genre1"));
        book.setCreateUser("user1");
        book = bookRepository.save(book);

        BookInsertUpdateDto dto = new BookInsertUpdateDto();
        dto.setId(book.getId());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setGenreId(book.getGenre().getId());

        assertThrows(AccessDeniedException.class,
                () -> bookService.update(dto));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Обновление разрешено для админа")
    void update_allowed_for_admin() {
        Book book = new Book();
        book.setTitle("Test");
        book.setAuthor(new Author(1L, "Author1"));
        book.setGenre(new Genre(1L, "Genre1"));
        book.setCreateUser("user1");
        book = bookRepository.save(book);

        BookInsertUpdateDto dto = new BookInsertUpdateDto();
        dto.setId(book.getId());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setGenreId(book.getGenre().getId());

        assertDoesNotThrow(() -> bookService.update(dto));
    }
}