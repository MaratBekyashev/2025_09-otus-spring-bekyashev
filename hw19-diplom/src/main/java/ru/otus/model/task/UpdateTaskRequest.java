package ru.otus.model.task;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatusEnum status,
        TaskPriorityEnum priority,
        Long assigneeId,
        LocalDateTime dueDate
) {}