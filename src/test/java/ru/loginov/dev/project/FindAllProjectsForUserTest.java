package ru.loginov.dev.project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.ProjectUser;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllProjectsForUserTest {
    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void findAllProjectsForUser_shouldReturnProjectsOfCurrentUser() {
        User user = mock(User.class);
        Project project = mock(Project.class);
        ProjectUser projectUser = mock(ProjectUser.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.findAllByUserId(1L)).thenReturn(List.of(projectUser));
        when(projectUser.getProject()).thenReturn(project);

        List<Project> result = projectService.findAllProjectsForUser();

        assertEquals(1, result.size());
        assertSame(project, result.getFirst());

        verify(projectUserRepository).findAllByUserId(1L);
    }

    @Test
    void findAllProjectsForUser_shouldReturnEmptyList_whenUserHasNoProjects() {
        User user = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.findAllByUserId(1L)).thenReturn(List.of());

        List<Project> result = projectService.findAllProjectsForUser();

        assertTrue(result.isEmpty());
    }
}