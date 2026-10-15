package ru.loginov.dev.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ru.loginov.dev.enums.ProjectStatus;

public class ProjectRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 254, message = "Description must not 254 characters")
    private String description;

    @NotNull(message = "Project`s status is required")
    private ProjectStatus projectStatus;

    public ProjectRequest(String title, String description, ProjectStatus projectStatus) {
        this.title = title;
        this.description = description;
        this.projectStatus = projectStatus;
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

    public ProjectStatus getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(ProjectStatus projectStatus) {
        this.projectStatus = projectStatus;
    }
}
