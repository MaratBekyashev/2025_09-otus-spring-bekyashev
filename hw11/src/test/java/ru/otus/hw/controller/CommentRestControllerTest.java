package ru.otus.hw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.data.BookTestData;
import ru.otus.hw.data.CommentTestData;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Юнит-тесты Rest контроллера для работы с комментариями к книгам")
@WebFluxTest
@ContextConfiguration(classes = CommentRestController.class)
@Disabled
class CommentRestControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CommentService commentService;

    private static Map<Long, List<Comment>> mapBooksComments;

    @Getter
    private static List<Comment> comments;

    private static Comment newComment;

    private static Comment changeComment;

    @Getter
    private static List<BookDto> books;

    @BeforeAll
    static void setUp() {
        mapBooksComments = CommentTestData.getDbMapBooksComments();
        comments = CommentTestData.getDbComments();
        newComment = CommentTestData.getNewComment();
        changeComment = CommentTestData.getChangeComment();
        books = BookDto.toDtoList(BookTestData.getDbBooks());
    }

    static Stream<BookDto> getDbBooks() {
        return books.stream();
    }

    @DisplayName("Получить список всех комментариев для заданной книги")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCommentList(BookDto book) {
        var dbComments =  CommentDto.toDtoList(mapBooksComments.get(book.getId()));
        List<CommentDto> expectedComments = CommentDto.toDtoList(mapBooksComments.get(book.getId()));
        when(commentService.findAllCommentsByBookId(book.getId()))
          .thenReturn(Flux.fromIterable(expectedComments));

        webTestClient.get()
                .uri("/api/books/{bookId}/comments", book.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(CommentDto.class)
                .hasSize(3)
                .isEqualTo(dbComments);
    }

    @ParameterizedTest
    @MethodSource("getComments")
    @DisplayName("Вернуть комментарий по его ID")
    void shouldReturnComment(Comment comment) {
        var commentDto = CommentDto.toDto(comment);
        when(commentService.findById(comment.getId())).thenReturn(Mono.just(commentDto));

        webTestClient.get()
                .uri("/api/books/comments/{id}", comment.getId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(new CommentDto(comment));
    }

    @Test
    @DisplayName("Создать новый комментарий к заданной книге")
    void shouldCreateComment() {
        Long bookId = newComment.getBookId();
        var newCommentDto = CommentDto.toDto(newComment);
        when(commentService.insert(newCommentDto)).thenReturn(Mono.just(newCommentDto));

        webTestClient.post()
                .uri("/api/books/comments", bookId)
                .bodyValue(newCommentDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(newCommentDto);
    }

    @Test
    @DisplayName("Изменить заданный комментарий к книге")
    void shouldEditComment() {
        var changeCommentDto = CommentDto.toDto(changeComment);
        when(commentService.update(changeCommentDto)).thenReturn(Mono.just(changeCommentDto));

        webTestClient.put()
                .uri("/api/books/comments/{id}", changeComment.getId())
                .bodyValue(changeCommentDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(CommentDto.class)
                .isEqualTo(CommentDto.toDto(changeComment));
    }

    @Test
    @DisplayName("Удалить заданный комментарий к книге")
    void shouldDeleteComment() {
        when(commentService.deleteById(changeComment.getId())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/books/comments/{id}", changeComment.getId())
                .exchange()
                .expectStatus()
                .isOk();

    }
}