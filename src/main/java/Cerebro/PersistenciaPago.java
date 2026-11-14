package Cerebro;
import Archivos.Pago;

import Archivos.Pago;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PersistenciaPago{

    private String ruta = "C:\\Users\\juan1\\OneDrive\\Desktop\\persistencia/pago.csv";

    private static final int CAPACIDAD = 1000;
    private Pago[] pagos = new Pago[CAPACIDAD];
    private int cantidadPagos = 0;

   public void leerPagos() {
    cantidadPagos = 0;

    try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
        lector.readLine(); 

        String linea;

        while ((linea = lector.readLine()) != null
                && cantidadPagos < CAPACIDAD) {

            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] datos = linea.split(",", -1);

            if (datos.length < 5) {
                continue;
            }

            try {
                String codigoPago = datos[0].trim();

                int idReserva = Integer.parseInt(
                        datos[1].replace("R", "").trim()
                );

                java.time.LocalDateTime fechaHora =
                        java.time.LocalDateTime.parse(datos[2].trim());

                double monto = Double.parseDouble(datos[3].trim());
                String metodoPago = datos[4].trim();

           
                String usuarioResponsable = "SIN_REGISTRO";

                if (datos.length > 5 && !datos[5].trim().isEmpty()) {
                    usuarioResponsable = datos[5].trim();
                }

                Pago pago = new Pago(
                        codigoPago,
                        idReserva,
                        fechaHora,
                        monto,
                        metodoPago,
                        usuarioResponsable
                );

                pagos[cantidadPagos] = pago;
                cantidadPagos++;

            } catch (RuntimeException e) {
                System.out.println("Fila de pago ignorada: " + linea);
            }
        }

        System.out.println("Pagos leídos: " + cantidadPagos);

    } catch (IOException e) {
        System.out.println("Error al leer pagos: " + e.getMessage());
    }
}
    
    public boolean agregarPago(Pago pago) {
        if (cantidadPagos >= CAPACIDAD) {
            return false;
        }

        pagos[cantidadPagos] = pago;
        cantidadPagos++;
        return true;
    }
 
    public boolean guardarPagos() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {
            escritor.write(
                    "codigoPago,idReserva,fechaHora,monto,metodoPago,codigoUsuarioResponsable"
            );
            escritor.newLine();

            for (int i = 0; i < cantidadPagos; i++) {
                Pago pago = pagos[i];

                if (pago == null) {
                    continue;
                }

                escritor.write(
                        pago.getCodigoPago() + ","
                        + "R" + String.format("%03d", pago.getIdReserva()) + ","
                        + pago.getFechaHora() + ","
                        + String.format(java.util.Locale.US, "%.2f", pago.getMonto()) + ","
                        + pago.getMetodoPago() + ","
                        + pago.getCodigoUsuarioResponsable()
                );
                escritor.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar pagos: " + e.getMessage());
            return false;
        }
    }
    
    public double getTotalPagadoReserva(int idReserva) {
        double totalPagado = 0;

        for (int i = 0; i < cantidadPagos; i++) {
            Pago pago = pagos[i];

            if (pago != null && pago.getIdReserva() == idReserva) {
                totalPagado += pago.getMonto();
            }
        }

        return totalPagado;
    }
    
    
    public double getTotalPagadoEnFecha(java.time.LocalDate fecha) {
    double total = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago != null
                && pago.getFechaHora().toLocalDate().equals(fecha)) {
            total += pago.getMonto();
        }
    }

    return total;
}
    
}