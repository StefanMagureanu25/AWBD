package com.stefan.ecommerce;

import com.stefan.ecommerce.entities.User;
import com.stefan.ecommerce.repositories.UserRepository;
import com.stefan.ecommerce.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdminUserTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testAdminUserExists() {
        // Test that admin user exists
        Optional<User> adminUser = userRepository.findByUsername("admin");
        assertTrue(adminUser.isPresent(), "Admin user should exist");
        
        User admin = adminUser.get();
        assertEquals("admin", admin.getUsername());
        assertEquals("admin@example.com", admin.getEmail());
        assertEquals("Test", admin.getFirstName());
        assertEquals("Admin", admin.getLastName());
        assertTrue(admin.getEnabled(), "Admin user should be enabled");
        
        // Test that admin has both USER and ADMIN roles
        assertTrue(admin.hasRole("USER"), "Admin should have USER role");
        assertTrue(admin.hasRole("ADMIN"), "Admin should have ADMIN role");
        assertTrue(admin.isAdmin(), "Admin should be recognized as admin");
    }

    @Test
    public void testAdminPasswordVerification() {
        // Test that admin password can be verified
        Optional<User> adminUser = userRepository.findByUsername("admin");
        assertTrue(adminUser.isPresent(), "Admin user should exist");
        
        User admin = adminUser.get();
        String plainPassword = "admin123";
        boolean passwordMatches = passwordEncoder.matches(plainPassword, admin.getPassword());
        
        assertTrue(passwordMatches, "Admin password should match 'admin123'");
    }

    @Test
    public void testTestUserExists() {
        // Test that test user exists
        Optional<User> testUser = userRepository.findByUsername("user");
        assertTrue(testUser.isPresent(), "Test user should exist");
        
        User user = testUser.get();
        assertEquals("user", user.getUsername());
        assertEquals("user@test.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertTrue(user.getEnabled(), "Test user should be enabled");
        
        // Test that user has only USER role
        assertTrue(user.hasRole("USER"), "User should have USER role");
        assertFalse(user.hasRole("ADMIN"), "User should not have ADMIN role");
        assertFalse(user.isAdmin(), "User should not be recognized as admin");
    }

    @Test
    public void testTestUserPasswordVerification() {
        // Test that test user password can be verified
        Optional<User> testUser = userRepository.findByUsername("user");
        assertTrue(testUser.isPresent(), "Test user should exist");
        
        User user = testUser.get();
        String plainPassword = "user123";
        boolean passwordMatches = passwordEncoder.matches(plainPassword, user.getPassword());
        
        assertTrue(passwordMatches, "Test user password should match 'user123'");
    }
} 