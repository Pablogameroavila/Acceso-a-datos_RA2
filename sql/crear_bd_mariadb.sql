-- Ejecutar UNA VEZ como administrador (root) en MariaDB: crea la base de datos y un usuario
-- con permisos SOLO sobre ella (no uses root desde tu aplicación).
-- CAMBIA 'CAMBIA_ESTA_CLAVE' por una contraseña tuya y no la subas a Git.

CREATE DATABASE IF NOT EXISTS refugio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'refugio_app'@'localhost' IDENTIFIED BY 'CAMBIA_ESTA_CLAVE';
GRANT ALL PRIVILEGES ON refugio.* TO 'refugio_app'@'localhost';
FLUSH PRIVILEGES;
