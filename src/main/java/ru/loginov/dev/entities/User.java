package ru.loginov.dev.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.loginov.dev.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 16, unique = true)
    private String username;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, name = "registration_date")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "owner")
    private List<Task> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "owner")
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ProjectUser> projectMemberships = new ArrayList<>();

    public User() {}

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Project> getProjects() {
        return projects;
    }
}