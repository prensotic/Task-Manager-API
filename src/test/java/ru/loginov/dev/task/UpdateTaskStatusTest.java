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
import ru.loginov.dev.enums.TaskStatus;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateTaskStatusTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void updateTaskStatus_shouldThrowException_whenStatusIsNull() {
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTaskStatus(20L, null));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void updateTaskStatus_shouldDenyAccess_whenUserHasNoPermission() {
        Task task = mock(Task.class);
        User creator = mock(User.class);
        User currentUser = mock(User.class);
        Project project = mock(Project.class);
        User projectOwner = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(currentUser.getId()).thenReturn(3L);
        when(task.getOwner()).thenReturn(creator);
        when(creator.getId()).thenReturn(1L);
        when(task.getProject()).thenReturn(project);
        when(project.getOwner()).thenReturn(projectOwner);
        when(projectOwner.getId()).thenReturn(2L);
        when(task.getAssignee()).thenReturn(null);

        assertThrows(AccessDeniedException.class, () -> taskService.updateTaskStatus(20L, TaskStatus.DONE));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void updateTaskStatus_shouldSaveTask_whenCurrentUserIsCreator() {
        Task task = mock(Task.class);
        User creator = mock(User.class);

        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(userService.getCurrentUser()).thenReturn(creator);
        when(creator.getId()).thenReturn(1L);
        when(task.getOwner()).thenReturn(creator);
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.updateTaskStatus(20L, TaskStatus.DONE);

        assertSame(task, result);
        verify(task).setTaskStatus(TaskStatus.DONE);
        verify(taskRepository).save(task);
    }

}
