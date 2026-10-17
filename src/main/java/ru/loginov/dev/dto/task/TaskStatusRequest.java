package ru.loginov.dev.dto.task;

import jakarta.validation.constraints.NotNull;
import ru.loginov.dev.enums.TaskStatus;

public class TaskStatusRequest {
    @NotNull(message = "Task`s status is required")
    private TaskStatus taskStatus;

    public TaskStatusRequest(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
