package ru.loginov.dev.services;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.loginov.dev.dto.task.TaskRequest;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.entities.User;
import ru.loginov.dev.enums.TaskPriority;
import ru.loginov.dev.enums.TaskStatus;
import ru.loginov.dev.exceptions.ProjectNotFoundException;
import ru.loginov.dev.exceptions.TaskNotFoundException;
import ru.loginov.dev.exceptions.UserNotFoundException;
import ru.loginov.dev.repositories.ProjectRepository;
import ru.loginov.dev.repositories.ProjectUserRepository;
import ru.loginov.dev.repositories.TaskRepository;
import ru.loginov.dev.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,
                       UserService userService,
                       ProjectUserRepository projectUserRepository, ProjectService projectService, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.projectUserRepository = projectUserRepository;
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public List<Task> findAllTasksByProjectId(Long projectId){
        if(projectId == null || projectId <= 0)
            throw new IllegalArgumentException("Project`s ID must not be null or negative");

        User user = userService.getCurrentUser();

        if(!projectUserRepository.existsByProjectIdAndUserId(projectId, user.getId()))
            throw new AccessDeniedException("Access is blocked");

        return taskRepository.findAllTasksByProjectId(projectId)
                .stream()
                .filter(t -> t.getAssignee() == null)
                .toList();
    }

    public List<Task> findAllMyTasks(){
        return taskRepository.findAllTasksByAssigneeId(userService.getCurrentUser().getId());
    }

    public List<Task> findAllTasksByProjectIdAndAssigneeId(Long projectId, Long assigneeId){
        if(projectId == null || projectId <= 0)
            throw new IllegalArgumentException("Project`s ID must not be null or negative");

        if(assigneeId == null || assigneeId <= 0)
            throw new IllegalArgumentException("Assignee`s ID must not be null or negative");

        User currentUser = userService.getCurrentUser();
        Project project = projectService.findProjectById(projectId);

        /*
        * Проверяем что задачи пользователя в данном проекте могут посмотреть только
        * владелец проекта или же сам пользователь
        */
        if(!project.getOwner().getId().equals(currentUser.getId()) && !currentUser.getId().equals(assigneeId))
            throw new AccessDeniedException("Access is blocked");

        return taskRepository.findAllTasksByProjectIdAndAssigneeId(projectId, assigneeId);
    }

    private Optional<Task> findOptionalTaskById(Long id){
        if(id == null || id <= 0)
            throw new IllegalArgumentException("Task`s ID must not be null or negative");

        return taskRepository.findById(id);
    }

    private Task findTaskById(Long id){
        return findOptionalTaskById(id)
                .orElseThrow(() ->
                        new TaskNotFoundException("Task with ID: " + id + " not found"));
    }

    public Task findByTaskIdAndProjectId(Long taskId, Long projectId){
        if(taskId == null || taskId <= 0)
            throw new IllegalArgumentException("Task`s ID must not be null or negative");

        if(projectId == null || projectId <= 0)
            throw new IllegalArgumentException("Project`s ID must not be null or negative");

        if(!projectUserRepository.existsByProjectIdAndUserId(projectId, userService.getCurrentUser().getId()))
            throw new AccessDeniedException("Access os blocked");

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId);

        if (task == null)
            throw new TaskNotFoundException("Task not found");

        if(task.getAssignee() != null)
            throw new AccessDeniedException("Access is blocked");

        return task;
    }

    public Task createTask(Task task){
        if(task == null)
            throw new IllegalArgumentException("Task must not be null");

        if(task.getTitle() == null || task.getTitle().isBlank())
            throw new IllegalArgumentException("Task`s title must be valid");

        if(task.getDescription() == null || task.getDescription().isBlank())
            throw new IllegalArgumentException("Task`s description must be valid");

        if(task.getTaskStatus() == null)
            throw new IllegalArgumentException("Task`s status must not be null");

        if(task.getTaskPriority() == null)
            throw new IllegalArgumentException("Task`s priority must not be null");

        if(task.getProject() == null)
            throw new IllegalArgumentException("Task`s project must not be null");

        /*
        *Проверка существует ли проект в бд
        */
        if(!projectRepository.existsProjectById(task.getProject().getId()))
            throw new ProjectNotFoundException("Task`s project not found");

        User owner = userService.getCurrentUser();

        /*
        *Проверка на то что создатель состоит в участниках проекта
        */
        if (!projectUserRepository.existsByProjectIdAndUserId(task.getProject().getId(), owner.getId()))
            throw new AccessDeniedException("Access is denied");

        task.setOwner(owner);

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long id, TaskStatus taskStatus){
        if(taskStatus == null)
            throw new IllegalArgumentException("Task`s status must not be null");

        Task task = findTaskById(id);
        User user = userService.getCurrentUser();

        /*
        * Доступ к обновлению задачи имеют только ее создатель, создатель проект и исполняющий задачу
        */
        if(!task.getOwner().getId().equals(user.getId()) &&
                !task.getProject().getOwner().getId().equals(user.getId()) && (task.getAssignee() == null
                || !task.getAssignee().getId().equals(user.getId())))
            throw new AccessDeniedException("Access is blocked");

        task.setTaskStatus(taskStatus);

        return taskRepository.save(task);
    }

    public Task updateTaskPriority(Long id, TaskPriority taskPriority){
        if(taskPriority == null)
            throw new IllegalArgumentException("Task`s priority must not be null");

        Task task = findTaskById(id);
        User user = userService.getCurrentUser();

        if(!task.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Access id blocked");

        task.setTaskPriority(taskPriority);

        return taskRepository.save(task);
    }

    public Task updateTask(Long id, TaskRequest updateTaskData){
        if(updateTaskData == null)
            throw new IllegalArgumentException("Task`s data must not be null");

        Task task = findTaskById(id);
        User user = userService.getCurrentUser();

        if(!task.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Access id blocked");

        if (updateTaskData.getTitle() == null || updateTaskData.getTitle().isBlank())
            throw new IllegalArgumentException("Task`s title must not be blank");

        if (updateTaskData.getDescription() == null || updateTaskData.getDescription().isBlank())
            throw new IllegalArgumentException("Task`s description must not be blank");

        task.setTitle(updateTaskData.getTitle());
        task.setDescription(updateTaskData.getDescription());
        task.setDueDate(updateTaskData.getDueDate());

        return taskRepository.save(task);
    }

    public void deleteTask(Long id){
        Task task = findTaskById(id);
        User user = userService.getCurrentUser();

        if(!task.getOwner().getId().equals(user.getId()))
            throw new AccessDeniedException("Access id blocked");

        taskRepository.delete(task);
    }

    @Transactional
    public Task setUserForTask(Long taskId, Long userId){
        Task task = findTaskById(taskId);
        User owner = userService.getCurrentUser();
        Project project = projectService.findProjectById(task.getProject().getId());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (task.getAssignee() != null)
            throw new IllegalStateException("Task already has an assignee");

        if(!project.getOwner().getId().equals(owner.getId()))
            throw new AccessDeniedException("Access is blocked");

        if(!projectUserRepository.existsByProjectIdAndUserId(project.getId(), user.getId()))
            throw new AccessDeniedException("Access is blocked");

        task.setAssignee(user);

        return taskRepository.save(task);
    }
}