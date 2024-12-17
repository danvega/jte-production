package dev.danvega.todo;

public record Todo(
        Long id,
        String title,
        String description,
        boolean completed
) {}

