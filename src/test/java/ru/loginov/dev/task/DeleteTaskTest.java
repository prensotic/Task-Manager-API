package ru.loginov.dev.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteTaskTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void deleteTask_shouldDenyAccess_whenUserIsNotCreator() {
        Task task = mock(Task.class);
        User creator = mock(User.class);
        User currentUser = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);
        when(task.getOwner()).thenReturn(creator);
        when(creator.getId()).thenReturn(1L);

        assertThrows(AccessDeniedException.class, () -> taskService.deleteTask(20L));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void deleteTask_shouldDeleteTask_whenUserIsCreator() {
        Task task = mock(Task.class);
        User creator = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        when(userService.getCurrentUser()).thenReturn(creator);
        when(creator.getId()).thenReturn(1L);
        when(task.getOwner()).thenReturn(creator);
        assertDoesNotThrow(() -> taskService.deleteTask(20L));

        verify(taskRepository).delete(task);
    }
}
