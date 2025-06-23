package com.stefan.ecommerce;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String storedHash = "$2a$10$9K5TxP2GYEt0fJBqfcRswOPWU3KVAQdhNP2pxDLgdhwsjTRv3CL/u";
        String password = "admin123";
        
        boolean matches = encoder.matches(password, storedHash);
        System.out.println("Password matches: " + matches);
        
        // Also generate a new hash for comparison
        String newHash = encoder.encode(password);
        System.out.println("New hash for 'admin123': " + newHash);
        System.out.println("New hash matches: " + encoder.matches(password, newHash));
    }
} 