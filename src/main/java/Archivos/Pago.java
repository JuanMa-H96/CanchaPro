package Archivos;

import java.time.LocalDateTime;

public class Pago {

    private String codigoPago;
    private int idReserva;
    private LocalDateTime fechaHora;
    private double monto;
    private String metodoPago;
    private String codigoUsuarioResponsable;

    public Pago(
            String codigoPago,
            int idReserva,
            LocalDateTime fechaHora,
            double monto,
            String metodoPago,
            String codigoUsuarioResponsable) {

        if (codigoPago == null || codigoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de pago es obligatorio.");
        }

        if (idReserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser válido.");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }

        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }

        if (codigoUsuarioResponsable == null
                || codigoUsuarioResponsable.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario responsable es obligatorio.");
        }

        this.codigoPago = codigoPago.trim();
        this.idReserva = idReserva;
        this.fechaHora = fechaHora;
        this.monto = Math.round(monto * 100.0) / 100.0;
        this.metodoPago = metodoPago.trim();
        this.codigoUsuarioResponsable = codigoUsuarioResponsable.trim();
    }

    public String getCodigoPago() {
        return codigoPago;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public double getMonto() {
        return monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public String getCodigoUsuarioResponsable() {
        return codigoUsuarioResponsable;
    }
}