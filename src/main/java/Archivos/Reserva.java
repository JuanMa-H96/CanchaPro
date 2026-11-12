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

    private boolean balon;
    private boolean arbitro;
    private boolean chalecos;

    public Reserva(
            int idReserva,Cliente cliente,Cancha cancha,LocalDate fecha,LocalTime horaInicio,int duracionHoras,
            boolean balon,boolean arbitro,boolean chalecos){
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

    public boolean isBalon() {
        return balon;
    }

    public boolean isArbitro() {
        return arbitro;
    }

    public boolean isChalecos() {
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
        this.duracionHoras = duracionHoras;
        actualizarHoraFinal();
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setBalon(boolean balon) {
        this.balon = balon;
    }

    public void setArbitro(boolean arbitro) {
        this.arbitro = arbitro;
    }

    public void setChalecos(boolean chalecos) {
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
}