package conexion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Ejecuta un fichero .sql sencillo desde Java (criterio e: definir la estructura).
 *
 * LIMITACIONES (a propósito, para que sea fácil de entender):
 *  - Separa las sentencias por ';' -> NO sirve para procedimientos almacenados (usan DELIMITER).
 *  - Ignora las líneas que empiezan por "--". No pongas comentarios al final de una línea de SQL.
 */
public final class EjecutorScript {

    private EjecutorScript() {
    }

    /** Devuelve el número de sentencias ejecutadas. */
    public static int ejecutar(Connection con, Path script) throws IOException, SQLException {
        StringBuilder sql = new StringBuilder();
        for (String linea : Files.readAllLines(script)) {
            if (!linea.trim().startsWith("--")) {
                sql.append(linea).append('\n');
            }
        }

        int ejecutadas = 0;
        try (Statement st = con.createStatement()) {
            for (String sentencia : sql.toString().split(";")) {
                String limpia = sentencia.trim();
                if (!limpia.isEmpty()) {
                    st.execute(limpia); // execute() vale para DDL e INSERT
                    ejecutadas++;
                }
            }
        }
        return ejecutadas;
    }
}
