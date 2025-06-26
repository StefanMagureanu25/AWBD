package com.stefan.ecommerce.repositories;

import com.stefan.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    // Find all active users
    List<User> findByActiveTrue();

    // Find all inactive users
    List<User> findByActiveFalse();

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

    // Count active users
    long countByActiveTrue();

    // Find users by role name (e.g., ADMIN)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);

    // Find users who are not admins (regular users)
    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT 1 FROM u.roles r WHERE r.name = 'ADMIN')")
    List<User> findRegularUsers();

    // Count users by role name
    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countUsersByRoleName(@Param("roleName") String roleName);

    List<User> findByActiveOrderByCreatedAtDesc(Boolean active);
    List<User> findByActiveOrderByCreatedAtAsc(Boolean active);
    List<User> findByActiveOrderByUsernameAsc(Boolean active);
    List<User> findByActiveOrderByUsernameDesc(Boolean active);
    List<User> findByActiveOrderByEmailAsc(Boolean active);
    List<User> findByActiveOrderByEmailDesc(Boolean active);

    List<User> findByCreatedAtAfter(LocalDateTime date);
    List<User> findByCreatedAtBefore(LocalDateTime date);
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<User> findByUpdatedAtAfter(LocalDateTime date);
    List<User> findByUpdatedAtBefore(LocalDateTime date);
    List<User> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findByUsernameContainingIgnoreCaseAndActiveTrue(String username);
    List<User> findByUsernameContainingIgnoreCaseAndActiveFalse(String username);
    long countByActiveFalse();
    long countByCreatedAtAfter(LocalDateTime date);
    long countByCreatedAtBefore(LocalDateTime date);
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    long countByUpdatedAtAfter(LocalDateTime date);
    long countByUpdatedAtBefore(LocalDateTime date);
    long countByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true")
    List<User> findByRoleNameAndActiveTrue(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false")
    List<User> findByRoleNameAndActiveFalse(@Param("roleName") String roleName);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName")
    long countByRoleName(@Param("roleName") String roleName);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true")
    long countByRoleNameAndActiveTrue(@Param("roleName") String roleName);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false")
    long countByRoleNameAndActiveFalse(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    List<User> findByUsernameOrEmailContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.active = true")
    List<User> findByUsernameOrEmailContainingIgnoreCaseAndActiveTrue(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.active = false")
    List<User> findByUsernameOrEmailContainingIgnoreCaseAndActiveFalse(@Param("keyword") String keyword);

    @Query("SELECT COUNT(u) FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword%")
    long countByUsernameOrEmailContainingIgnoreCase(@Param("keyword") String keyword);

    @Query("SELECT COUNT(u) FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.active = true")
    long countByUsernameOrEmailContainingIgnoreCaseAndActiveTrue(@Param("keyword") String keyword);

    @Query("SELECT COUNT(u) FROM User u WHERE (u.username LIKE %:keyword% OR u.email LIKE %:keyword%) AND u.active = false")
    long countByUsernameOrEmailContainingIgnoreCaseAndActiveFalse(@Param("keyword") String keyword);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findAllOrderByCreatedAtDesc();

    @Query("SELECT u FROM User u ORDER BY u.createdAt ASC")
    List<User> findAllOrderByCreatedAtAsc();

    @Query("SELECT u FROM User u ORDER BY u.updatedAt DESC")
    List<User> findAllOrderByUpdatedAtDesc();

    @Query("SELECT u FROM User u ORDER BY u.updatedAt ASC")
    List<User> findAllOrderByUpdatedAtAsc();

    @Query("SELECT u FROM User u ORDER BY u.username ASC")
    List<User> findAllOrderByUsernameAsc();

    @Query("SELECT u FROM User u ORDER BY u.username DESC")
    List<User> findAllOrderByUsernameDesc();

    @Query("SELECT u FROM User u ORDER BY u.email ASC")
    List<User> findAllOrderByEmailAsc();

    @Query("SELECT u FROM User u ORDER BY u.email DESC")
    List<User> findAllOrderByEmailDesc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt DESC")
    List<User> findByActiveTrueOrderByCreatedAtDesc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.createdAt ASC")
    List<User> findByActiveTrueOrderByCreatedAtAsc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.updatedAt DESC")
    List<User> findByActiveTrueOrderByUpdatedAtDesc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.updatedAt ASC")
    List<User> findByActiveTrueOrderByUpdatedAtAsc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.username ASC")
    List<User> findByActiveTrueOrderByUsernameAsc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.username DESC")
    List<User> findByActiveTrueOrderByUsernameDesc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.email ASC")
    List<User> findByActiveTrueOrderByEmailAsc();

    @Query("SELECT u FROM User u WHERE u.active = true ORDER BY u.email DESC")
    List<User> findByActiveTrueOrderByEmailDesc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.createdAt DESC")
    List<User> findByActiveFalseOrderByCreatedAtDesc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.createdAt ASC")
    List<User> findByActiveFalseOrderByCreatedAtAsc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.updatedAt DESC")
    List<User> findByActiveFalseOrderByUpdatedAtDesc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.updatedAt ASC")
    List<User> findByActiveFalseOrderByUpdatedAtAsc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.username ASC")
    List<User> findByActiveFalseOrderByUsernameAsc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.username DESC")
    List<User> findByActiveFalseOrderByUsernameDesc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.email ASC")
    List<User> findByActiveFalseOrderByEmailAsc();

    @Query("SELECT u FROM User u WHERE u.active = false ORDER BY u.email DESC")
    List<User> findByActiveFalseOrderByEmailDesc();

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findTopByRoleNameOrderByCreatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.createdAt ASC LIMIT :limit")
    List<User> findTopByRoleNameOrderByCreatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.updatedAt DESC LIMIT :limit")
    List<User> findTopByRoleNameOrderByUpdatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.updatedAt ASC LIMIT :limit")
    List<User> findTopByRoleNameOrderByUpdatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.username ASC LIMIT :limit")
    List<User> findTopByRoleNameOrderByUsernameAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.username DESC LIMIT :limit")
    List<User> findTopByRoleNameOrderByUsernameDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.email ASC LIMIT :limit")
    List<User> findTopByRoleNameOrderByEmailAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName ORDER BY u.email DESC LIMIT :limit")
    List<User> findTopByRoleNameOrderByEmailDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByCreatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.createdAt ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByCreatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.updatedAt DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByUpdatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.updatedAt ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByUpdatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.username ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByUsernameAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.username DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByUsernameDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.email ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByEmailAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = true ORDER BY u.email DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveTrueOrderByEmailDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.createdAt DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByCreatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.createdAt ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByCreatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.updatedAt DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByUpdatedAtDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.updatedAt ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByUpdatedAtAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.username ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByUsernameAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.username DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByUsernameDesc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.email ASC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByEmailAsc(@Param("roleName") String roleName, @Param("limit") int limit);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.active = false ORDER BY u.email DESC LIMIT :limit")
    List<User> findTopByRoleNameAndActiveFalseOrderByEmailDesc(@Param("roleName") String roleName, @Param("limit") int limit);
}