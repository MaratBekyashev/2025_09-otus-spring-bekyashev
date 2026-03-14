package ru.otus.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.dto.taskComment.CreateCommentRequest;
import ru.otus.dto.taskComment.TaskCommentDto;
import ru.otus.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<TaskCommentDto>> getComments(@PathVariable Long taskId) {
        var response=  commentService.getTaskComments(taskId);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TaskCommentDto> createComment(@PathVariable Long taskId,
                                                        @RequestBody @Valid CreateCommentRequest request) {
        var response = commentService.createComment(taskId, request.text());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public void createComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

}