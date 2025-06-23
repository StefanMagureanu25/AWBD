package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (for login)
    Optional<User> findByEmail(String email);

    // Find user by username (for login)
    Optional<User> findByUsername(String username);

    // Check if email already exists (for registration validation)
    boolean existsByEmail(String email);

    // Check if username already exists (for registration validation)
    boolean existsByUsername(String username);

    // Find user by email or username (flexible login)
    @Query("SELECT u FROM User u WHERE u.email = :login OR u.username = :login")
    Optional<User> findByEmailOrUsername(@Param("login") String login);

    // Find all enabled users
    List<User> findByEnabledTrue();

    // Find all disabled users
    List<User> findByEnabledFalse();

    // Find users by first name and last name (for admin search)
    List<User> findByFirstNameContainingIgnoreCaseAndLastNameContainingIgnoreCase(
            String firstName, String lastName);

    // Find users by email containing text (for admin search)
    List<User> findByEmailContainingIgnoreCase(String email);

    // Custom query to find users with their profiles
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findByIdWithProfile(@Param("id") Long id);

    // Find users who have placed orders
    @Query("SELECT DISTINCT u FROM User u JOIN u.orders o")
    List<User> findUsersWithOrders();

    // Count enabled users
    long countByEnabledTrue();

    // Find users by role name (e.g., ADMIN)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    // Find users who are not admins (regular users)
    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = 'ADMIN')")
    List<User> findRegularUsers();

    // Count users by role name
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRoleName(@Param("roleName") String roleName);
}