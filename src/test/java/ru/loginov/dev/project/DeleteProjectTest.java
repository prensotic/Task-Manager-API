package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProjectTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void deleteProject_shouldDeleteProject_whenCurrentUserIsOwner() {
        Project project = mock(Project.class);
        User owner = mock(User.class);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(owner);

        projectService.deleteProject(10L);

        verify(projectRepository).delete(project);
    }

    @Test
    void deleteProject_shouldDenyAccess_whenCurrentUserIsNotOwner() {
        Project project = mock(Project.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);

        assertThrows(AccessDeniedException.class, () -> projectService.deleteProject(10L));

        verify(projectRepository, never()).delete(any());
    }

    @Test
    void deleteProject_shouldThrowException_whenProjectDoesNotExist() {
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ru.loginov.dev.exceptions.ProjectNotFoundException.class,
                () -> projectService.deleteProject(10L));
        verify(projectRepository, never()).delete(any());
    }
}