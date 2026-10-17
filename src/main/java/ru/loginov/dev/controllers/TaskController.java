package ru.loginov.dev.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.loginov.dev.dto.task.*;
import ru.loginov.dev.entities.Project;
import ru.loginov.dev.entities.Task;
import ru.loginov.dev.services.ProjectService;
import ru.loginov.dev.services.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ProjectService projectService;

    public TaskController(TaskService taskService, ProjectService projectService) {
        this.taskService = taskService;
        this.projectService = projectService;
    }

    @GetMapping("/my-tasks")
    public List<TaskResponse> findAllMyTasks(){
        return taskService.findAllMyTasks()
                .stream()
                .map(t -> new TaskResponse(t.getTitle(),
                        t.getDescription(),
                        t.getDueDate(),
                        t.getTaskStatus(),
                        t.getTaskPriority(),
                        t.getProject().getId()))
                .toList();
    }

    @GetMapping("/project/{projectId}/tasks-for-project/user/{userId}")
    public List<TaskResponse> findMyTaskInProject(@PathVariable("projectId") Long projectId,
                                          @PathVariable("userId") Long userId){
        return taskService.findAllTasksByProjectIdAndAssigneeId(projectId, userId).stream()
                .map(t -> new TaskResponse(t.getTitle(),
                        t.getDescription(),
                        t.getDueDate(),
                        t.getTaskStatus(),
                        t.getTaskPriority(),
                        t.getProject().getId()))
                .toList();
    }

    @GetMapping("/project/{projectId}/free-tasks")
    public List<TaskResponse> findFreeTasksForProject(@PathVariable("projectId") Long projectId){
        return taskService.findAllTasksByProjectId(projectId).stream()
                .map(t -> new TaskResponse(t.getTitle(),
                        t.getDescription(),
                        t.getDueDate(),
                        t.getTaskStatus(),
                        t.getTaskPriority(),
                        t.getProject().getId()))
                .toList();
    }

    @GetMapping("/project/{projectId}/task/{taskId}")
    public TaskResponse findFreeTaskForProject(@PathVariable("projectId") Long projectId,
                                       @PathVariable("taskId") Long taskId){
        Task task = taskService.findByTaskIdAndProjectId(taskId, projectId);
        return new TaskResponse(task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getTaskStatus(),
                task.getTaskPriority(),
                task.getProject().getId());
    }

    @PostMapping("/project/{projectId}/create-task")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequest taskRequest){
        Project project = projectService.findProjectById(projectId);
        Task savedTask = taskService.createTask(new Task(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.getTaskStatus(),
                taskRequest.getTaskPriority(),
                taskRequest.getDueDate(),
                null,
                project
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(new TaskResponse(
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getDueDate(),
                savedTask.getTaskStatus(),
                savedTask.getTaskPriority(),
                savedTask.getProject().getId()
        ));
    }

    @PutMapping("/task-update/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable("id") Long id,
                                                   @Valid @RequestBody TaskUpdateRequest taskRequest){
        Task updatedTask = taskService.updateTask(id, new TaskRequest(
                taskRequest.getTitle(),
                taskRequest.getDescription(),
                taskRequest.getDueDate()
        ));
        return ResponseEntity.status(HttpStatus.OK).body(
                new TaskResponse(
                        updatedTask.getTitle(),
                        updatedTask.getDescription(),
                        updatedTask.getDueDate(),
                        updatedTask.getTaskStatus(),
                        updatedTask.getTaskPriority(),
                        updatedTask.getProject().getId()
                )
        );
    }

    @PutMapping("/task-update/{id}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long id,
                                                         @Valid @RequestBody TaskStatusRequest taskStatusRequest){
        Task updatedTask = taskService.updateTaskStatus(id, taskStatusRequest.getTaskStatus());
        return ResponseEntity.status(HttpStatus.OK).body(
                new TaskResponse(
                        updatedTask.getTitle(),
                        updatedTask.getDescription(),
                        updatedTask.getDueDate(),
                        updatedTask.getTaskStatus(),
                        updatedTask.getTaskPriority(),
                        updatedTask.getProject().getId()
                )
        );
    }

    @PutMapping("/task-update/{id}/priority")
    public ResponseEntity<TaskResponse> updateTaskPriority(@PathVariable Long id,
                                                           @Valid @RequestBody
                                                           TaskPriorityRequest taskPriorityRequest){
        Task updatedTask = taskService.updateTaskPriority(id, taskPriorityRequest.getTaskPriority());
        return ResponseEntity.status(HttpStatus.OK).body(
                new TaskResponse(
                        updatedTask.getTitle(),
                        updatedTask.getDescription(),
                        updatedTask.getDueDate(),
                        updatedTask.getTaskStatus(),
                        updatedTask.getTaskPriority(),
                        updatedTask.getProject().getId()
                )
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/task-update/{id}/set-user/{userId}")
    public ResponseEntity<TaskResponse> setUserForTask(@PathVariable("id") Long id,
                                                       @PathVariable("userId") Long userId){
        Task updatedTask = taskService.setUserForTask(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new TaskResponse(
                        updatedTask.getTitle(),
                        updatedTask.getDescription(),
                        updatedTask.getDueDate(),
                        updatedTask.getTaskStatus(),
                        updatedTask.getTaskPriority(),
                        updatedTask.getProject().getId()
                )
        );
    }
}