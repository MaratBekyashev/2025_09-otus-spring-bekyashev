package ru.otus.model.task;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateTaskRequest(
        @NotBlank(message = "Заголовок задачи должен быть заполнен")
        String title,

        String description,

        TaskPriorityEnum priority,

        Long   assigneeId,

        LocalDateTime dueDate
) {}