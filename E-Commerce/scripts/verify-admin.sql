-- =====================================================
-- Admin User Verification Script
-- =====================================================

-- Check if admin user exists
SELECT 
    'Admin User Check' as check_type,
    CASE 
        WHEN COUNT(*) > 0 THEN 'EXISTS'
        ELSE 'NOT FOUND'
    END as status,
    COUNT(*) as count
FROM users 
WHERE username = 'admin';

-- Show admin user details
SELECT 
    'Admin Details' as check_type,
    id,
    username,
    email,
    first_name,
    last_name,
    enabled,
    created_at,
    password_length
FROM (
    SELECT 
        id,
        username,
        email,
        first_name,
        last_name,
        enabled,
        created_at,
        LENGTH(password) as password_length
    FROM users 
    WHERE username = 'admin'
) admin_info;

-- Check admin user roles
SELECT 
    'Admin Roles' as check_type,
    u.username,
    array_agg(r.name) as roles,
    COUNT(r.name) as role_count
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin'
GROUP BY u.username;

-- Check all users
SELECT 
    'All Users' as check_type,
    username,
    email,
    enabled,
    created_at
FROM users
ORDER BY created_at;

-- Check all roles
SELECT 
    'All Roles' as check_type,
    name,
    description,
    created_at
FROM roles
ORDER BY name; 