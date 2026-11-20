package app;

import conexion.ConexionBD;
import conexion.EjecutorScript;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DemoEstructura {

    public static void main(String[] args) throws Exception {
        Path esquema = Path.of("sql", "esquema_mariadb.sql");
        Path datos = Path.of("sql", "datos_iniciales.sql");

        try (Connection con = ConexionBD.conectar()) {
            // 1. Ejecutar DDL y datos iniciales desde Java
            int n1 = EjecutorScript.ejecutar(con, esquema);
            System.out.println("[OK] " + esquema + ": " + n1 + " sentencias ejecutadas.");

            int n2 = EjecutorScript.ejecutar(con, datos);
            System.out.println("[OK] " + datos + ": " + n2 + " sentencias ejecutadas.");

            // 2. Probar integridad referencial: insertar alumno con id_modulo = 999 (inexistente)
            System.out.println("\n-- Prueba de integridad referencial (id_modulo = 999) --");
            String sqlInvalido = "INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, id_modulo) "
                    + "VALUES ('Alumno Fantasma', 6.00, 'MATRICULADO', '2026-10-01', 999)";
            try (PreparedStatement ps = con.prepareStatement(sqlInvalido)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                System.out.println("[OK - Integridad verificada] MariaDB rechazó la inserción:");
                System.out.println("  Mensaje: " + e.getMessage());
                System.out.println("  SQLState: " + e.getSQLState() + " | ErrorCode: " + e.getErrorCode());
            }
        }
    }
}