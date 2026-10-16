package ru.loginov.dev.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.loginov.dev.dto.project.ProjectRequest;
import ru.loginov.dev.dto.project.ProjectResponse;
import ru.loginov.dev.dto.project.ProjectStatusRequest;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.ProjectStatus;
import ru.loginov.dev.services.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> findAllProjects(){
        List<Project> projects = projectService.findAllProjects();
        return projects.stream().map(p -> new ProjectResponse(p.getTitle(),
                p.getDescription(),
                p.getCreatedAt(),
                p.getProjectStatus())).toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse findProjectById(@PathVariable Long id){
        Project project = projectService.findProjectById(id);
        return new ProjectResponse(project.getTitle(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getProjectStatus());
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest projectRequest){
        Project savedProject = projectService.createProject(new Project(projectRequest.getTitle(),
                projectRequest.getDescription(),
                projectRequest.getProjectStatus()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProjectResponse(savedProject.getTitle(),
                savedProject.getDescription(),
                savedProject.getCreatedAt(),
                savedProject.getProjectStatus()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                 @Valid @RequestBody ProjectRequest projectRequest){
        Project updatedProject = projectService.updateProject(id, new Project(projectRequest.getTitle(),
                projectRequest.getDescription(),
                projectRequest.getProjectStatus()));
        return ResponseEntity.status(HttpStatus.OK).body(new ProjectResponse(updatedProject.getTitle(),
                updatedProject.getDescription(),
                updatedProject.getCreatedAt(),
                updatedProject.getProjectStatus()));
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<ProjectResponse> updateProjectStatus(@PathVariable Long id,
                                                       @Valid @RequestBody ProjectStatusRequest projectStatusRequest){
        Project updatedProject = projectService.updateProjectStatus(id, projectStatusRequest.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(new ProjectResponse(updatedProject.getTitle(),
                updatedProject.getDescription(),
                updatedProject.getCreatedAt(),
                updatedProject.getProjectStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/find-projects-with-me")
    public List<ProjectResponse> findAllProjectsForUser(){
        List<Project> projects = projectService.findAllProjectsForUser();
        return projects.stream().map(p ->
                new ProjectResponse(p.getTitle(),
                        p.getDescription(),
                        p.getCreatedAt(),
                        p.getProjectStatus())).toList();
    }
}