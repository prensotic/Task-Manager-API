package ru.loginov.dev.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.loginov.dev.dto.project.ProjectRequest;
import ru.loginov.dev.dto.project.ProjectStatusRequest;
import ru.loginov.dev.entities.Project;
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
    public List<Project> findAllProjects(){ return projectService.findAllProjects(); }

    @GetMapping("/{id}")
    public Project findProjectById(@PathVariable Long id){
        return projectService.findProjectById(id);
    }

    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody ProjectRequest projectRequest){
        Project savedProject = projectService.createProject(new Project(projectRequest.getTitle(),
                projectRequest.getDescription(),
                projectRequest.getProjectStatus()));
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable Long id,
                                                 @Valid @RequestBody ProjectRequest projectRequest){
        Project updatedProject = projectService.updateProject(id, new Project(projectRequest.getTitle(),
                projectRequest.getDescription(),
                projectRequest.getProjectStatus()));
        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @PutMapping("/{id}/update-status")
    public ResponseEntity<Project> updateProjectStatus(@PathVariable Long id,
                                                       @Valid @RequestBody ProjectStatusRequest projectStatusRequest){
        Project updatedProject = projectService.updateProjectStatus(id, projectStatusRequest.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}