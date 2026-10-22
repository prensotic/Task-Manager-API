package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void createProject_shouldThrowException_whenProjectIsNull() {
        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(null));
        verifyNoInteractions(projectRepository, projectUserRepository);
    }

    @Test
    void createProject_shouldThrowException_whenTitleIsBlank() {
        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn(" ");

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_shouldThrowException_whenTitleIsNull() {
        Project project = mock(Project.class);
        when(project.getTitle()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_shouldThrowException_whenDescriptionIsBlank() {
        Project project = mock(Project.class);

        when(project.getTitle()).thenReturn("Test project");
        when(project.getDescription()).thenReturn(" ");

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_shouldThrowException_whenDescriptionIsNull() {
        Project project = mock(Project.class);

        when(project.getTitle()).thenReturn("Test project");
        when(project.getDescription()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_shouldThrowException_whenStatusIsNull() {
        Project project = mock(Project.class);

        when(project.getTitle()).thenReturn("Test project");
        when(project.getDescription()).thenReturn("Description");
        when(project.getProjectStatus()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> projectService.createProject(project));

        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_shouldSaveProjectAndAddOwnerAsMember() {
        Project project = mock(Project.class);
        User owner = mock(User.class);

        when(project.getTitle()).thenReturn("Test project");
        when(project.getDescription()).thenReturn("Description");
        when(project.getProjectStatus()).thenReturn(ru.loginov.dev.enums.ProjectStatus.values()[0]);
        when(userService.getCurrentUser()).thenReturn(owner);
        when(projectRepository.save(project)).thenReturn(project);

        Project result = projectService.createProject(project);

        assertSame(project, result);

        verify(project).setOwner(owner);
        verify(projectRepository).save(project);
        verify(projectUserRepository).save(any(ProjectUser.class));
    }
}
