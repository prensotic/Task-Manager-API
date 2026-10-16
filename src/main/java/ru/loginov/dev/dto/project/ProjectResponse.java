package ru.loginov.dev.dto.project;

import ru.loginov.dev.enums.ProjectStatus;

import java.time.LocalDateTime;

public record ProjectResponse(String title,
                              String description,
                              LocalDateTime createdAt,
                              ProjectStatus status) {
}
