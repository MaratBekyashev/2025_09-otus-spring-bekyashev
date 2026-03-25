package ru.otus.model.task;

import java.time.LocalDate;
import java.util.List;

public record TaskSearchFilter(

        Long projectId,
        Long assigneeId,
        List<TaskStatusEnum> statuses,
        TaskPriorityEnum priority,
        String title,
        LocalDate dueDateFrom,
        LocalDate dueDateTo
) {}