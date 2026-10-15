package ru.loginov.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.loginov.dev.entities.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllProjectsByOwnerId(Long ownerId);
}