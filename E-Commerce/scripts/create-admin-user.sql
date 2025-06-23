-- =====================================================
-- E-Commerce Admin User Creation Script
-- =====================================================
-- This script creates an admin user with proper roles
-- Run this script in your PostgreSQL database
-- =====================================================

-- First, ensure we're in the correct database
-- \c e-commerce;

-- =====================================================
-- 1. Create Roles (if they don't exist)
-- =====================================================

-- Create ADMIN role
INSERT INTO roles (name, description, created_at, updated_at)
VALUES ('ADMIN', 'Administrator role with full access', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- Create USER role  
INSERT INTO roles (name, description, created_at, updated_at)
VALUES ('USER', 'Regular user role', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =====================================================
-- 2. Create Admin User
-- =====================================================

-- Create the admin user
-- Password: admin123 (BCrypt encoded)
INSERT INTO users (
    username, 
    email, 
    password, 
    first_name, 
    last_name, 
    enabled,
    created_at, 
    updated_at
)
VALUES (
    'admin',
    'admin@ecommerce.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- admin123
    'Admin',
    'User',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

-- =====================================================
-- 3. Assign Roles to Admin User
-- =====================================================

-- Get the user ID and role IDs
DO $$
DECLARE
    admin_user_id BIGINT;
    admin_role_id BIGINT;
    user_role_id BIGINT;
BEGIN
    -- Get admin user ID
    SELECT id INTO admin_user_id FROM users WHERE username = 'admin';
    
    -- Get role IDs
    SELECT id INTO admin_role_id FROM roles WHERE name = 'ADMIN';
    SELECT id INTO user_role_id FROM roles WHERE name = 'USER';
    
    -- Assign ADMIN role to admin user
    INSERT INTO user_roles (user_id, role_id)
    VALUES (admin_user_id, admin_role_id)
    ON CONFLICT (user_id, role_id) DO NOTHING;
    
    -- Assign USER role to admin user (admin has both roles)
    INSERT INTO user_roles (user_id, role_id)
    VALUES (admin_user_id, user_role_id)
    ON CONFLICT (user_id, role_id) DO NOTHING;
    
    RAISE NOTICE 'Admin user created successfully!';
    RAISE NOTICE 'Username: admin';
    RAISE NOTICE 'Email: admin@ecommerce.com';
    RAISE NOTICE 'Password: admin123';
    RAISE NOTICE 'User ID: %', admin_user_id;
END $$;

-- =====================================================
-- 4. Verification Queries
-- =====================================================

-- Verify admin user was created
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.enabled,
    u.created_at,
    array_agg(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin'
GROUP BY u.id, u.username, u.email, u.first_name, u.last_name, u.enabled, u.created_at;

-- Show all users with their roles
SELECT 
    u.username,
    u.email,
    u.enabled,
    array_agg(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
GROUP BY u.id, u.username, u.email, u.enabled
ORDER BY u.username;

-- =====================================================
-- 5. Cleanup (if needed)
-- =====================================================

-- To remove the admin user (uncomment if needed):
-- DELETE FROM user_roles WHERE user_id = (SELECT id FROM users WHERE username = 'admin');
-- DELETE FROM users WHERE username = 'admin';

-- =====================================================
-- Script completed!
-- ===================================================== 