package Cerebro;
import Archivos.Reserva;
import Archivos.Cliente;
import Archivos.Cancha;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PersistenciaReservas {

    private PersistenciaClientes persistenciaClientes;
    private PersistenciaCancha persistenciaCanchas;

    public PersistenciaReservas(PersistenciaClientes persistenciaClientes,PersistenciaCancha persistenciaCanchas) {
        this.persistenciaClientes = persistenciaClientes;
        this.persistenciaCanchas = persistenciaCanchas;
    }
        
   String rutareserva = "C:\\Users\\juan1\\OneDrive\\Desktop\\persistencia/reservas.csv";
   
    private static final int CAPACIDAD = 1000;
    private Reserva[] reservas = new Reserva[CAPACIDAD];
    private int cantidad;

    
    public void leerReservas() {
        cantidad=0;
    try {
        BufferedReader lector = new BufferedReader(new FileReader(rutareserva));
        String linea = lector.readLine(); // Lee el encabezado

        while ((linea = lector.readLine()) != null && cantidad < 1000) {
            String[] datos = linea.split(",", -1);

            int idReserva = Integer.parseInt(datos[0].replace("R", "").trim());

            Cliente cliente = persistenciaClientes.buscarCliente(datos[1].trim());
            Cancha cancha = persistenciaCanchas.buscarCancha(datos[2].trim());

          Reserva reserva = new Reserva(
            idReserva,
            cliente,
            cancha,
            LocalDate.parse(datos[3].trim()),
            LocalTime.parse(datos[5].trim()),
            Integer.parseInt(datos[6].trim()),
            Integer.parseInt(datos[8].trim()),  // cantidad de balones
            Integer.parseInt(datos[9].trim()),  // cantidad de árbitros
            Integer.parseInt(datos[10].trim())  // cantidad de chalecos
    );

                reserva.setEstado(datos[4].trim());
                if (datos.length >= 14) {
                    reserva.setSubtotalCancha(Double.parseDouble(datos[11]));
                    reserva.setTotalExtras(Double.parseDouble(datos[12]));
                    reserva.setTotalReserva(Double.parseDouble(datos[13]));
                }

                reservas[cantidad] = reserva;
                cantidad++;
        }
        
        ordenarReservas();
        lector.close();

        System.out.println("Reservas leídas: " + cantidad);

    } catch (IOException e) {
        System.out.println("Error al leer el archivo: " + e.getMessage());
    }
    
    
}
    
    private void ordenarReservas() {
    for (int i = 0; i < cantidad - 1; i++) {
        for (int j = i + 1; j < cantidad; j++) {
            if (reservas[i].getFecha().isAfter(reservas[j].getFecha())
                    || (reservas[i].getFecha().equals(reservas[j].getFecha())
                    && reservas[i].getHoraInicio()
                            .isAfter(reservas[j].getHoraInicio()))) {

                Reserva temporal = reservas[i];
                reservas[i] = reservas[j];
                reservas[j] = temporal;
            }
        }
    }
}
  
    public Reserva buscarReserva(int id) {
    for (int i = 0; i < cantidad; i++) {
        if (reservas[i] != null
                && reservas[i].getIdReserva() == id) {
            return reservas[i];
        }
    }

    return null;
}
    
    public boolean cancelarReserva(int idReserva) {
        for (int i = 0; i < cantidad; i++) {
            Reserva reserva = reservas[i];

            if (reserva != null && reserva.getIdReserva() == idReserva) {
                String estado = reserva.getEstado();

                if (estado.equalsIgnoreCase("CANCELADA")
                        || estado.equalsIgnoreCase("FINALIZADA")
                        || estado.equalsIgnoreCase("VENCIDA")) {
                    return false;
                }

                reserva.cancelar();
                return true;
            }
        }

        return false; // No se encontró la reserva
    }  
    
    public boolean modificarReserva(
        int idReserva,
        Cancha cancha,
        LocalDate fecha,
        LocalTime horaInicio,
        int duracionHoras,
        int balones,
        int arbitros,
        int chalecos,
        double subtotalCancha,
        double totalExtras,
        double totalReserva) {

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva != null && reserva.getIdReserva() == idReserva) {
            String estado = reserva.getEstado();

            if (!estado.equalsIgnoreCase("PENDIENTE")
                    && !estado.equalsIgnoreCase("CONFIRMADA")) {
                return false;
            }

            reserva.setCancha(cancha);
            reserva.setFecha(fecha);
            reserva.setHoraInicio(horaInicio);
            reserva.setDuracionHoras(duracionHoras);
            reserva.setBalon(balones);
            reserva.setArbitro(arbitros);
            reserva.setChalecos(chalecos);
            reserva.setSubtotalCancha(subtotalCancha);
            reserva.setTotalExtras(totalExtras);
            reserva.setTotalReserva(totalReserva);

            ordenarReservas();
            return true;
        }
    }

    return false;
}
    
    
    public void mostrarReservas(JTable tabla) {
        String[] columnas = {
            "Código", "Cliente", "Cancha", "Fecha", "Horario", "Estado"
        };

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        LocalDate fechaHoy = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        for (int i = 0; i < cantidad; i++) {
            Reserva reserva = reservas[i];

            if (reserva == null) {
                continue;
            }

            String estado = reserva.getEstado();

            boolean esPendiente = estado.equalsIgnoreCase("PENDIENTE");
            boolean esConfirmada = estado.equalsIgnoreCase("CONFIRMADA");

            boolean estaEnCursoAhora =
                    estado.equalsIgnoreCase("EN_CURSO")
                    && reserva.getFecha().equals(fechaHoy)
                    && !horaActual.isBefore(reserva.getHoraInicio())
                    && horaActual.isBefore(reserva.getHoraFinal());

            if (esPendiente || esConfirmada || estaEnCursoAhora) {
                modelo.addRow(new Object[]{
                    "R" + String.format("%03d", reserva.getIdReserva()),
                    reserva.getCliente().getNombre(),
                    reserva.getCancha().getNombre(),
                    reserva.getFecha(),
                    reserva.getHoraInicio() + " - " + reserva.getHoraFinal(),
                    reserva.getEstado()
                });
            }
        }

        tabla.setModel(modelo);
    }
    
    public void mostrarTodasReservas(
        JTable tabla,
        PersistenciaPago persistenciaPagos
) {
    String[] columnas = {
        "Reserva (código)",
        "Cliente (nombre)",
        "Cancha (nombre)",
        "Fecha",
        "Horario (inicio-final)",
        "Duración",
        "Estado",
        "Balones",
        "Árbitros",
        "Chalecos",
        "Total Q",
        "Cancelado Q",
        "Pendiente Q"
    };

    DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva == null) {
            continue;
        }

        double total = reserva.getTotalReserva();
        double pagado =
                persistenciaPagos.getTotalPagadoReserva(
                        reserva.getIdReserva()
                );
        double pendiente = Math.max(0, total - pagado);

        modelo.addRow(new Object[]{
            "R" + String.format("%03d", reserva.getIdReserva()),
            reserva.getCliente().getNombre(),
            reserva.getCancha().getNombre(),
            reserva.getFecha(),
            reserva.getHoraInicio() + " - " + reserva.getHoraFinal(),
            reserva.getDuracionHoras(),
            reserva.getEstado(),
            reserva.getBalon(),
            reserva.getArbitro(),
            reserva.getChalecos(),
            String.format("Q %.2f", total),
            String.format("Q %.2f", pagado),
            String.format("Q %.2f", pendiente)
        });
    }

    tabla.setModel(modelo);
}
    
    public int getCantidadReservas() {
    return cantidad;
    }

    public Reserva getReserva(int posicion) {
    return reservas[posicion];
    }   
    
    public int generarSiguienteId() {
    int mayorId = 0;

    for (int i = 0; i < cantidad; i++) {
        if (reservas[i] != null
                && reservas[i].getIdReserva() > mayorId) {
            mayorId = reservas[i].getIdReserva();
        }
    }

    return mayorId + 1;
}

    public boolean agregarReserva(Reserva reserva) {
            if (cantidad >= CAPACIDAD) {
                return false;
            }

            reservas[cantidad] = reserva;
            cantidad++;

            ordenarReservas();
            return true;
        }

    public void actualizarEstadosFinalizados() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva == null) {
            continue;
        }

        String estado = reserva.getEstado();

        boolean puedeFinalizar =
                estado.equalsIgnoreCase("CONFIRMADA")
                || estado.equalsIgnoreCase("EN_CURSO");

        java.time.LocalDateTime finReserva =
                java.time.LocalDateTime.of(
                        reserva.getFecha(),
                        reserva.getHoraFinal()
                );

                if (!finReserva.isAfter(ahora)) {
                    if (estado.equalsIgnoreCase("CONFIRMADA")
                            || estado.equalsIgnoreCase("EN_CURSO")) {
                        reserva.setEstado("FINALIZADA");

                    } else if (estado.equalsIgnoreCase("PENDIENTE")) {
                        reserva.setEstado("VENCIDA");
                    }
                }
    }
}
    
    
    
    
    
    
    
    
    
    
    
    public boolean guardarReservas() {
        try (java.io.BufferedWriter escritor =
                new java.io.BufferedWriter(new java.io.FileWriter(rutareserva))) {

            escritor.write(
                "idReserva,codigoCliente,codigoCancha,fecha,estado,"
                + "horaInicio,duracion,horaFinal,balon,arbitro,chalecos,"
                + "subtotalCancha,totalExtras,totalReserva"
            );
            escritor.newLine();

            for (int i = 0; i < cantidad; i++) {
                Reserva r = reservas[i];

                escritor.write(
                    "R" + String.format("%03d", r.getIdReserva()) + ","
                    + r.getCliente().getCodigo() + ","
                    + r.getCancha().getCodigo() + ","
                    + r.getFecha() + ","
                    + r.getEstado() + ","
                    + r.getHoraInicio() + ","
                    + r.getDuracionHoras() + ","
                    + r.getHoraFinal() + ","
                    + r.getBalon() + ","
                    + r.getArbitro() + ","
                    + r.getChalecos() + ","
                    + String.format("%.2f", r.getSubtotalCancha()) + ","
                    + String.format("%.2f", r.getTotalExtras()) + ","
                    + String.format("%.2f", r.getTotalReserva())
                );
                escritor.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar reservas: " + e.getMessage());
            return false;
        }
    }

    
    
      
}