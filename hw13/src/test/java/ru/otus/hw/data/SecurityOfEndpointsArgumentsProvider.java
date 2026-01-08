package ru.otus.hw.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityOfEndpointsArgumentsProvider implements ArgumentsProvider {

    private final SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user;

    private final ObjectMapper objectMapper;

    private final BookDto newBook;

    private final BookDto changeBook;

    private final Comment newComment;

    private final Comment changeComment;

    public SecurityOfEndpointsArgumentsProvider() {
        this.user = user("User");
        this.objectMapper = new ObjectMapper();
        this.newBook = new BookDto(BookTestData.getNewBook());
        this.changeBook = new BookDto(BookTestData.getChangeBook());
        this.newComment = CommentTestData.getNewComment();
        this.changeComment = CommentTestData.getChangeComment();
    }

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws JsonProcessingException {


        return Stream.of(getBooksArgumentsStream(),
                        getCommentsArgumentsStream(),
                        getAuthorsArgumentsStream(),
                        getGenresArgumentsStream()
                        )
                .flatMap(i -> i);
    }

    private Stream<Arguments> getBooksArgumentsStream() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком книг c user",
                        user,
                        get("/library/books"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком книг без user",
                        null,
                        get("/library/books"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги c user",
                        user,
                        get("/library/books/create"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания новой книги без user",
                        null,
                        get("/library/books/create"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги c user",
                        user,
                        post("/library/books/edit/{id}", changeBook.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования книги без user",
                        null,
                        get("/library/books/edit/{id}", changeBook.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getCommentsArgumentsStream() throws JsonProcessingException {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге c user",
                        user,
                        get("/library/comments").param("bookId", changeBook.getId().toString()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком комментариев к книге без user",
                        null,
                        get("/library/comments").param("bookId", changeBook.getId().toString()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге c user",
                        user,
                        post("/library/comments/create").param("bookId", changeBook.getId().toString()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу создания нового комментария к книге без user",
                        null,
                        post("/library/comments/create").param("bookId", changeBook.getId().toString()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге c user",
                        user,
                        post("/library/comments/edit/{id}", changeComment.getId()),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу редактирования комментария к книге без user",
                        null,
                        post("/library/comments/edit/{id}", changeComment.getId()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                ),
                Arguments.of(
                        "переходим на страницу удаления комментария к книге c user",
                        user,
                        post("/library/comments/delete/{id}", changeComment.getId()).param("bookId", changeBook.getId().toString()),
                        List.of(status().is3xxRedirection())
                ),
                Arguments.of(
                        "переходим на страницу удаления комментария к книге без user",
                        null,
                        post("/library/comments/delete/{id}", changeComment.getId()).param("bookId", changeBook.getId().toString()),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getAuthorsArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком авторов c user",
                        user,
                        get("/library/authors"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком авторов без user",
                        null,
                        get("/library/authors"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }

    private Stream<Arguments> getGenresArgumentsStream() {
        return Stream.of(
                Arguments.of(
                        "переходим на страницу со списком жанров c user",
                        user,
                        get("/library/genres"),
                        List.of(status().isOk())
                ),
                Arguments.of(
                        "переходим на страницу со списком жанров без user",
                        null,
                        get("/library/genres"),
                        List.of(status().is3xxRedirection(), redirectedUrlPattern("**/login"))
                )
        );
    }
}
