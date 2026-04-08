package ru.otus.service;

import ru.otus.dto.taskComment.TaskCommentDto;
import java.util.List;

public interface CommentService {

    List<TaskCommentDto> getTaskComments(Long taskId);

    TaskCommentDto createComment(Long taskId, String text) ;

    void deleteComment(Long commentId);

    List<TaskCommentDto> deleteTaskComments(Long taskId);
}