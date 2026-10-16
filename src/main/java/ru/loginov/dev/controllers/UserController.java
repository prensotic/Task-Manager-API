package ru.loginov.dev.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.loginov.dev.dto.user.UserResponse;
import ru.loginov.dev.dto.user.UserWithProjectResponse;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.services.ProjectUserService;
import ru.loginov.dev.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ProjectUserService projectUserService;

    public UserController(UserService userService, ProjectUserService projectUserService) {
        this.userService = userService;
        this.projectUserService = projectUserService;
    }

    @GetMapping("/me")
    public UserResponse findMe(){
        User user = userService.getCurrentUser();
        return new UserResponse(user.getUsername(), user.getEmail());
    }

    @GetMapping("/projects/{projectId}/members")
    public List<UserResponse> findAllUsersForProject(@PathVariable Long projectId){
        List<User> members = userService.findAllUsersForProject(projectId);
        return members.stream().map(u -> new UserResponse(u.getUsername(), u.getEmail())).toList();
    }

    @PostMapping("/projects/{projectId}/add-user-to-project/{userId}")
    public ResponseEntity<UserWithProjectResponse> addUserToProject(@PathVariable("projectId") Long projectId,
                                                                    @PathVariable("userId") Long userId){
        ProjectUser savedProjectUser = projectUserService.addUserToProject(userId, projectId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserWithProjectResponse(savedProjectUser.getUser().getUsername(),
                        savedProjectUser.getUser().getEmail(),
                        savedProjectUser.getProject().getTitle()));
    }
}