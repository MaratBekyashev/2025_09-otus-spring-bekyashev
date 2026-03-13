package ru.otus.model.task;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CreateTaskRequest(
        @NotNull(message = "Заголовок задачи должен быть заполнен")
        String title,

        String description,

        TaskPriorityEnum priority,

        Long   assigneeId,

        LocalDateTime dueDate
) {}