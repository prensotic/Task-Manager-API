package ru.loginov.dev.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FindAllTasksByProjectIdTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void findAllTasksByProjectId_shouldThrowException_whenProjectIdIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> taskService.findAllTasksByProjectId(0L));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void findAllTasksByProjectId_shouldDenyAccess_whenUserIsNotMember() {
        User user = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> taskService.findAllTasksByProjectId(10L));

        verify(taskRepository, never()).findAllTasksByProjectId(anyLong());
    }

    @Test
    void findAllTasksByProjectId_shouldReturnOnlyUnassignedTasks() {
        User user = mock(User.class);
        Task freeTask = mock(Task.class);
        Task assignedTask = mock(Task.class);
        User assignee = mock(User.class);

        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(true);

        when(taskRepository.findAllTasksByProjectId(10L)).thenReturn(List.of(freeTask, assignedTask));
        when(freeTask.getAssignee()).thenReturn(null);
        when(assignedTask.getAssignee()).thenReturn(assignee);

        List<Task> result = taskService.findAllTasksByProjectId(10L);

        assertEquals(1, result.size());
        assertSame(freeTask, result.get(0));
    }
}
