package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Comment;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    @NotBlank
    private String content;

    @NotNull
    private Long bookId;

    public CommentDto (Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.bookId = comment.getBookId();
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment);
    }

    public static List<CommentDto> toDtoList (List<Comment> dataList) {
        return dataList.stream().map(CommentDto::toDto).toList();
    }
}
