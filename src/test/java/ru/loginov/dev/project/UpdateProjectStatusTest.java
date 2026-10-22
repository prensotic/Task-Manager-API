package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.ProjectStatus;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProjectStatusTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void updateProjectStatus_shouldThrowException_whenStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> projectService.updateProjectStatus(10L, null));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void updateProjectStatus_shouldUpdateStatus_whenCurrentUserIsOwner() {
        Project project = mock(Project.class);
        User owner = mock(User.class);
        ProjectStatus status = ProjectStatus.values()[0];

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(owner);
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.updateProjectStatus(10L, status);

        assertSame(project, result);

        verify(project).setProjectStatus(status);
        verify(projectRepository).save(project);
    }

    @Test
    void updateProjectStatus_shouldDenyAccess_whenCurrentUserIsNotOwner() {
        Project project = mock(Project.class);
        User owner = mock(User.class);
        User currentUser = mock(User.class);

        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(project.getOwner()).thenReturn(owner);
        when(owner.getId()).thenReturn(1L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);

        assertThrows(AccessDeniedException.class,
                () -> projectService.updateProjectStatus(10L, ProjectStatus.values()[0]));

        verify(projectRepository, never()).save(any());
    }
}