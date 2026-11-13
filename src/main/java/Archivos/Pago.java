package Archivos;

import java.time.LocalDateTime;

public class Pago {

    private String codigoPago;
    private int idReserva;
    private LocalDateTime fechaHora;
    private double monto;
    private String metodoPago;

    public Pago(
            String codigoPago,
            int idReserva,
            LocalDateTime fechaHora,
            double monto,
            String metodoPago
    ) {
        this.codigoPago = codigoPago;
        this.idReserva = idReserva;
        this.fechaHora = fechaHora;
        this.monto = monto;
        this.metodoPago = metodoPago;
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
}