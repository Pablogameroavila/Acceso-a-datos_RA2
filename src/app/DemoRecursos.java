package app;

import conexion.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Criterio i: comprobar que try-with-resources cierra los objetos al terminar.
 * Guardamos referencias fuera del try SOLO para poder preguntar después si están cerradas
 * (en código real no se hace: el objeto ya no se usa una vez cerrado).
 */
public final class DemoRecursos {

    private DemoRecursos() {
    }

    public static void demostrar() throws SQLException {
        Connection conRef = null;
        PreparedStatement psRef = null;
        ResultSet rsRef = null;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT id, nombre FROM alumnos");
             ResultSet rs = ps.executeQuery()) {

            conRef = con;
            psRef = ps;
            rsRef = rs;
            System.out.println("DENTRO del try:");
            System.out.println("  Connection cerrada?        " + con.isClosed());
            System.out.println("  PreparedStatement cerrado? " + ps.isClosed());
            System.out.println("  ResultSet cerrado?         " + rs.isClosed());
        }

        System.out.println("FUERA del try (Java los ha cerrado en orden inverso al de apertura):");
        System.out.println("  Connection cerrada?        " + conRef.isClosed());
        System.out.println("  PreparedStatement cerrado? " + psRef.isClosed());
        System.out.println("  ResultSet cerrado?         " + rsRef.isClosed());
    }
}