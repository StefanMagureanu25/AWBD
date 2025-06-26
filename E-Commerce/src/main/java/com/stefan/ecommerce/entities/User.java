package com.stefan.ecommerce.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @NotNull(message = "Enabled status is required")
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @NotNull(message = "Active status is required")
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many-to-Many relationship with Role
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // One-to-One relationship with Profile
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    // One-to-Many relationship with Address
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Address> addresses = new HashSet<>();

    // One-to-Many relationship with Order
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Order> orders = new HashSet<>();

    // One-to-Many relationship with Review
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    // Constructors
    public User() {}

    public User(String username, String email, String password, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.enabled = true;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled != null && enabled;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    // Role-related Getters and Setters
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    // Relationship Getters and Setters
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    // ==================== ROLE MANAGEMENT METHODS ====================

    /**
     * Add a role to this user
     */
    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    /**
     * Remove a role from this user
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }

    /**
     * Check if user has a specific role by name (case-insensitive)
     */
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    /**
     * Check if user is an administrator
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if user is a regular user
     */
    public boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Check if user has admin rights (alias for isAdmin)
     */
    public boolean hasAdminRights() {
        return isAdmin();
    }

    /**
     * Get all role names as a set of strings
     */
    public Set<String> getRoleNames() {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Get primary role (ADMIN if admin, otherwise USER)
     */
    public Role getPrimaryRole() {
        if (isAdmin()) {
            return roles.stream()
                    .filter(role -> role.getName().equalsIgnoreCase("ADMIN"))
                    .findFirst()
                    .orElse(null);
        }
        return roles.stream()
                .filter(role -> role.getName().equalsIgnoreCase("USER"))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get primary role name
     */
    public String getPrimaryRoleName() {
        return isAdmin() ? "ADMIN" : "USER";
    }

    // ==================== RELATIONSHIP HELPER METHODS ====================

    public void addAddress(Address address) {
        this.addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        this.addresses.remove(address);
        address.setUser(null);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        order.setUser(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setUser(null);
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        review.setUser(null);
    }

    // ==================== BUSINESS LOGIC METHODS ====================

    /**
     * Get user's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Get user's display name (full name or username if name not available)
     */
    public String getDisplayName() {
        if (firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty()) {
            return getFullName();
        }
        return username;
    }

    /**
     * Check if user has a profile
     */
    public boolean hasProfile() {
        return profile != null;
    }

    /**
     * Check if user has any orders
     */
    public boolean hasOrders() {
        return orders != null && !orders.isEmpty();
    }

    /**
     * Get order count
     */
    public int getOrderCount() {
        return orders != null ? orders.size() : 0;
    }

    /**
     * Check if user has any reviews
     */
    public boolean hasReviews() {
        return reviews != null && !reviews.isEmpty();
    }

    /**
     * Get review count
     */
    public int getReviewCount() {
        return reviews != null ? reviews.size() : 0;
    }

    /**
     * Check if user has any addresses
     */
    public boolean hasAddresses() {
        return addresses != null && !addresses.isEmpty();
    }

    /**
     * Get address count
     */
    public int getAddressCount() {
        return addresses != null ? addresses.size() : 0;
    }

    /**
     * Check if user account is active (enabled and has at least one role)
     */
    public boolean isActive() {
        return active != null && active;
    }

    /**
     * Get user status as string
     */
    public String getStatus() {
        if (!isEnabled()) {
            return "DISABLED";
        }
        if (isAdmin()) {
            return "ADMIN";
        }
        if (isUser()) {
            return "USER";
        }
        return "UNKNOWN";
    }

    // ==================== AUTHORIZATION HELPER METHODS ====================

    /**
     * Check if user can manage other users
     */
    public boolean canManageUsers() {
        return isAdmin();
    }

    /**
     * Check if user can manage products
     */
    public boolean canManageProducts() {
        return isAdmin();
    }

    /**
     * Check if user can manage orders
     */
    public boolean canManageOrders() {
        return isAdmin();
    }

    /**
     * Check if user can view admin dashboard
     */
    public boolean canAccessAdminDashboard() {
        return isAdmin();
    }

    // ==================== EQUALS, HASHCODE, AND TOSTRING ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getId() != null && getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", enabled=" + enabled +
                ", roles=" + getRoleNames() +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}