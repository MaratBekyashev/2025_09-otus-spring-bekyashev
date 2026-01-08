package ru.otus.hw.services;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.dto.BookInsertUpdateDto;
import ru.otus.hw.models.Book;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class BookServiceSecurityTest {

    @Autowired
    private BookService bookService;

    Book adminsBook;
    Book readersBook;

    @BeforeEach
    void setUp() {
        adminsBook = BookTestData.getDbBooks().get(0);
        readersBook = BookTestData.getDbBooks().get(2);
    }

    @Test
    @WithMockUser(username = "guest", roles = {"GUEST"})
    void createBook() {
        BookInsertUpdateDto book = new BookInsertUpdateDto (
                null,
                "Title for new book",
                1L,
                1L
        );

        assertDoesNotThrow(() -> bookService.insert(book));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "READER"})
    void editBook_byAdmin() {
        BookInsertUpdateDto book = new BookInsertUpdateDto (
                readersBook.getId(),
                readersBook.getTitle(),
                readersBook.getAuthor().getId(),
                readersBook.getGenre().getId()
        );

        assertDoesNotThrow(() -> bookService.update(book));
    }

    @Test
    @WithMockUser(username = "reader", roles = {"READER"})
    void editBook_byReader() throws Exception {
        BookInsertUpdateDto book = new BookInsertUpdateDto (
                adminsBook.getId(),
                adminsBook.getTitle(),
                adminsBook.getAuthor().getId(),
                adminsBook.getGenre().getId());
        assertThrows(AuthorizationDeniedException.class, () -> bookService.update(book));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "READER"})
    void deleteBook_byAdmin() {
          assertDoesNotThrow(() -> bookService.findById(readersBook.getId()));
    }

    @Test
    @WithMockUser(username = "reader", roles = {"READER"})
    void deleteAdminsBook_byReader() throws Exception {
        assertThrows(AuthorizationDeniedException.class, () -> bookService.deleteById(adminsBook.getId()));
    }
}