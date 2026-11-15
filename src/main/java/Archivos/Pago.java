package Archivos;

import java.time.LocalDateTime;

public class Pago {

    
    private String codigoPago;
    private int idReserva;
    private LocalDateTime fechaHora;
    private double monto;
    private String metodoPago;
    private String codigoUsuarioResponsable;
    private double montoCancha;
    private double montoTienda;

    // Constructor para los pagos existentes: se asignan a cancha y extras.
    public Pago(
            String codigoPago,
            int idReserva,
            LocalDateTime fechaHora,
            double monto,
            String metodoPago,
            String codigoUsuarioResponsable) {

        this(
                codigoPago,
                idReserva,
                fechaHora,
                monto,
                metodoPago,
                codigoUsuarioResponsable,
                monto,
                0
        );
    }

    // Constructor para pagos distribuidos entre cancha y tienda.
    public Pago(
            String codigoPago,
            int idReserva,
            LocalDateTime fechaHora,
            double monto,
            String metodoPago,
            String codigoUsuarioResponsable,
            double montoCancha,
            double montoTienda) {

        if (codigoPago == null || codigoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de pago es obligatorio.");
        }

        if (idReserva <= 0) {
            throw new IllegalArgumentException("El ID de reserva debe ser válido.");
        }

        if (fechaHora == null) {
            throw new IllegalArgumentException("La fecha y hora son obligatorias.");
        }

        if (!Double.isFinite(monto) || monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }

        if (codigoUsuarioResponsable == null
                || codigoUsuarioResponsable.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El usuario responsable es obligatorio."
            );
        }

        if (!Double.isFinite(montoCancha) || montoCancha < 0
                || !Double.isFinite(montoTienda) || montoTienda < 0) {
            throw new IllegalArgumentException(
                    "La distribución del pago no puede tener montos negativos."
            );
        }

        long montoCentavos = Math.round(monto * 100);
        long canchaCentavos = Math.round(montoCancha * 100);
        long tiendaCentavos = Math.round(montoTienda * 100);

        if (canchaCentavos + tiendaCentavos != montoCentavos) {
            throw new IllegalArgumentException(
                    "Los montos de cancha y tienda deben sumar el pago."
            );
        }

        this.codigoPago = codigoPago.trim();
        this.idReserva = idReserva;
        this.fechaHora = fechaHora;
        this.monto = montoCentavos / 100.0;
        this.metodoPago = metodoPago.trim();
        this.codigoUsuarioResponsable = codigoUsuarioResponsable.trim();
        this.montoCancha = canchaCentavos / 100.0;
        this.montoTienda = tiendaCentavos / 100.0;
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

    public double getMontoCancha() {
        return montoCancha;
    }

    public double getMontoTienda() {
        return montoTienda;
    }
}