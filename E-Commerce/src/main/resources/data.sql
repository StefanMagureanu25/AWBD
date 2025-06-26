INSERT INTO roles (id, name, active, created_at, updated_at) VALUES (1, 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP), (2, 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, username, email, password, first_name, last_name, enabled, active, created_at, updated_at)
VALUES (1, 'admin', 'admin@example.com', '$2a$10$HKtULSjzb4cRMJh8xjANVeKRI8LHi6qJU.aERBpwr2NJU8YtPPSrC', 'Test', 'Admin', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO users (id, username, email, password, first_name, last_name, enabled, active, created_at, updated_at)
VALUES (2, 'user', 'user@example.com', '$2a$10$HKtULSjzb4cRMJh8xjANVeKRI8LHi6qJU.aERBpwr2NJU8YtPPSrC', 'John', 'Doe', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (1, 2), (2, 1);

INSERT INTO categories (id, name, description, active, created_at, updated_at) VALUES 
(1, 'Electronics', 'Latest electronic devices and gadgets', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Clothing', 'Fashion and apparel for all seasons', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Books', 'Educational and entertainment books', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Home & Garden', 'Home improvement and garden supplies', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Sports', 'Sports equipment and athletic wear', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO products (id, name, description, price, stock_quantity, active, created_at, updated_at) VALUES 
(1, 'Smartphone X', 'Latest smartphone with advanced features', 699.99, 50, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Laptop Pro', 'High-performance laptop for professionals', 1299.99, 25, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Wireless Headphones', 'Premium wireless headphones with noise cancellation', 199.99, 100, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'T-Shirt Classic', 'Comfortable cotton t-shirt in various colors', 24.99, 200, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Jeans Slim Fit', 'Modern slim fit jeans for men and women', 59.99, 150, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 'Running Shoes', 'Professional running shoes with cushioning', 89.99, 75, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Programming Book', 'Complete guide to modern programming', 39.99, 30, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Garden Tool Set', 'Complete set of essential garden tools', 79.99, 40, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Yoga Mat', 'Premium yoga mat for home workouts', 34.99, 60, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Coffee Maker', 'Automatic coffee maker for home use', 149.99, 35, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO product_categories (product_id, category_id) VALUES 
(1, 1), -- Smartphone X -> Electronics
(2, 1), -- Laptop Pro -> Electronics
(3, 1), -- Wireless Headphones -> Electronics
(4, 2), -- T-Shirt Classic -> Clothing
(5, 2), -- Jeans Slim Fit -> Clothing
(6, 2), -- Running Shoes -> Clothing
(6, 5), -- Running Shoes -> Sports
(7, 3), -- Programming Book -> Books
(8, 4), -- Garden Tool Set -> Home & Garden
(9, 5), -- Yoga Mat -> Sports
(10, 4); -- Coffee Maker -> Home & Garden 