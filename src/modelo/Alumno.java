package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Una fila de la tabla alumnos (criterio g). Incluye 4 tipos distintos (int, String, BigDecimal, LocalDate). */
public class Alumno {
    private int id;
    private String nombre;
    private BigDecimal notaMedia;
    private String estado;          // MATRICULADO | APROBADO | PENDIENTE
    private LocalDate fechaMatricula;
    private String observaciones;   // columna opcional: puede ser null
    private int idModulo;           // clave foránea hacia modulos(id)

    public Alumno() {
    }

    public Alumno(int id, String nombre, BigDecimal notaMedia, String estado,
                  LocalDate fechaMatricula, String observaciones, int idModulo) {
        this.id = id;
        this.nombre = nombre;
        this.notaMedia = notaMedia;
        this.estado = estado;
        this.fechaMatricula = fechaMatricula;
        this.observaciones = observaciones;
        this.idModulo = idModulo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public BigDecimal getNotaMedia() { return notaMedia; }
    public void setNotaMedia(BigDecimal notaMedia) { this.notaMedia = notaMedia; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaMatricula() { return fechaMatricula; }
    public void setFechaMatricula(LocalDate fechaMatricula) { this.fechaMatricula = fechaMatricula; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public int getIdModulo() { return idModulo; }
    public void setIdModulo(int idModulo) { this.idModulo = idModulo; }

    @Override
    public String toString() {
        return "Alumno [id=" + id + ", nombre=" + nombre + ", notaMedia=" + notaMedia
                + ", estado=" + estado + ", fechaMatricula=" + fechaMatricula
                + ", observaciones=" + (observaciones != null ? observaciones : "(sin observaciones)")
                + ", idModulo=" + idModulo + "]";
    }
}