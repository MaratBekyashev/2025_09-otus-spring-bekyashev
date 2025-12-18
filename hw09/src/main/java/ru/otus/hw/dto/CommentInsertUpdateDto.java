package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentInsertUpdateDto {

    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Long bookId;

    public static CommentInsertUpdateDto toDto(Comment comment) {
        return new CommentInsertUpdateDto(comment.getId(), comment.getContent(), comment.getBook().getId());
    }
}
