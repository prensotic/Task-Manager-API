package ru.loginov.dev.dto.task;

import ru.loginov.dev.entities.Project;
import ru.loginov.dev.enums.TaskPriority;
import ru.loginov.dev.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(String title,
                           String description,
                           LocalDateTime dueDate,
                           TaskStatus taskStatus,
                           TaskPriority taskPriority,
                           Long projectId) {
}
