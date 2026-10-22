package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.exceptions.ProjectNotFoundException;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.services.ProjectService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindProjectByIdTest {
    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void findProjectById_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> projectService.findProjectById(null));
        verifyNoInteractions(projectRepository);
    }

    @Test
    void findProjectById_shouldThrowException_whenIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> projectService.findProjectById(0L));
        assertThrows(IllegalArgumentException.class, () -> projectService.findProjectById(-1L));
        verifyNoInteractions(projectRepository);
    }

    @Test
    void findProjectById_shouldThrowException_whenProjectDoesNotExist() {
        when(projectRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> projectService.findProjectById(10L));
    }

    @Test
    void findProjectById_shouldReturnProject_whenProjectExists() {
        Project project = mock(Project.class);
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        Project result = projectService.findProjectById(10L);
        assertSame(project, result);
    }
}
