package ru.otus.model.projects;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(
        @NotBlank(message = "Имя проекта не может быть пустым")
        String name,
        String description
) {}