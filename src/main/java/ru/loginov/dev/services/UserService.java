package ru.loginov.dev.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.exceptions.EmailAlreadyExistsException;
import ru.loginov.dev.exceptions.ProjectNotFoundException;
import ru.loginov.dev.exceptions.UsernameAlreadyExistsException;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectRepository projectRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ProjectUserRepository projectUserRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectUserRepository = projectUserRepository;
        this.projectRepository = projectRepository;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public User create(User user){
        if(user.getUsername() == null || user.getUsername().isBlank())
            throw new IllegalArgumentException("Username is required");

        if(user.getEmail() == null || user.getEmail().isBlank())
            throw new IllegalArgumentException("Email is required");

        if(userRepository.existsByUsername(user.getUsername()))
            throw new UsernameAlreadyExistsException("Username already exists");

        if(userRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistsException("Email already exists");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return save(user);
    }

    public User getByUsernameOrEmail(String usernameOrEmail){
        if(usernameOrEmail == null || usernameOrEmail.isBlank())
            throw new IllegalArgumentException("Username is required");

        return userRepository.findUserByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UserDetailsService userDetailsService() {
        return usernameOrEmail -> {
            User user = getByUsernameOrEmail(usernameOrEmail);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .build();
        };
    }

    public User getCurrentUser(){
        var username = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        return getByUsernameOrEmail(username);
    }

    public List<User> findAllUsersForProject(Long projectId){
        if(projectId == null || projectId <= 0)
            throw new IllegalArgumentException("Project`s ID must not be null or negative");

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID: " + projectId + " not found"));

        if(!project.getOwner().getId().equals(getCurrentUser().getId()))
            throw new AccessDeniedException("Access is blocked");

        return projectUserRepository.findAllByProjectId(projectId)
                .stream()
                .map(ProjectUser::getUser)
                .toList();
    }
}