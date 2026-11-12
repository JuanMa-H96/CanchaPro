package Cerebro;

import Archivos.Cancha;
import Archivos.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;

public class Disponibilidad {

    private PersistenciaCancha persistenciaCanchas;
    private PersistenciaReservas persistenciaReservas;

    public Disponibilidad(PersistenciaCancha persistenciaCanchas, PersistenciaReservas persistenciaReservas){
        this.persistenciaCanchas = persistenciaCanchas;
        this.persistenciaReservas = persistenciaReservas;
    }

   public Cancha[] buscarCanchasDisponibles(LocalDate fecha, LocalTime horaInicio, int duracionHoras, int jugadores){
            if (jugadores <= 0) { return new Cancha[0];}

        Cancha[] disponibles = new Cancha[persistenciaCanchas.getCantidadCanchas()];
        int cantidadDisponibles = 0;

        for (int i = 0; i < persistenciaCanchas.getCantidadCanchas(); i++) {
            Cancha cancha = persistenciaCanchas.getCancha(i);

            if (cancha == null) {continue;}

            boolean puedeUsarse = cancha.isActivo()
                && !cancha.getEstado().equalsIgnoreCase("INACTIVA")
                && !cancha.getEstado().equalsIgnoreCase("MANTENIMIENTO")
                && cancha.getTipoFutbol() == jugadores
                && cancha.getLimitePorEquipo() >= jugadores
                && estaLibre(cancha, fecha, horaInicio, duracionHoras);

            if (puedeUsarse) {
                disponibles[cantidadDisponibles] = cancha;
                cantidadDisponibles++;
            }
        }

        return disponibles;
    }

    private boolean estaLibre(Cancha cancha, LocalDate fecha, LocalTime horaInicio,int duracionHoras) {
        LocalTime horaFinal = horaInicio.plusHours(duracionHoras);
        LocalTime finConMantenimiento = horaFinal.plusMinutes(15);

        if (horaInicio.isBefore(cancha.getHoraApertura())
                || finConMantenimiento.isAfter(cancha.getHoraCierre())) {
            return false;
        }

        for (int i = 0; i < persistenciaReservas.getCantidadReservas(); i++) {
            Reserva reserva = persistenciaReservas.getReserva(i);

            if (reserva == null
                    || reserva.getEstado().equalsIgnoreCase("CANCELADA")
                    || reserva.getEstado().equalsIgnoreCase("FINALIZADA")) {
                continue;
            }

            boolean mismaCancha = reserva.getCancha().getCodigo()
                    .equalsIgnoreCase(cancha.getCodigo());

            boolean mismaFecha = reserva.getFecha().equals(fecha);

            if (mismaCancha && mismaFecha) {
                LocalTime finReservaConMantenimiento =
                        reserva.getHoraFinal().plusMinutes(15);

                boolean seTraslapan =
                        horaInicio.isBefore(finReservaConMantenimiento)
                        && reserva.getHoraInicio().isBefore(finConMantenimiento);

                if (seTraslapan) {
                    return false;
                }
            }
        }

        return true;
    }
}
