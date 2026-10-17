package ru.loginov.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.loginov.dev.entities.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllTasksByProjectId(Long projectId);
    List<Task> findAllTasksByAssigneeId(Long assigneeId);
    List<Task> findAllTasksByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
}