package conexion;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Fábrica de conexiones (criterio d) para el proyecto Gestión Académica de 2º de DAM.
 *
 * - La URL, el usuario y la contraseña NO están escritos en el código: se leen del
 *   fichero config.properties (excluido en .gitignore) o de la variable de entorno
 *   AD_DB_PASSWORD.
 * - No hace falta Class.forName(...): desde JDBC 4 el DriverManager localiza el driver
 *   mariadb-java-client en el classpath a partir del prefijo jdbc:mariadb:.
 */
public final class ConexionBD {

    private static final Path FICHERO_CONFIG = Path.of("config.properties");
    private static final String URL_POR_DEFECTO = "jdbc:mariadb://localhost:3306/dam2";

    private static Properties configuracion;

    private ConexionBD() {
        // clase de utilidad: no se instancia
    }

    /** Abre una conexión NUEVA. Quien la pide es responsable de cerrarla (try-with-resources). */
    public static Connection conectar() throws SQLException {
        Properties cfg = cargarConfiguracion();
        String url = cfg.getProperty("db.url", URL_POR_DEFECTO);
        String usuario = cfg.getProperty("db.user", "");
        String password = System.getenv("AD_DB_PASSWORD");
        if (password == null) {
            password = cfg.getProperty("db.password", "");
        }

        try {
            if (url.startsWith("jdbc:sqlite:")) {
                Connection con = DriverManager.getConnection(url);
                try {
                    activarClavesForaneasSqlite(con);
                } catch (SQLException e) {
                    con.close();
                    throw e;
                }
                return con;
            }
            return DriverManager.getConnection(url, usuario, password);

        } catch (SQLException e) {
            // Mensaje "seguro": ni URL, ni usuario, ni contraseña.
            System.err.println("No se pudo establecer la conexión con la base de datos.");
            System.err.println("  Código de error: " + e.getErrorCode() + " | SQLState: " + e.getSQLState());
            throw new SQLException("No se pudo abrir la conexión con la base de datos.",
                    e.getSQLState(), e.getErrorCode(), e);
        }
    }

    /** Devuelve "mariadb" o "sqlite" según la URL configurada (sirve para elegir el script SQL). */
    public static String tipoGestor() throws SQLException {
        String url = cargarConfiguracion().getProperty("db.url", URL_POR_DEFECTO);
        if (url.startsWith("jdbc:mariadb:")) {
            return "mariadb";
        }
        if (url.startsWith("jdbc:sqlite:")) {
            return "sqlite";
        }
        throw new SQLException("Gestor no soportado en este proyecto: revisa db.url en config.properties");
    }

    private static void activarClavesForaneasSqlite(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
    }

    private static synchronized Properties cargarConfiguracion() throws SQLException {
        if (configuracion == null) {
            Properties p = new Properties();
            if (Files.exists(FICHERO_CONFIG)) {
                try (InputStream in = Files.newInputStream(FICHERO_CONFIG)) {
                    p.load(in);
                } catch (IOException e) {
                    throw new SQLException("No se pudo leer config.properties", e);
                }
            }
            configuracion = p;
        }
        return configuracion;
    }
}