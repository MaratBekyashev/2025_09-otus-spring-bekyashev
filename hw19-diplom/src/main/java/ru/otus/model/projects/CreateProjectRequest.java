package ru.otus.model.projects;

import jakarta.validation.constraints.NotNull;

public record CreateProjectRequest(
        @NotNull(message = "Имя проекта не может быть пустым")
        String name,
        String description
) {}