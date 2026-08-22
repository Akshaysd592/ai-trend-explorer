package com.aitrend.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class User {
    private final Long id;
    private final String email;
    private final String passwordHash;
    private final String firstName;
    private final String lastName;
    private final Set<Role> roles;
    private final boolean enabled;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public User(Long id, String email, String passwordHash, String firstName, String lastName,
                Set<Role> roles, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = Objects.requireNonNull(email, "Email cannot be null").toLowerCase().trim();
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles != null && !roles.isEmpty() ? Set.copyOf(roles) : Set.of(Role.ROLE_USER);
        this.enabled = enabled;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Set<Role> getRoles() { return roles; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
