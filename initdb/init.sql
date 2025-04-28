-- Création de la base pour le service d'authentification
CREATE DATABASE IF NOT EXISTS `auth-db`;

-- Donne tous les droits à l'utilisateur 'user' sur cette base
GRANT ALL PRIVILEGES ON `auth-db`.* TO 'user'@'%';

-- (Optionnel) Rafraîchit les privilèges
FLUSH PRIVILEGES;
