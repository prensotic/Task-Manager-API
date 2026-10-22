package ru.loginov.dev.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.exceptions.UserNotFoundException;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.repositories.UserRepository;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SetUserForTaskTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void setUserForTask_shouldThrowException_whenUserDoesNotExist() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        User currentUser = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(projectService.findProjectById(10L)).thenReturn(project);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> taskService.setUserForTask(20L, 2L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void setUserForTask_shouldThrowException_whenTaskAlreadyAssigned() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        User assignee = mock(User.class);
        User currentUser = mock(User.class);
        User user = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(task.getAssignee()).thenReturn(assignee);
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(projectService.findProjectById(10L)).thenReturn(project);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> taskService.setUserForTask(20L, 2L));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void setUserForTask_shouldDenyAccess_whenCurrentUserIsNotProjectOwner() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        User currentUser = mock(User.class);
        User projectOwner = mock(User.class);
        User assignee = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(task.getAssignee()).thenReturn(null);
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(projectService.findProjectById(10L)).thenReturn(project);
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignee));
        when(project.getOwner()).thenReturn(projectOwner);
        when(projectOwner.getId()).thenReturn(1L);
        when(currentUser.getId()).thenReturn(3L);

        assertThrows(AccessDeniedException.class, () -> taskService.setUserForTask(20L, 2L));

        verify(taskRepository, never()).save(any());
    }
}
