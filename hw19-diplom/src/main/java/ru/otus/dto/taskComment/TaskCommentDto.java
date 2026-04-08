package ru.otus.dto.taskComment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.entity.TaskComment;
import ru.otus.model.IdentifableEntity;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class TaskCommentDto implements IdentifableEntity {

    private Long commentId;

    private String text;

    private Long taskId;

    private Long authorId;

    private String authorName;

    private LocalDateTime createdAt;

    public static TaskCommentDto toDto(TaskComment comment) {
        var result = TaskCommentDto.builder()
                .commentId(comment.getCommentId())
                .text(comment.getComment())
                .taskId(comment.getTask().getTaskId())
                .authorId(comment.getAuthor().getUserId())
                .authorName(comment.getAuthor().getUserName())
                .createdAt(comment.getCreateDate())
                .build();

        return result;
    }

    public static List<TaskCommentDto> toDtoList(List<TaskComment> comments) {
        return comments.stream().map(TaskCommentDto::toDto).toList();
    }

    @Override
    public Long getId() {
        return this.commentId;
    }
}