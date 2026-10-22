package ru.loginov.dev.task;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.TaskPriority;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskPriorityTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void updateTaskPriority_shouldDenyAccess_whenUserIsNotCreator() {
        Task task = mock(Task.class);
        User creator = mock(User.class);
        User currentUser = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(2L);
        when(task.getOwner()).thenReturn(creator);
        when(creator.getId()).thenReturn(1L);

        assertThrows(AccessDeniedException.class, () -> taskService.updateTaskPriority(20L, TaskPriority.HIGH));

        verify(taskRepository, never()).save(any());
    }
}
