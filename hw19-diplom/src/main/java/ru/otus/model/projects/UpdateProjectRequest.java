package ru.otus.model.projects;

public record UpdateProjectRequest(
        String name,
        String description
) {}