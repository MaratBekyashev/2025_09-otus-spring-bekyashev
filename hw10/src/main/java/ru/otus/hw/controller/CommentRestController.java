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
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/library/book/{bookId}/comment")
    public ResponseEntity<List<CommentDto>> getCommentsList(@PathVariable
                                                            Long bookId) {
        var list = commentService.findAllByBookId(bookId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/api/library/book/{bookId}/comment/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long bookId,
                                                 @PathVariable Long id) {
        CommentDto comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/api/library/book/{bookId}/comment/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long bookId,
                                                    @PathVariable Long id,
                                                    @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(commentService.update(commentDto));
    }

    @PostMapping("/api/library/book/{bookId}/comment")
    public ResponseEntity<CommentDto> createComment(@PathVariable String bookId, @RequestBody CommentDto commentDto) {
        var result = commentService.insert(commentDto);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/api/library/book/{bookId}/comment/{id}")
    public void deleteComment(@PathVariable Long bookId,
                              @PathVariable Long id) {
        commentService.deleteById(id);
    }
}
