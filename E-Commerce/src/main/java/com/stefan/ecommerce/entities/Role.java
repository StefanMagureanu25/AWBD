package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-Many relationship with User
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Constructors
    public Role() {}

    public Role(String name) {
        this.name = name;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Basic Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // ==================== RELATIONSHIP HELPER METHODS ====================

    /**
     * Add a user to this role
     */
    public void addUser(User user) {
        this.users.add(user);
        user.getRoles().add(this);
    }

    /**
     * Remove a user from this role
     */
    public void removeUser(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }

    /**
     * Check if this role contains a specific user
     */
    public boolean hasUser(User user) {
        return users.contains(user);
    }

    /**
     * Get count of users with this role
     */
    public int getUserCount() {
        return users != null ? users.size() : 0;
    }

    /**
     * Check if role has any users
     */
    public boolean hasUsers() {
        return users != null && !users.isEmpty();
    }

    // ==================== BUSINESS LOGIC METHODS ====================

    /**
     * Check if this is an admin role
     */
    public boolean isAdminRole() {
        return name != null && name.equalsIgnoreCase("ADMIN");
    }

    /**
     * Check if this is a user role
     */
    public boolean isUserRole() {
        return name != null && name.equalsIgnoreCase("USER");
    }

    /**
     * Get display name (formatted role name)
     */
    public String getDisplayName() {
        if (name == null) return "Unknown Role";

        switch (name.toUpperCase()) {
            case "ADMIN": return "Administrator";
            case "USER": return "User";
            default: return name;
        }
    }

    /**
     * Get role color for UI (can be used in frontend)
     */
    public String getRoleColor() {
        if (name == null) return "gray";

        switch (name.toUpperCase()) {
            case "ADMIN": return "red";
            case "USER": return "blue";
            default: return "gray";
        }
    }

    /**
     * Get role icon for UI (can be used in frontend)
     */
    public String getRoleIcon() {
        if (name == null) return "user";

        switch (name.toUpperCase()) {
            case "ADMIN": return "shield";
            case "USER": return "user";
            default: return "user";
        }
    }

    // ==================== STATIC FACTORY METHODS ====================

    /**
     * Create a new USER role
     */
    public static Role createUserRole() {
        return new Role("USER", "Regular user with basic permissions");
    }

    /**
     * Create a new ADMIN role
     */
    public static Role createAdminRole() {
        return new Role("ADMIN", "Administrator with full system access");
    }

    // ==================== EQUALS, HASHCODE, AND TOSTRING ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return getId() != null && getId().equals(role.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userCount=" + getUserCount() +
                ", createdAt=" + createdAt +
                '}';
    }
}