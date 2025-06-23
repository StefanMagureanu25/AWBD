# Admin User Creation Scripts

This directory contains scripts to create an admin user in your E-Commerce database.

## Files

- `create-admin-user.sql` - The main SQL script that creates the admin user
- `run-admin-script.sh` - Shell script to easily run the SQL script
- `README.md` - This instruction file

## Prerequisites

1. **PostgreSQL Database**: Make sure your database is running
2. **Database Connection**: Ensure you can connect to your database
3. **psql Client**: PostgreSQL command-line client must be installed

## Quick Start

### Option 1: Using the Shell Script (Recommended)

```bash
# Make sure you're in the project root directory
cd /path/to/your/e-commerce-project

# Run the script
./scripts/run-admin-script.sh
```

### Option 2: Manual SQL Execution

```bash
# Connect to your database and run the SQL script
psql -h localhost -U postgres -d e-commerce -f scripts/create-admin-user.sql
```

### Option 3: Using psql Interactive Mode

```bash
# Connect to your database
psql -h localhost -U postgres -d e-commerce

# Then run the script
\i scripts/create-admin-user.sql
```

## Database Configuration

The script uses these default settings:
- **Host**: localhost
- **Port**: 5432
- **Database**: e-commerce
- **User**: postgres

If your database uses different settings, you can:

1. **Modify the shell script** to change the database connection parameters
2. **Use manual SQL execution** with your custom parameters
3. **Set environment variables** for database connection

## What the Script Does

1. **Creates Roles**: ADMIN and USER roles (if they don't exist)
2. **Creates Admin User**: Username `admin` with BCrypt-encoded password
3. **Assigns Roles**: Gives the admin user both ADMIN and USER roles
4. **Verifies Creation**: Shows the created user and their roles

## Admin User Credentials

After running the script, you'll have an admin user with these credentials:

- **Username**: `admin`
- **Email**: `admin@ecommerce.com`
- **Password**: `admin123`

## Security Notes

⚠️ **IMPORTANT**: 
- Change the default password immediately after first login
- This script is for development/testing purposes only
- Use strong passwords in production environments

## Troubleshooting

### Common Issues

1. **"psql: command not found"**
   - Install PostgreSQL client tools
   - Make sure psql is in your PATH

2. **"Connection refused"**
   - Check if PostgreSQL is running
   - Verify host, port, and database name
   - Check firewall settings

3. **"Database does not exist"**
   - Create the database first: `createdb e-commerce`
   - Or use an existing database name

4. **"Permission denied"**
   - Check database user permissions
   - Ensure the user can create tables and insert data

### Verification

After running the script, you can verify the admin user was created:

```sql
-- Check admin user
SELECT username, email, enabled FROM users WHERE username = 'admin';

-- Check user roles
SELECT u.username, array_agg(r.name) as roles
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
LEFT JOIN roles r ON ur.role_id = r.id
WHERE u.username = 'admin'
GROUP BY u.username;
```

## Next Steps

1. **Start your application**: `./mvnw spring-boot:run`
2. **Login**: Use the admin credentials to access the application
3. **Test admin features**: Navigate to admin dashboard and features
4. **Change password**: Update the default password for security

## Support

If you encounter issues:
1. Check the database logs for error messages
2. Verify your database connection settings
3. Ensure all prerequisites are met
4. Check the troubleshooting section above 