package ru.loginov.dev.dto.project;

import jakarta.validation.constraints.NotNull;
import ru.loginov.dev.enums.ProjectStatus;

public class ProjectStatusRequest {
    @NotNull(message = "Project status must not be null")
    private ProjectStatus status;

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }
}
