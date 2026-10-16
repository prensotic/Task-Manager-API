package ru.loginov.dev.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.ProjectStatus;
import ru.loginov.dev.exceptions.ProjectNotFoundException;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.repositories.ProjectUserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final ProjectUserRepository projectUserRepository;

    public ProjectService(ProjectRepository projectRepository, UserService userService, ProjectUserRepository projectUserRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.projectUserRepository = projectUserRepository;
    }

    public List<Project> findAllProjects(){
        return projectRepository.findAllProjectsByOwnerId(userService.getCurrentUser().getId());
    }

    private Optional<Project> findOptionalProjectById(Long id){
        if(id == null || id <= 0)
            throw new IllegalArgumentException("Project`s ID must not be null or negative");

        return projectRepository.findById(id);
    }

    public Project findProjectById(Long id){
        return findOptionalProjectById(id)
                .orElseThrow(() ->
                        new ProjectNotFoundException("Project with ID: " + id + " not found"));
    }

    @Transactional //У нас происходит два обновления бд, как одна атомарная операция
    public Project createProject(Project project){
        if (project == null)
            throw new IllegalArgumentException("Project must not be null");

        if(project.getTitle() == null || project.getTitle().isBlank())
            throw new IllegalArgumentException("Project`s title must be valid");

        if(project.getDescription() == null || project.getDescription().isBlank())
            throw new IllegalArgumentException("Project`s description must be valid");

        if(project.getProjectStatus() == null)
            throw new IllegalArgumentException("Project`s status must be valid");

        User owner = userService.getCurrentUser();

        project.setOwner(owner); // Владельцем проекта можем быть только его создатель

        Project savedProject = projectRepository.save(project);

        projectUserRepository.save(new ProjectUser(savedProject, owner));

        return savedProject;
    }

    public void deleteProject(Long id){
        Project project = getProjectForCurrentUser(id);
        projectRepository.delete(project);
    }

    public Project updateProjectStatus(Long id, ProjectStatus projectStatus){
        if(projectStatus == null)
            throw new IllegalArgumentException("New project`s status must be valid");

        Project project = getProjectForCurrentUser(id);
        project.setProjectStatus(projectStatus);

        return projectRepository.save(project);
    }

    public Project updateProject(Long id, Project projectUpdateData){
        if (projectUpdateData == null)
            throw new IllegalArgumentException("Project update data must not be null");

        if (projectUpdateData.getTitle() == null || projectUpdateData.getTitle().isBlank())
            throw new IllegalArgumentException("Project title must be valid");

        if (projectUpdateData.getDescription() == null || projectUpdateData.getDescription().isBlank())
            throw new IllegalArgumentException("Project description must be valid");

        Project project = getProjectForCurrentUser(id);

        project.setTitle(projectUpdateData.getTitle());
        project.setDescription(projectUpdateData.getDescription());

        return projectRepository.save(project);
    }

    public List<Project> findAllProjectsForUser(){
        User user = userService.getCurrentUser();

        return projectUserRepository.findAllByUserId(user.getId())
                .stream()
                .map(ProjectUser::getProject)
                .toList();
    }

    private Project getProjectForCurrentUser(Long id) { //Проверка на доступ пользователя к проекту
        Project project = findProjectById(id);
        Long ownerId = project.getOwner().getId();
        Long currentUserId = userService.getCurrentUser().getId();

        if (!Objects.equals(ownerId, currentUserId))
            throw new AccessDeniedException("You do not have permission to modify this project");

        return project;
    }
}