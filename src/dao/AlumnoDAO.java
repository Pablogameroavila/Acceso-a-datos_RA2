package dao;

import conexion.ConexionBD;
import modelo.Alumno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Acceso a la tabla alumnos (criterios f, g, h, i).
 */
public class AlumnoDAO {

    // ---------------------------------------------------------------- criterio f

    /** INSERT con PreparedStatement y recuperación de la clave primaria generada. */
    public int insertar(Alumno a) throws SQLException {
        String sql = "INSERT INTO alumnos (nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getNombre());
            ps.setBigDecimal(2, a.getNotaMedia());
            ps.setString(3, a.getEstado());
            ps.setObject(4, a.getFechaMatricula());
            if (a.getObservaciones() == null) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, a.getObservaciones());
            }
            ps.setInt(6, a.getIdModulo());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    a.setId(claves.getInt(1));
                }
            }
            return a.getId();
        }
    }

    /** UPDATE del estado y la nota media de un alumno. Devuelve true si afectó a alguna fila. */
    public boolean actualizarCalificacion(int id, java.math.BigDecimal nuevaNota, String nuevoEstado) throws SQLException {
        String sql = "UPDATE alumnos SET nota_media = ?, estado = ? WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, nuevaNota);
            ps.setString(2, nuevoEstado);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** DELETE por clave primaria. Devuelve true si existía y se borró. */
    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM alumnos WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------------------------------------------------------------- criterio h

    /** Consulta general (sin parámetros). */
    public List<Alumno> listarTodos() throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo "
                + "FROM alumnos ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Consulta filtrada con parámetro (?) por estado académico. */
    public List<Alumno> listarPorEstado(String estado) throws SQLException {
        List<Alumno> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo "
                + "FROM alumnos WHERE estado = ? ORDER BY nota_media DESC";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Búsqueda por clave primaria devolviendo Optional para gestionar el caso sin resultado. */
    public Optional<Alumno> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, nota_media, estado, fecha_matricula, observaciones, id_modulo "
                + "FROM alumnos WHERE id = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
                return Optional.empty();
            }
        }
    }

    /** Consulta agregada: número de alumnos por estado (COUNT + GROUP BY). */
    public Map<String, Integer> contarPorEstado() throws SQLException {
        Map<String, Integer> resultado = new LinkedHashMap<>();
        String sql = "SELECT estado, COUNT(*) AS total FROM alumnos GROUP BY estado ORDER BY estado";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                resultado.put(rs.getString("estado"), rs.getInt("total"));
            }
        }
        return resultado;
    }

    /** Consulta con JOIN entre alumnos y modulos. */
    public List<String> listarConModulo() throws SQLException {
        List<String> lineas = new ArrayList<>();
        String sql = "SELECT a.nombre AS alumno, a.nota_media, a.estado, m.codigo, m.nombre AS modulo "
                + "FROM alumnos a JOIN modulos m ON a.id_modulo = m.id "
                + "ORDER BY m.codigo, a.nombre";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lineas.add(rs.getString("alumno") + " [" + rs.getString("codigo") + " - "
                        + rs.getString("modulo") + "] -> Nota: " + rs.getBigDecimal("nota_media")
                        + " (" + rs.getString("estado") + ")");
            }
        }
        return lineas;
    }

    // ---------------------------------------------------------------- criterio g

    /**
     * Convierte la fila actual del ResultSet en un objeto Alumno.
     * Gestiona explícitamente valores NULL en la columna opcional 'observaciones'.
     */
    static Alumno mapear(ResultSet rs) throws SQLException {
        Alumno a = new Alumno();
        a.setId(rs.getInt("id"));
        a.setNombre(rs.getString("nombre"));
        a.setNotaMedia(rs.getBigDecimal("nota_media"));
        a.setEstado(rs.getString("estado"));
        a.setFechaMatricula(rs.getObject("fecha_matricula", LocalDate.class));

        String obs = rs.getString("observaciones");
        a.setObservaciones(rs.wasNull() ? null : obs);

        a.setIdModulo(rs.getInt("id_modulo"));
        return a;
    }
}