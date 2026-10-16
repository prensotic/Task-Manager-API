package ru.loginov.dev.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.exceptions.UserNotFoundException;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.UserRepository;


@Service
public class ProjectUserService {
    private final ProjectUserRepository projectUserRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    public ProjectUserService(ProjectUserRepository projectUserRepository, UserService userService, UserRepository userRepository, ProjectService projectService) {
        this.projectUserRepository = projectUserRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.projectService = projectService;
    }

    @Transactional
    public ProjectUser addUserToProject(Long userId, Long projectId){
        if(userId == null || userId <= 0)
            throw new IllegalArgumentException("User`s ID must not be null or negative");

        if(projectId == null || projectId <= 0)
            throw new IllegalArgumentException("Project` ID must not be null or negative");

        User currentUser = userService.getCurrentUser();
        Project project = projectService.findProjectById(projectId);

        if(!currentUser.getId().equals(project.getOwner().getId()))
            throw new AccessDeniedException("Access id blocked");

        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        if(projectUserRepository.existsByProjectIdAndUserId(projectId, userId))
            throw new IllegalArgumentException("User is already a member of this project");

        return projectUserRepository.save(new ProjectUser(project, user));
    }
}