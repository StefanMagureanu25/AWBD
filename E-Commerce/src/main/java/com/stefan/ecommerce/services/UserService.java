package com.stefan.ecommerce.services;

import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.entities.Role;
import com.stefan.ecommerce.entities.Profile;
import com.stefan.ecommerce.repositories.UserRepository;
import com.stefan.ecommerce.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== USER REGISTRATION ====================

    /**
     * Register a new user with USER role
     */
    public User registerUser(String username, String email, String password,
                             String firstName, String lastName) {

        // Validate user doesn't already exist
        validateUserForRegistration(username, email);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Add default USER role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found"));
        user.addRole(userRole);

        return userRepository.save(user);
    }

    /**
     * Create admin user
     */
    public User createAdminUser(String username, String email, String password,
                                String firstName, String lastName) {
        // Validate user doesn't already exist
        validateUserForRegistration(username, email);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Add USER role (every user should have this)
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found"));
        user.addRole(userRole);

        // Add ADMIN role
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
        user.addRole(adminRole);

        return userRepository.save(user);
    }

    /**
     * Validate user data for registration
     */
    private void validateUserForRegistration(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
    }

    // ==================== USER AUTHENTICATION ====================

    /**
     * Find user by email or username (for login)
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmailOrUsername(String login) {
        return userRepository.findByEmailOrUsername(login);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Find user by username
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Validate user credentials
     */
    public boolean validateCredentials(String login, String password) {
        Optional<User> userOpt = findByEmailOrUsername(login);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.isActive() && passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    // ==================== USER MANAGEMENT ====================

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Find user by ID with profile
     */
    @Transactional(readOnly = true)
    public Optional<User> findByIdWithProfile(Long id) {
        return userRepository.findByIdWithProfile(id);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get all enabled users
     */
    @Transactional(readOnly = true)
    public List<User> findEnabledUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Get all disabled users
     */
    @Transactional(readOnly = true)
    public List<User> findDisabledUsers() {
        return userRepository.findByActiveFalse();
    }

    /**
     * Update user information
     */
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Update user basic info
     */
    public User updateUserInfo(Long userId, String firstName, String lastName, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Check if email is changing and if new email already exists
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    // ==================== ROLE MANAGEMENT ====================

    /**
     * Make user admin
     */
    public User makeUserAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!user.isAdmin()) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
            user.addRole(adminRole);
            userRepository.save(user);
        }

        return user;
    }

    /**
     * Remove admin rights from user
     */
    public User removeAdminRights(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.isAdmin()) {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));
            user.removeRole(adminRole);
            userRepository.save(user);
        }

        return user;
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            return Optional.empty();
        }
        return findByUsername(auth.getName());
    }

    /**
     * Check if current user is admin
     */
    @Transactional(readOnly = true)
    public boolean isCurrentUserAdmin() {
        return getCurrentUser()
                .map(User::isAdmin)
                .orElse(false);
    }

    /**
     * Require admin rights for current user
     */
    public void requireAdminRights() {
        if (!isCurrentUserAdmin()) {
            throw new SecurityException("Admin rights required for this operation");
        }
    }

    /**
     * Get all admin users
     */
    @Transactional(readOnly = true)
    public List<User> findAllAdmins() {
        return userRepository.findUsersByRoleName("ADMIN");
    }

    /**
     * Get all regular users (non-admin)
     */
    @Transactional(readOnly = true)
    public List<User> findAllRegularUsers() {
        return userRepository.findRegularUsers();
    }

    // ==================== PASSWORD MANAGEMENT ====================

    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Reset password (admin function)
     */
    public void resetPassword(Long userId, String newPassword) {
        // Only admins can reset passwords
        requireAdminRights();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ==================== USER STATUS MANAGEMENT ====================

    /**
     * Enable user account
     */
    public void enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    /**
     * Disable user account
     */
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    // ==================== AUTHORIZATION METHODS ====================

    /**
     * Validate user can manage target user
     */
    public void validateUserCanManageUser(Long targetUserId) {
        User currentUser = getCurrentUser()
                .orElseThrow(() -> new SecurityException("Authentication required"));

        // Admins can manage anyone
        if (currentUser.isAdmin()) {
            return;
        }

        // Users can only manage themselves
        if (!currentUser.getId().equals(targetUserId)) {
            throw new SecurityException("Insufficient permissions to manage this user");
        }
    }

    /**
     * Check if user can manage products
     */
    public boolean canCurrentUserManageProducts() {
        return isCurrentUserAdmin();
    }

    /**
     * Check if user can manage users
     */
    public boolean canCurrentUserManageUsers() {
        return isCurrentUserAdmin();
    }

    // ==================== USER SEARCH ====================

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String firstName, String lastName) {
        return userRepository.findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
                firstName, lastName);
    }

    /**
     * Search users by email
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByEmail(String email) {
        return userRepository.findByEmailContainingIgnoreCase(email);
    }

    // ==================== USER STATISTICS ====================

    /**
     * Get total user count
     */
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    /**
     * Get enabled user count
     */
    @Transactional(readOnly = true)
    public long getEnabledUserCount() {
        return userRepository.countByActiveTrue();
    }

    /**
     * Get admin user count
     */
    @Transactional(readOnly = true)
    public long getAdminUserCount() {
        return userRepository.countUsersByRoleName("ADMIN");
    }

    /**
     * Get regular user count
     */
    @Transactional(readOnly = true)
    public long getRegularUserCount() {
        return getTotalUserCount() - getAdminUserCount();
    }

    /**
     * Get users who have placed orders
     */
    @Transactional(readOnly = true)
    public List<User> getUsersWithOrders() {
        return userRepository.findUsersWithOrders();
    }

    // ==================== VALIDATION METHODS ====================

    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Validate email format
     */
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        // Password should be at least 8 characters long
        // You can add more complex validation here
        return password != null && password.length() >= 8;
    }

    // ==================== DELETE USER ====================

    /**
     * Delete user (soft delete by disabling)
     */
    public void deleteUser(Long userId) {
        disableUser(userId);
    }

    /**
     * Permanently delete user (use with caution - admin only)
     */
    public void permanentlyDeleteUser(Long userId) {
        requireAdminRights();

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    // ==================== ROLE INITIALIZATION ====================

    /**
     * Initialize default roles if they don't exist
     */
    @Transactional
    public void initializeDefaultRoles() {
        createRoleIfNotExists("USER", "Regular user with basic permissions");
        createRoleIfNotExists("ADMIN", "Administrator with full system access");
    }

    /**
     * Create role if it doesn't exist
     */
    private void createRoleIfNotExists(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role(roleName, description);
            roleRepository.save(role);
        }
    }

    /**
     * Check if a user is admin by username
     */
    @Transactional(readOnly = true)
    public boolean isAdmin(String username) {
        return findByUsername(username)
                .map(User::isAdmin)
                .orElse(false);
    }
}