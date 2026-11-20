package app;

import conexion.ConexionBD;
import conexion.EjecutorScript;
import dao.AlumnoDAO;
import dao.DamTransacciones;
import dao.ModuloDAO;
import dao.ProcedimientosDAO;
import modelo.Alumno;
import modelo.Modulo;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú de demostración del proyecto RA2 - Gestión Académica de 2º de DAM.
 * Cada opción ilustra un criterio de evaluación del RA2.
 *
 * El Main no contiene ni una sola línea de SQL: solo invoca a los DAO.
 */
public class Main {

    private static final AlumnoDAO alumnoDAO = new AlumnoDAO();
    private static final ModuloDAO moduloDAO = new ModuloDAO();
    private static final DamTransacciones transacciones = new DamTransacciones();
    private static final ProcedimientosDAO procedimientos = new ProcedimientosDAO();

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            String opcion = teclado.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> probarConexion();
                    case "2" -> crearEsquemaYDatos();
                    case "3" -> demoModificaciones();
                    case "4" -> demoConsultas(teclado);
                    case "5" -> DemoRecursos.demostrar();
                    case "6" -> demoTransaccion(false);
                    case "7" -> demoTransaccion(true);
                    case "8" -> demoProcedimientos(teclado);
                    case "0" -> salir = true;
                    default -> System.out.println("Opción no válida.");
                }
            } catch (SQLException e) {
                System.err.println("[ERROR SQL] " + e.getMessage()
                        + " (SQLState=" + e.getSQLState() + ", código=" + e.getErrorCode() + ")");
                if (e.getCause() != null) {
                    System.err.println("  Causa: " + e.getCause().getMessage());
                }
            } catch (IOException e) {
                System.err.println("[ERROR de fichero] " + e.getMessage());
            }
        }
        System.out.println("Fin.");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== GESTIÓN 2º DAM - RA2 Acceso a Datos =====");
        System.out.println("1. Probar la conexión                          (criterio d)");
        System.out.println("2. Crear esquema y datos de prueba             (criterio e)");
        System.out.println("3. Insertar / actualizar / borrar              (criterio f)");
        System.out.println("4. Consultas y objetos resultado               (criterios g, h)");
        System.out.println("5. Comprobar el cierre de recursos             (criterio i)");
        System.out.println("6. Transacción correcta (commit)               (criterio j)");
        System.out.println("7. Transacción con fallo (rollback)            (criterio j)");
        System.out.println("8. Procedimientos almacenados [solo MariaDB]   (ampliación)");
        System.out.println("0. Salir");
        System.out.print("Elige: ");
    }

    // ------------------------------------------------------------------ d
    private static void probarConexion() throws SQLException {
        try (Connection con = ConexionBD.conectar()) {
            System.out.println("[OK] Conexión abierta satisfactoriamente");
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("  Gestor: " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("  Driver: " + meta.getDriverName() + " " + meta.getDriverVersion());
        }
    }

    // ------------------------------------------------------------------ e
    private static void crearEsquemaYDatos() throws SQLException, IOException {
        String tipo = ConexionBD.tipoGestor();
        Path esquema = Path.of("sql", "esquema_" + tipo + ".sql");
        Path datos = Path.of("sql", "datos_iniciales.sql");

        try (Connection con = ConexionBD.conectar()) {
            int n1 = EjecutorScript.ejecutar(con, esquema);
            System.out.println("[OK] " + esquema + ": " + n1 + " sentencias ejecutadas.");
            int n2 = EjecutorScript.ejecutar(con, datos);
            System.out.println("[OK] " + datos + ": " + n2 + " sentencias ejecutadas.");
        }
    }

    // ------------------------------------------------------------------ f
    private static void demoModificaciones() throws SQLException {
        System.out.println("-- INSERT --");
        Alumno nuevo = new Alumno(0, "Marta Castillo", new BigDecimal("6.25"),
                "MATRICULADO", LocalDate.of(2026, 11, 20), null, 1);
        int id = alumnoDAO.insertar(nuevo);
        System.out.println("Insertado con id=" + id + " -> " + nuevo);

        System.out.println("-- UPDATE --");
        boolean actualizado = alumnoDAO.actualizarCalificacion(id, new BigDecimal("8.90"), "APROBADO");
        System.out.println("Filas actualizadas: " + (actualizado ? 1 : 0));
        alumnoDAO.buscarPorId(id).ifPresent(a -> System.out.println("Ahora: " + a));

        System.out.println("-- DELETE --");
        boolean borrado = alumnoDAO.eliminar(id);
        System.out.println("Filas borradas: " + (borrado ? 1 : 0));
        System.out.println("¿Sigue existiendo? " + alumnoDAO.buscarPorId(id).isPresent());

        System.out.println("-- Integridad referencial: alumno con un módulo que NO existe --");
        try {
            alumnoDAO.insertar(new Alumno(0, "Alumno Fantasma", new BigDecimal("5.00"),
                    "MATRICULADO", LocalDate.now(), null, 999));
        } catch (SQLException e) {
            System.out.println("La BD lo ha rechazado (es lo esperado): SQLState=" + e.getSQLState()
                    + " | ErrorCode=" + e.getErrorCode());
        }
    }

    // ------------------------------------------------------------------ g, h
    private static void demoConsultas(Scanner teclado) throws SQLException {
        System.out.println("-- Todos los módulos de 2º DAM --");
        for (Modulo m : moduloDAO.listarTodos()) {
            System.out.println(m);
        }

        System.out.println("\n-- Todos los alumnos (cada fila mapeada a objeto Alumno) --");
        for (Alumno a : alumnoDAO.listarTodos()) {
            System.out.println(a);
        }

        System.out.print("\nEstado a filtrar (MATRICULADO / APROBADO / PENDIENTE): ");
        String estado = teclado.nextLine().trim().toUpperCase();
        System.out.println("-- Alumnos en estado '" + estado + "' --");
        for (Alumno a : alumnoDAO.listarPorEstado(estado)) {
            System.out.println(a);
        }

        System.out.println("\n-- Búsqueda por id = 1 (Optional) --");
        Optional<Alumno> encontrado = alumnoDAO.buscarPorId(1);
        System.out.println(encontrado.map(Alumno::toString).orElse("No existe"));

        System.out.println("\n-- Búsqueda por id = 999 (caso sin resultado) --");
        Optional<Alumno> noExiste = alumnoDAO.buscarPorId(999);
        System.out.println(noExiste.map(Alumno::toString).orElse("Alumno con id 999 no encontrado (Optional.empty)"));

        System.out.println("\n-- Recuento de alumnos por estado (GROUP BY) --");
        for (Map.Entry<String, Integer> e : alumnoDAO.contarPorEstado().entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

        System.out.println("\n-- Alumnos con su módulo matriculado (JOIN) --");
        alumnoDAO.listarConModulo().forEach(System.out::println);
    }

    // ------------------------------------------------------------------ j
    private static void demoTransaccion(boolean forzarError) throws SQLException {
        int alumnosAntes = alumnoDAO.listarTodos().size();
        Modulo modAntes = moduloDAO.listarTodos().getFirst();
        System.out.println("Estado ANTES -> Total alumnos: " + alumnosAntes
                + " | Plazas libres en '" + modAntes.getNombre() + "': " + modAntes.getPlazasDisponibles());

        try {
            int idAlumno = transacciones.matricularAlumnoConPlaza(
                    new Alumno(0, "Alumno Transacción " + (System.currentTimeMillis() % 1000),
                            new BigDecimal("7.50"), "MATRICULADO", LocalDate.now(),
                            "Alta transaccional", modAntes.getId()),
                    forzarError);
            System.out.println("[TRANSACCIÓN COMPLETADA] commit() realizado. Alumno creado con id=" + idAlumno);
        } catch (SQLException e) {
            System.out.println("[TRANSACCIÓN FALLIDA] " + e.getMessage() + " -> rollback() realizado.");
        }

        int alumnosDespues = alumnoDAO.listarTodos().size();
        Modulo modDespues = moduloDAO.listarTodos().getFirst();
        System.out.println("Estado DESPUÉS -> Total alumnos: " + alumnosDespues
                + " | Plazas libres en '" + modDespues.getNombre() + "': " + modDespues.getPlazasDisponibles());
    }

    // ------------------------------------------------------------------ ampliación
    private static void demoProcedimientos(Scanner teclado) throws SQLException {
        if (!"mariadb".equals(ConexionBD.tipoGestor())) {
            System.out.println("SQLite no tiene procedimientos almacenados. Configura MariaDB en config.properties.");
            return;
        }
        System.out.print("Estado para sp_listar_por_estado (MATRICULADO / APROBADO / PENDIENTE): ");
        String estado = teclado.nextLine().trim().toUpperCase();
        procedimientos.listarPorEstado(estado).forEach(System.out::println);

        System.out.print("Estado para sp_contar_por_estado: ");
        String estadoContar = teclado.nextLine().trim().toUpperCase();
        System.out.println("Total: " + procedimientos.contarPorEstado(estadoContar));
    }
}