package ru.loginov.dev.dto.task;

import jakarta.validation.constraints.NotNull;
import ru.loginov.dev.enums.TaskPriority;

public class TaskPriorityRequest {
    @NotNull(message = "Task`s priority is required")
    private TaskPriority taskPriority;

    public TaskPriorityRequest(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }

    public TaskPriority getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(TaskPriority taskPriority) {
        this.taskPriority = taskPriority;
    }
}
