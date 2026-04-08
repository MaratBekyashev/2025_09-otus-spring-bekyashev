package ru.otus.dto.taskComment;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
        @NotBlank(message = "Текст комментария должен быть заполнен")
        String text
) {}