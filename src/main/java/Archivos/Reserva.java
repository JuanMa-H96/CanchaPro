package Archivos;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reserva {

    private int idReserva;
    private Cliente cliente;
    private Cancha cancha;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private int duracionHoras;
    private LocalTime horaFinal;
    private String estado;

    private int balon;
    private int arbitro;
    private int chalecos;
    
    private double subtotalCancha;
    private double totalExtras;
    private double totalReserva;
    

    public Reserva(int idReserva, Cliente cliente, Cancha cancha, LocalDate fecha, LocalTime horaInicio, int duracionHoras,
            int balon, int arbitro, int chalecos){
       
        validarCantidad(balon);
        validarCantidad(arbitro);
        validarCantidad(chalecos);

        this.idReserva = idReserva;
        this.cliente = cliente;
        this.cancha = cancha;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.duracionHoras = duracionHoras;
        this.horaFinal = horaInicio.plusHours(duracionHoras);
        this.estado = "PENDIENTE";

        this.balon = balon;
        this.arbitro = arbitro;
        this.chalecos = chalecos;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Cancha getCancha() {
        return cancha;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFinal() {
        return horaFinal;
    }

    public int getDuracionHoras() {
        return duracionHoras;
    }

    public String getEstado() {
        return estado;
    }

    public int getBalon() {
        return balon;
    }

    public int getArbitro() {
        return arbitro;
    }

    public int getChalecos() {
        return chalecos;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setCancha(Cancha cancha) {
        this.cancha = cancha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
        actualizarHoraFinal();
    }

    public void setDuracionHoras(int duracionHoras) {
        if (duracionHoras <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que cero.");
        }

        this.duracionHoras = duracionHoras;
        actualizarHoraFinal();
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setBalon(int balon) {
        validarCantidad(balon);
        this.balon = balon;
    }

    public void setArbitro(int arbitro) {
        validarCantidad(arbitro);
        this.arbitro = arbitro;
    }

    public void setChalecos(int chalecos) {
        validarCantidad(chalecos);
        this.chalecos = chalecos;
    }

    public void cancelar() {
        estado = "CANCELADA";
    }

    private void actualizarHoraFinal() {
        if (horaInicio != null && duracionHoras > 0) {
            horaFinal = horaInicio.plusHours(duracionHoras);
        }
    }

    private void validarCantidad(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa.");
        }
    }
    
    public double getSubtotalCancha() {
    return subtotalCancha;
    }

    public void setSubtotalCancha(double subtotalCancha) {
        this.subtotalCancha = subtotalCancha;
    }

    public double getTotalExtras() {
        return totalExtras;
    }

    public void setTotalExtras(double totalExtras) {
        this.totalExtras = totalExtras;
    }

    public double getTotalReserva() {
        return totalReserva;
    }

    public void setTotalReserva(double totalReserva) {
        this.totalReserva = totalReserva;
    }
}