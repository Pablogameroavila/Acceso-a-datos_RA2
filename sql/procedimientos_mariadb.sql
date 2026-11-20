-- AMPLIACION (solo MariaDB). Procedimientos almacenados para probar CallableStatement.
-- Ejecutar con el cliente de línea de comandos (admite DELIMITER):
--     mariadb -u refugio_app -p refugio < sql/procedimientos_mariadb.sql

DROP PROCEDURE IF EXISTS sp_listar_por_especie;
DROP PROCEDURE IF EXISTS sp_contar_por_estado;

DELIMITER //

CREATE PROCEDURE sp_listar_por_especie(IN p_especie VARCHAR(40))
BEGIN
    SELECT id, nombre, especie, estado, id_cuidador
    FROM animales
    WHERE especie = p_especie
    ORDER BY nombre;
END //

CREATE PROCEDURE sp_contar_por_estado(IN p_estado VARCHAR(20), OUT p_total INT)
BEGIN
    SELECT COUNT(*) INTO p_total FROM animales WHERE estado = p_estado;
END //

DELIMITER ;
