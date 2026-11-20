package app;

import dao.AlumnoDAO;
import modelo.Alumno;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class DemoModificaciones {

    public static void main(String[] args) throws Exception {
        AlumnoDAO dao = new AlumnoDAO();
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Haz SELECT * FROM alumnos; en MariaDB -> captura 'AD-2.f_insert_antes.png' y pulsa ENTER...");
        sc.nextLine();

        Alumno nuevo = new Alumno(0, "Marta Castillo", new BigDecimal("6.25"), "MATRICULADO",
                LocalDate.of(2026, 11, 20), null, 1);
        int idGenerado = dao.insertar(nuevo);
        System.out.println("[INSERT OK] Clave generada id=" + idGenerado + " -> " + nuevo);

        System.out.println("2. Haz SELECT * FROM alumnos; en MariaDB -> captura 'AD-2.f_insert_despues.png' (y 'AD-2.f_update_antes.png') y pulsa ENTER...");
        sc.nextLine();

        boolean actualizado = dao.actualizarCalificacion(idGenerado, new BigDecimal("8.90"), "APROBADO");
        System.out.println("[UPDATE OK] Filas afectadas: " + (actualizado ? 1 : 0));

        System.out.println("3. Haz SELECT * FROM alumnos; en MariaDB -> captura 'AD-2.f_update_despues.png' (y 'AD-2.f_delete_antes.png') y pulsa ENTER...");
        sc.nextLine();

        boolean borrado = dao.eliminar(idGenerado);
        System.out.println("[DELETE OK] Filas afectadas: " + (borrado ? 1 : 0));

        System.out.println("4. Haz SELECT * FROM alumnos; en MariaDB -> captura 'AD-2.f_delete_despues.png' y pulsa ENTER...");
        sc.nextLine();
    }
}