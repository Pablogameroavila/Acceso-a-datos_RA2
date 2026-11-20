package modelo;

/** Una fila de la tabla modulos (criterio g). */
public class Modulo {
    private int id;
    private String codigo;
    private String nombre;
    private int horasTotales;
    private int plazasDisponibles;

    public Modulo() {
    }

    public Modulo(int id, String codigo, String nombre, int horasTotales, int plazasDisponibles) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.horasTotales = horasTotales;
        this.plazasDisponibles = plazasDisponibles;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getHorasTotales() { return horasTotales; }
    public void setHorasTotales(int horasTotales) { this.horasTotales = horasTotales; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public void setPlazasDisponibles(int plazasDisponibles) { this.plazasDisponibles = plazasDisponibles; }

    @Override
    public String toString() {
        return "Modulo [id=" + id + ", codigo=" + codigo + ", nombre=" + nombre
                + ", horasTotales=" + horasTotales + ", plazasDisponibles=" + plazasDisponibles + "]";
    }
}