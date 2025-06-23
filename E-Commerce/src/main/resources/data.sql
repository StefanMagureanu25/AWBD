INSERT INTO roles (id, name) VALUES (1, 'USER'), (2, 'ADMIN');

INSERT INTO users (id, username, email, password, first_name, last_name, enabled, created_at, updated_at)
VALUES (1, 'testadmin', 'admin@example.com', '$2a$10$HKtULSjzb4cRMJh8xjANVeKRI8LHi6qJU.aERBpwr2NJU8YtPPSrC', 'Test', 'Admin', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO user_roles (user_id, role_id) VALUES (1, 1), (1, 2); 