package ru.loginov.dev.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.loginov.dev.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}