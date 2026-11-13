package Cerebro;

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

        try {
            BufferedReader lector = new BufferedReader(new FileReader(ruta));
            String linea = lector.readLine(); // Saltar encabezado

            while ((linea = lector.readLine()) != null
                    && cantidadPagos < CAPACIDAD) {

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

                    Pago pago = new Pago(
                            codigoPago,
                            idReserva,
                            fechaHora,
                            monto,
                            metodoPago
                    );

                    pagos[cantidadPagos] = pago;
                    cantidadPagos++;

                } catch (RuntimeException e) {
                    System.out.println("Fila de pago ignorada: " + linea);
                }
            }

            lector.close();
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
 
    public boolean guardarPago(Pago pago) {
        File archivo = new File(ruta);
        boolean escribirEncabezado = !archivo.exists() || archivo.length() == 0;

        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(archivo, true))) {

            if (escribirEncabezado) {
                escritor.write(
                    "codigoPago,idReserva,fechaHora,monto,metodoPago"
                );
                escritor.newLine();
            }

            escritor.write(
                    pago.getCodigoPago() + ","
                    + "R" + String.format("%03d", pago.getIdReserva()) + ","
                    + pago.getFechaHora() + ","
                    + String.format("%.2f", pago.getMonto()) + ","
                    + pago.getMetodoPago()
            );
            escritor.newLine();

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar el pago: " + e.getMessage());
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
    
    
    
    
}