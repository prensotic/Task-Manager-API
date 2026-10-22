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
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProjectTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void updateProject_shouldThrowException_whenUpdateDataIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> projectService.updateProject(10L, null));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldThrowException_whenTitleIsBlank() {
        Project updateData = mock(Project.class);
        when(updateData.getTitle()).thenReturn(" ");
        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(10L, updateData));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldThrowException_whenTitleIsNull() {
        Project updateData = mock(Project.class);
        when(updateData.getTitle()).thenReturn(null);
        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(10L, updateData));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldThrowException_whenDescriptionIsBlank() {
        Project updateData = mock(Project.class);

        when(updateData.getTitle()).thenReturn("Updated title");
        when(updateData.getDescription()).thenReturn(" ");

        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(10L, updateData));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldThrowException_whenDescriptionIsNull() {
        Project updateData = mock(Project.class);

        when(updateData.getTitle()).thenReturn("Updated title");
        when(updateData.getDescription()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> projectService.updateProject(10L, updateData));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldUpdateTitleAndDescription_whenCurrentUserIsOwner() {
        Project project = mock(Project.class);
        Project updateData = mock(Project.class);
        User owner = mock(User.class);

        when(updateData.getTitle()).thenReturn("Updated title");
        when(updateData.getDescription()).thenReturn("Updated description");
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(owner);
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.updateProject(10L, updateData);

        assertSame(project, result);

        verify(project).setTitle("Updated title");
        verify(project).setDescription("Updated description");
        verify(projectRepository).save(project);
    }

    @Test
    void updateProject_shouldDenyAccess_whenCurrentUserIsNotOwner() {
        Project project = mock(Project.class);
        Project updateData = mock(Project.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        when(updateData.getTitle()).thenReturn("Updated title");
        when(updateData.getDescription()).thenReturn("Updated description");
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);

        assertThrows(AccessDeniedException.class, () -> projectService.updateProject(10L, updateData));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProject_shouldThrowException_whenProjectDoesNotExist() {
        Project updateData = mock(Project.class);

        when(updateData.getTitle()).thenReturn("Updated title");
        when(updateData.getDescription()).thenReturn("Updated description");
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ru.loginov.dev.exceptions.ProjectNotFoundException.class,
                () -> projectService.updateProject(10L, updateData));

        verify(projectRepository, never()).save(any());
    }
}