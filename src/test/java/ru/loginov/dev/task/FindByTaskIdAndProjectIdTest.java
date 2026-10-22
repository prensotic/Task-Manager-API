package ru.loginov.dev.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.exceptions.TaskNotFoundException;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FindByTaskIdAndProjectIdTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void findByTaskIdAndProjectId_shouldThrowException_whenTaskDoesNotExist() {
        User user = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(20L, 10L)).thenReturn(null);

        assertThrows(TaskNotFoundException.class, () -> taskService.findByTaskIdAndProjectId(20L, 10L));
    }

    @Test
    void findByTaskIdAndProjectId_shouldDenyAccess_whenTaskIsAssigned() {
        User user = mock(User.class);
        Task task = mock(Task.class);
        User assignee = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(true);
        when(taskRepository.findByIdAndProjectId(20L, 10L)).thenReturn(task);
        when(task.getAssignee()).thenReturn(assignee);

        assertThrows(AccessDeniedException.class, () -> taskService.findByTaskIdAndProjectId(20L, 10L));
    }
}
