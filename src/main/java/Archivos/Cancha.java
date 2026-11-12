package Archivos;

import java.time.LocalTime;

public class Cancha {

    private String codigo;
    private String nombre;
    private int tipoFutbol;
    private double precioHora;
    private String estado;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean activo = true;

    public Cancha(String codigo, String nombre, int tipoFutbol, double precioHora, String estado, LocalTime horaApertura, LocalTime horaCierre){
        this.codigo = codigo;
        this.nombre = nombre;
        setTipoFutbol(tipoFutbol);
        this.precioHora = precioHora;
        this.estado = estado;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
    }

    public int getLimitePorEquipo() {
        if (tipoFutbol == 5) {
            return 8;
        } else if (tipoFutbol == 7) {
            return 10;
        } else {
            return 15;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setTipoFutbol(int tipoFutbol) {
        if (tipoFutbol == 5 || tipoFutbol == 7 || tipoFutbol == 11) {
            this.tipoFutbol = tipoFutbol;
        } else {
            throw new IllegalArgumentException(
                "El tipo de fútbol debe ser 5, 7 u 11."
            );
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getTipoFutbol() {
        return tipoFutbol;
    }

    public double getPrecioHora() {
        return precioHora;
    }

    public void setPrecioHora(double precioHora) {
        this.precioHora = precioHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalTime getHoraApertura() {
        return horaApertura;
    }

    public void setHoraApertura(LocalTime horaApertura) {
        this.horaApertura = horaApertura;
    }

    public LocalTime getHoraCierre() {
        return horaCierre;
    }

    public void setHoraCierre(LocalTime horaCierre) {
        this.horaCierre = horaCierre;
    }
}