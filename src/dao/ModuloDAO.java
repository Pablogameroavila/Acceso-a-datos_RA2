package dao;

import conexion.ConexionBD;
import modelo.Modulo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Acceso a la tabla modulos (criterios f, g, h, i). */
public class ModuloDAO {

    public int insertar(Modulo m) throws SQLException {
        String sql = "INSERT INTO modulos (codigo, nombre, horas_totales, plazas_disponibles) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getCodigo());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getHorasTotales());
            ps.setInt(4, m.getPlazasDisponibles());
            ps.executeUpdate();

            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    m.setId(claves.getInt(1));
                }
            }
            return m.getId();
        }
    }

    public List<Modulo> listarTodos() throws SQLException {
        List<Modulo> lista = new ArrayList<>();
        String sql = "SELECT id, codigo, nombre, horas_totales, plazas_disponibles FROM modulos ORDER BY id";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public int contar() throws SQLException {
        String sql = "SELECT COUNT(*) FROM modulos";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    static Modulo mapear(ResultSet rs) throws SQLException {
        return new Modulo(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getInt("horas_totales"),
                rs.getInt("plazas_disponibles")
        );
    }
}