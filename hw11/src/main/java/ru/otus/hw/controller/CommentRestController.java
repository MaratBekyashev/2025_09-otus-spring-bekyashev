package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.UpdateCommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/books/{bookId}/comments")
    public ResponseEntity<Flux<CommentDto>> getCommentsList(@PathVariable Long bookId) {
        var resultList = commentService.findAllCommentsByBookId(bookId);
        return ResponseEntity.ok(resultList);
    }

    @GetMapping("/api/books/comments/{id}")
    public ResponseEntity<Mono<CommentDto>> getComment(@PathVariable Long id) {
        var comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/api/books/comments/{id}")
    public ResponseEntity<Mono<CommentDto>> updateComment(@PathVariable Long id,
                                                    @RequestBody UpdateCommentDto updateComment) {
        CommentDto commentDto = new CommentDto(id, updateComment.getContent(), updateComment.getBookId());
        var result = commentService.update(commentDto);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/books/comments")
    public ResponseEntity<Mono<CommentDto>> createComment(@RequestBody CommentDto commentDto) {
        var result = commentService.insert(commentDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/books/comments/{id}")
    public Mono<Void> deleteComment(@PathVariable Long id) {
        return commentService.deleteById(id);
    }
}
