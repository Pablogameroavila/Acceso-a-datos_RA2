-- Datos iniciales de prueba para Gestión Académica de 2º de DAM

INSERT INTO modulos (codigo, nombre, horas_totales, plazas_disponibles) VALUES ('0486', 'Acceso a Datos', 120, 25);
INSERT INTO modulos (codigo, nombre, horas_totales, plazas_disponibles) VALUES ('0490', 'Programación de Servicios y Procesos', 90, 25);
INSERT INTO modulos (codigo, nombre, horas_totales, plazas_disponibles) VALUES ('0488', 'Desarrollo de Interfaces', 140, 20);

INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) VALUES ('Pablo Navarro', 8.75, 'APROBADO', '2026-09-15', 'Delegado de clase', 1);
INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) VALUES ('Lucía Gómez', 9.20, 'APROBADO', '2026-09-15', NULL, 1);
INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) VALUES ('Carlos Mendoza', 5.80, 'MATRICULADO', '2026-09-16', 'Pendiente entrega RA2', 2);
INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) VALUES ('Elena Torres', 7.40, 'MATRICULADO', '2026-09-16', NULL, 2);
INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) VALUES ('Javier Serrano', 4.50, 'PENDIENTE', '2026-09-18', 'Convocatoria extraordinaria', 3);