package dao;

import conexion.ConexionBD;
import modelo.Alumno;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * AMPLIACIÓN (opcional, solo MariaDB): llamada a procedimientos almacenados con CallableStatement.
 */
public class ProcedimientosDAO {

    /** Procedimiento con un parámetro de entrada (IN) que devuelve filas de alumnos. */
    public List<Alumno> listarPorEstado(String estado) throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String llamada = "{call sp_listar_por_estado(?)}";

        try (Connection con = ConexionBD.conectar();
             CallableStatement cs = con.prepareCall(llamada)) {

            cs.setString(1, estado);
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(AlumnoDAO.mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Procedimiento con un parámetro de entrada (IN) y uno de salida (OUT). */
    public int contarPorEstado(String estado) throws SQLException {
        String llamada = "{call sp_contar_por_estado(?, ?)}";

        try (Connection con = ConexionBD.conectar();
             CallableStatement cs = con.prepareCall(llamada)) {

            cs.setString(1, estado);
            cs.registerOutParameter(2, Types.INTEGER); // declara el tipo del parámetro OUT
            cs.execute();
            return cs.getInt(2);
        }
    }
}