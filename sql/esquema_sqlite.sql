-- Criterio e: estructura de la base de datos (SQLite)
-- Relación 1:N -> un cuidador atiende a muchos animales; cada animal tiene un único cuidador.
-- Al volver a ejecutarlo, borra y recrea las tablas (primero la tabla "hija").

DROP TABLE IF EXISTS animales;
DROP TABLE IF EXISTS cuidadores;

CREATE TABLE cuidadores (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre   TEXT NOT NULL,
    telefono TEXT
);

CREATE TABLE animales (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre      TEXT    NOT NULL,
    especie     TEXT    NOT NULL,
    estado      TEXT    NOT NULL DEFAULT 'EN_REFUGIO'
                CHECK (estado IN ('EN_REFUGIO', 'ADOPTADO', 'EN_TRATAMIENTO')),
    id_cuidador INTEGER NOT NULL,
    FOREIGN KEY (id_cuidador) REFERENCES cuidadores (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
