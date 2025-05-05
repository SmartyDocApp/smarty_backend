-- Insertion d'un utilisateur par défaut (mot de passe: admin123)
-- Le mot de passe est encodé avec BCrypt
INSERT INTO users (username, password, email, first_name, last_name, enabled)
VALUES ('admin', '$2a$10$8tHKGZOjoSWvpZkJ9IxAPO83YPu4jsI1Z6npL/Rj3.QMQmW3CQcK6', 'admin@example.com', 'Admin', 'User', true)
ON DUPLICATE KEY UPDATE username = username;

-- Attribution du rôle ADMIN à l'utilisateur admin
INSERT INTO user_authorities (user_id, authority)
SELECT id, 'ROLE_ADMIN' FROM users WHERE username = 'admin'
ON DUPLICATE KEY UPDATE authority = authority;

-- Attribution du rôle USER à l'utilisateur admin
INSERT INTO user_authorities (user_id, authority)
SELECT id, 'ROLE_USER' FROM users WHERE username = 'admin'
ON DUPLICATE KEY UPDATE authority = authority; 