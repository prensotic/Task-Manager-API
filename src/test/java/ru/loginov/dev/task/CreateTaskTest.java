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
import ru.loginov.dev.enums.TaskPriority;
import ru.loginov.dev.enums.TaskStatus;
import ru.loginov.dev.exceptions.ProjectNotFoundException;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.services.TaskService;
import ru.loginov.dev.services.UserService;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTaskTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProjectUserRepository projectUserRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_shouldThrowException_whenTaskIsNull() {
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(null));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_shouldThrowException_whenTitleIsBlank() {
        Task task = mock(Task.class);

        when(task.getTitle()).thenReturn("   ");

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_shouldThrowException_whenTitleIsNull() {
        Task task = mock(Task.class);

        when(task.getTitle()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_shouldThrowException_whenDescriptionIsBlank() {
        Task task = mock(Task.class);

        when(task.getTitle()).thenReturn("Test task");
        when(task.getDescription()).thenReturn(" ");

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));

        verifyNoInteractions(taskRepository);
    }

    @Test
    void createTask_shouldThrowException_whenProjectDoesNotExist() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);

        when(task.getTitle()).thenReturn("Test task");
        when(task.getDescription()).thenReturn("Description");
        when(task.getTaskStatus()).thenReturn(TaskStatus.TODO);
        when(task.getTaskPriority()).thenReturn(TaskPriority.MEDIUM);
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(projectRepository.existsProjectById(10L)).thenReturn(false);

        assertThrows(ProjectNotFoundException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_shouldThrowException_whenUserIsNotProjectMember() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        User user = mock(User.class);

        when(task.getTitle()).thenReturn("Test task");
        when(task.getDescription()).thenReturn("Description");
        when(task.getTaskStatus()).thenReturn(TaskStatus.TODO);
        when(task.getTaskPriority()).thenReturn(TaskPriority.MEDIUM);
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(projectRepository.existsProjectById(10L)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> taskService.createTask(task));

        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTask_shouldSaveTask_whenDataIsValid() {
        Task task = mock(Task.class);
        Project project = mock(Project.class);
        User user = mock(User.class);

        when(task.getTitle()).thenReturn("Test task");
        when(task.getDescription()).thenReturn("Description");
        when(task.getTaskStatus()).thenReturn(TaskStatus.TODO);
        when(task.getTaskPriority()).thenReturn(TaskPriority.MEDIUM);
        when(task.getProject()).thenReturn(project);
        when(project.getId()).thenReturn(10L);
        when(projectRepository.existsProjectById(10L)).thenReturn(true);
        when(userService.getCurrentUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(projectUserRepository.existsByProjectIdAndUserId(10L, 1L)).thenReturn(true);
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.createTask(task);

        assertSame(task, result);
        verify(task).setOwner(user);
        verify(taskRepository).save(task);
    }
}