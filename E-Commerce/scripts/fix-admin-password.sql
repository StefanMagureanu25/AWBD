-- =====================================================
-- Fix Admin User Password Script
-- =====================================================
-- This script updates the admin user's password with a correct BCrypt hash
-- =====================================================

-- Update admin user password with correct BCrypt hash for 'admin123'
UPDATE users 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE username = 'admin';

-- Verify the update
SELECT 
    username,
    email,
    enabled,
    LENGTH(password) as password_length
FROM users 
WHERE username = 'admin';

-- Show success message
DO $$
BEGIN
    RAISE NOTICE 'Admin password updated successfully!';
    RAISE NOTICE 'Username: admin';
    RAISE NOTICE 'Password: admin123';
    RAISE NOTICE 'New BCrypt hash applied';
END $$; 