package dao;

import conexion.ConexionBD;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * Criterio j: gestión de transacciones con commit, rollback y restauración de autoCommit.
 * Agrupa dos operaciones dependientes:
 *  1. Insertar un nuevo alumno en la tabla alumnos.
 *  2. Descontar una plaza disponible en el módulo correspondiente (tabla modulos).
 */
public class DamTransacciones {

    public int matricularAlumnoConPlaza(Alumno alumno, boolean forzarError) throws SQLException {
        String sqlInsertAlumno = "INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlRestarPlaza = "UPDATE modulos SET plazas_disponibles = plazas_disponibles - 1 "
                + "WHERE id = ? AND plazas_disponibles > 0";

        try (Connection con = ConexionBD.conectar()) {
            boolean autoCommitOriginal = con.getAutoCommit();
            con.setAutoCommit(false); // Inicia la transacción manual

            try {
                // Operación 1: Insertar al alumno y recuperar su id generado
                int idAlumno;
                try (PreparedStatement ps1 = con.prepareStatement(sqlInsertAlumno, Statement.RETURN_GENERATED_KEYS)) {
                    ps1.setString(1, alumno.getNombre());
                    ps1.setBigDecimal(2, alumno.getNotaMedia());
                    ps1.setString(3, alumno.getEstado());
                    ps1.setObject(4, alumno.getFechaMatricula());
                    if (alumno.getObservaciones() == null) {
                        ps1.setNull(5, Types.VARCHAR);
                    } else {
                        ps1.setString(5, alumno.getObservaciones());
                    }
                    ps1.setInt(6, alumno.getIdModulo());
                    ps1.executeUpdate();

                    try (ResultSet claves = ps1.getGeneratedKeys()) {
                        if (!claves.next()) {
                            throw new SQLException("No se generó clave primaria para el alumno.");
                        }
                        idAlumno = claves.getInt(1);
                        alumno.setId(idAlumno);
                    }
                }

                // Simulación de caída o fallo entre ambas operaciones
                if (forzarError) {
                    throw new SQLException("Fallo forzado entre operaciones para verificar rollback().");
                }

                // Operación 2: Descontar 1 plaza disponible en el módulo
                try (PreparedStatement ps2 = con.prepareStatement(sqlRestarPlaza)) {
                    ps2.setInt(1, alumno.getIdModulo());
                    int filasAfectadas = ps2.executeUpdate();
                    if (filasAfectadas == 0) {
                        throw new SQLException("No quedan plazas disponibles o el módulo no existe.");
                    }
                }

                con.commit(); // Consolida ambas operaciones de forma atómica
                return idAlumno;

            } catch (SQLException | RuntimeException e) {
                try {
                    con.rollback(); // Deshace el INSERT del alumno si falló el descuento de plaza
                } catch (SQLException exRollback) {
                    e.addSuppressed(exRollback);
                }
                throw e;
            } finally {
                con.setAutoCommit(autoCommitOriginal); // Restaura el modo original de la conexión
            }
        }
    }
}