-- Criterio e: estructura de la base de datos (MariaDB)
-- Temática: Gestión Académica de 2º de DAM (modulos 1 : N alumnos)

DROP TABLE IF EXISTS alumnos;
DROP TABLE IF EXISTS modulos;

CREATE TABLE modulos (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    codigo             VARCHAR(10)  NOT NULL UNIQUE,
    nombre             VARCHAR(100) NOT NULL,
    horas_totales      INT          NOT NULL,
    plazas_disponibles INT          NOT NULL,
    CONSTRAINT ck_modulos_horas  CHECK (horas_totales > 0),
    CONSTRAINT ck_modulos_plazas CHECK (plazas_disponibles >= 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE alumnos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(80)  NOT NULL,
    nota_media      DECIMAL(4,2) NOT NULL DEFAULT 5.00,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'MATRICULADO',
    fecha_matricula DATE         NOT NULL,
    observaciones   VARCHAR(200),
    id_modulo       INT          NOT NULL,
    CONSTRAINT ck_alumnos_nota   CHECK (nota_media BETWEEN 0.00 AND 10.00),
    CONSTRAINT ck_alumnos_estado CHECK (estado IN ('MATRICULADO', 'APROBADO', 'PENDIENTE')),
    CONSTRAINT fk_alumnos_modulo FOREIGN KEY (id_modulo) REFERENCES modulos (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;