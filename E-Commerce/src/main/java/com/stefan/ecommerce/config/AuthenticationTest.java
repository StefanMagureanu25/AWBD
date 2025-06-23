package com.stefan.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationTest implements CommandLineRunner {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationTest(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Authentication Test ===");
        
        try {
            // Test loading admin user
            UserDetails userDetails = userDetailsService.loadUserByUsername("admin");
            System.out.println("✅ Admin user loaded successfully!");
            System.out.println("Username: " + userDetails.getUsername());
            System.out.println("Authorities: " + userDetails.getAuthorities());
            System.out.println("Enabled: " + userDetails.isEnabled());
            
            // Test password verification
            String plainPassword = "admin123";
            boolean passwordMatches = passwordEncoder.matches(plainPassword, userDetails.getPassword());
            System.out.println("Password verification: " + passwordMatches);
            
            if (passwordMatches) {
                System.out.println("✅ Password verification successful!");
            } else {
                System.out.println("❌ Password verification failed!");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error loading admin user: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== End Authentication Test ===");
    }
} 