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

    private String ruta = "C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\persistencia/pagos.csv";

    private static final int CAPACIDAD = 1000;
    private Pago[] pagos = new Pago[CAPACIDAD];
    private int cantidadPagos = 0;

   public void leerPagos() {
    cantidadPagos = 0;

    try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
        lector.readLine(); // Saltar encabezado

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

                String usuario = "SIN_REGISTRO";
                if (datos.length > 5 && !datos[5].trim().isEmpty()) {
                    usuario = datos[5].trim();
                }

                // Compatibilidad con filas anteriores del CSV.
                double montoCancha = monto;
                double montoTienda = 0;

                if (datos.length >= 8
                        && !datos[6].trim().isEmpty()
                        && !datos[7].trim().isEmpty()) {
                    montoCancha = Double.parseDouble(datos[6].trim());
                    montoTienda = Double.parseDouble(datos[7].trim());
                }

                pagos[cantidadPagos] = new Pago(
                        codigoPago,
                        idReserva,
                        fechaHora,
                        monto,
                        metodoPago,
                        usuario,
                        montoCancha,
                        montoTienda
                );

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
    
 public double getTotalPagadoReserva(int idReserva) {
    double total = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago != null && pago.getIdReserva() == idReserva) {
            total += pago.getMontoCancha();
        }
    }

    return total;
}

public double getTotalPagadoTienda(int idReserva) {
    double total = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago != null && pago.getIdReserva() == idReserva) {
            total += pago.getMontoTienda();
        }
    }

    return total;
}

public double getTotalPagadoGeneral(int idReserva) {
    double total = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago != null && pago.getIdReserva() == idReserva) {
            total += pago.getMonto();
        }
    }

    return total;
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
    
  
        public void mostrarPagos(javax.swing.JTable tabla, String filtro) {
        String[] columnas = {
            "Código de pago", "Reserva", "Fecha y hora", "Monto", "Método de pago"
        };

        javax.swing.table.DefaultTableModel modelo =
                new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        for (int i = 0; i < cantidadPagos; i++) {
            Pago pago = pagos[i];

            if (pago == null) {
                continue;
            }

            if (!filtro.equalsIgnoreCase("TODOS")
                    && !pago.getMetodoPago().equalsIgnoreCase(filtro)) {
                continue;
            }

            modelo.addRow(new Object[]{
                pago.getCodigoPago(),
                "R" + String.format("%03d", pago.getIdReserva()),
                pago.getFechaHora(),
                String.format("Q %.2f", pago.getMonto()),
                pago.getMetodoPago()
            });
        }

        tabla.setModel(modelo);
    }
    

public boolean guardarPagos() {
    try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {

        escritor.write(
                "codigoPago,idReserva,fechaHora,monto,metodoPago,"
                + "codigoUsuarioResponsable,montoCancha,montoTienda"
        );
        escritor.newLine();

        for (int i = 0; i < cantidadPagos; i++) {
            Pago pago = pagos[i];

            if (pago != null) {
                escritor.write(
                        pago.getCodigoPago() + ","
                        + "R" + String.format("%03d", pago.getIdReserva()) + ","
                        + pago.getFechaHora() + ","
                        + String.format(
                                java.util.Locale.US, "%.2f", pago.getMonto()
                        ) + ","
                        + pago.getMetodoPago() + ","
                        + pago.getCodigoUsuarioResponsable() + ","
                        + String.format(
                                java.util.Locale.US, "%.2f",
                                pago.getMontoCancha()
                        ) + ","
                        + String.format(
                                java.util.Locale.US, "%.2f",
                                pago.getMontoTienda()
                        )
                );
                escritor.newLine();
            }
        }

        return true;

    } catch (IOException e) {
        System.out.println("Error al guardar pagos: " + e.getMessage());
        return false;
    }
}
    
    public void mostrarIngresosPorPeriodo(
        javax.swing.JTable tabla,
        java.time.LocalDate desde,
        java.time.LocalDate hasta) {

    String[] columnas = {
        "Código de pago", "Reserva", "Fecha y hora",
        "Monto", "Método de pago"
    };

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    double totalIngresos = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago == null) {
            continue;
        }

        java.time.LocalDate fechaPago = pago.getFechaHora().toLocalDate();

        if (!fechaPago.isBefore(desde) && !fechaPago.isAfter(hasta)) {
            modelo.addRow(new Object[]{
                pago.getCodigoPago(),
                "R" + String.format("%03d", pago.getIdReserva()),
                pago.getFechaHora(),
                String.format("Q %.2f", pago.getMonto()),
                pago.getMetodoPago()
            });

            totalIngresos += pago.getMonto();
        }
    }

    modelo.addRow(new Object[]{
        "", "", "", String.format("Q %.2f", totalIngresos), "TOTAL INGRESOS"
    });

    tabla.setModel(modelo);
}
    
    public void mostrarIngresosTiendaPorPeriodo(
        javax.swing.JTable tabla,
        java.time.LocalDate desde,
        java.time.LocalDate hasta) {

    String[] columnas = {
        "Código de pago",
        "Reserva",
        "Fecha y hora",
        "Ingreso tienda",
        "Método de pago",
        "Usuario"
    };

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    double totalIngresosTienda = 0;

    for (int i = 0; i < cantidadPagos; i++) {
        Pago pago = pagos[i];

        if (pago == null || pago.getMontoTienda() <= 0) {
            continue;
        }

        java.time.LocalDate fechaPago = pago.getFechaHora().toLocalDate();

        if (!fechaPago.isBefore(desde) && !fechaPago.isAfter(hasta)) {
            modelo.addRow(new Object[]{
                pago.getCodigoPago(),
                "R" + String.format("%03d", pago.getIdReserva()),
                pago.getFechaHora(),
                String.format("Q %.2f", pago.getMontoTienda()),
                pago.getMetodoPago(),
                pago.getCodigoUsuarioResponsable()
            });

            totalIngresosTienda += pago.getMontoTienda();
        }
    }

    modelo.addRow(new Object[]{
        "", "", "TOTAL DE INGRESOS DE TIENDA",
        String.format("Q %.2f", totalIngresosTienda), "", ""
    });

    tabla.setModel(modelo);
}
    
    
}