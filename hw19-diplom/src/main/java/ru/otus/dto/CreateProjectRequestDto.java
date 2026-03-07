package ru.otus.dto;

public record CreateProjectRequestDto(
        String name,
        String description
) {}