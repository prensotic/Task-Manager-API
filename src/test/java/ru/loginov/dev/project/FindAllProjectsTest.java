package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllProjectsTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void findAllProjects_shouldReturnCurrentUsersProjects() {
        User user = mock(User.class);
        Project project1 = mock(Project.class);
        Project project2 = mock(Project.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectRepository.findAllProjectsByOwnerId(1L)).thenReturn(List.of(project1, project2));

        List<Project> result = projectService.findAllProjects();

        assertEquals(2, result.size());
        assertSame(project1, result.get(0));
        assertSame(project2, result.get(1));

        verify(projectRepository).findAllProjectsByOwnerId(1L);
    }

    @Test
    void findAllProjects_shouldReturnEmptyList_whenUserHasNoOwnedProjects() {
        User user = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectRepository.findAllProjectsByOwnerId(1L)).thenReturn(List.of());

        List<Project> result = projectService.findAllProjects();

        assertTrue(result.isEmpty());
    }
}