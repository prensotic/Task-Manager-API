package ru.loginov.dev.dto.task;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.ProjectStatus;
import ru.loginov.dev.enums.TaskPriority;
import ru.loginov.dev.enums.TaskStatus;

import java.time.LocalDateTime;

public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 254, message = "Description must not 254 characters")
    private String description;

    @NotNull(message = "Task`s status is required")
    private TaskStatus taskStatus;

    @NotNull(message = "Task`s priority is required")
    private TaskPriority taskPriority;

    @NotNull(message = "Task`s deadline is required")
    @Future(message = "Task's deadline must be in the future")
    private LocalDateTime dueDate;

    private User user;

    public TaskRequest(String title,
                       String description,
                       TaskStatus taskStatus,
                       TaskPriority taskPriority,
                       LocalDateTime dueDate,
                       User user) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskPriority = taskPriority;
        this.dueDate = dueDate;
        this.user = user;
    }

    public TaskRequest(String title,
                       String description,
                       LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public TaskRequest() {}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
