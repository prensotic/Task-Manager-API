package ru.loginov.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.loginov.dev.entities.ProjectUser;

import java.util.List;

public interface ProjectUserRepository extends JpaRepository<ProjectUser, Long> {
    List<ProjectUser> findAllByProjectId(Long projectId);
    List<ProjectUser> findAllByUserId(Long userId);
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}