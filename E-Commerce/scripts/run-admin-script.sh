#!/bin/bash

# =====================================================
# E-Commerce Admin User Creation Script Runner
# =====================================================

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=====================================================${NC}"
echo -e "${BLUE}E-Commerce Admin User Creation Script${NC}"
echo -e "${BLUE}=====================================================${NC}"

# Check if psql is available
if ! command -v psql &> /dev/null; then
    echo -e "${YELLOW}PostgreSQL client (psql) is not installed or not in PATH${NC}"
    echo "Please install PostgreSQL client tools"
    exit 1
fi

# Database configuration
DB_NAME="e-commerce"
DB_USER="postgres"
DB_HOST="localhost"
DB_PORT="5432"

echo -e "${BLUE}Database Configuration:${NC}"
echo "Database: $DB_NAME"
echo "Host: $DB_HOST"
echo "Port: $DB_PORT"
echo "User: $DB_USER"
echo ""

# Check if SQL script exists
SQL_SCRIPT="$(dirname "$0")/create-admin-user.sql"
if [ ! -f "$SQL_SCRIPT" ]; then
    echo -e "${YELLOW}SQL script not found: $SQL_SCRIPT${NC}"
    exit 1
fi

echo -e "${BLUE}Running admin user creation script...${NC}"
echo ""

# Run the SQL script
psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME -f "$SQL_SCRIPT"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✅ Admin user created successfully!${NC}"
    echo ""
    echo -e "${BLUE}Admin user credentials:${NC}"
    echo "Username: admin"
    echo "Email: admin@ecommerce.com"
    echo "Password: admin123"
    echo ""
    echo -e "${YELLOW}⚠️  IMPORTANT: Change the default password in production!${NC}"
    echo ""
    echo -e "${BLUE}You can now log in to the application with these credentials.${NC}"
else
    echo ""
    echo -e "${YELLOW}❌ Failed to create admin user${NC}"
    echo "Please check your database connection and try again."
fi 