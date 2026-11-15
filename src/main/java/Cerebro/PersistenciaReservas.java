package Cerebro;
import Archivos.Reserva;
import Archivos.Cliente;
import Archivos.Cancha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    
        
   String rutareserva = "C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\persistencia/reservas.csv";
   
    private static final int CAPACIDAD = 1000;
    private Reserva[] reservas = new Reserva[CAPACIDAD];
    private int cantidad;
    private CalcularPrecio calculadora = new CalcularPrecio();

    
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
            Integer.parseInt(datos[8].trim()),  
            Integer.parseInt(datos[9].trim()), 
            Integer.parseInt(datos[10].trim())  
    );

                reserva.setEstado(datos[4].trim());
               
                        if (datos.length >= 14) {
                            reserva.setSubtotalCancha(
                                    Double.parseDouble(datos[11].trim())
                            );
                            reserva.setTotalExtras(
                                    Double.parseDouble(datos[12].trim())
                            );
                            reserva.setTotalReserva(
                                    Double.parseDouble(datos[13].trim())
                            );
                        }
       
                    if (datos.length > 14 && !datos[14].trim().isEmpty()) {
                        reserva.setFechaCancelacion(
                                java.time.LocalDateTime.parse(datos[14].trim())
                        );
                    }

                    if (datos.length > 15) {
                        reserva.setMotivoCancelacion(datos[15].trim());
                    }

                    if (datos.length > 16) {
                        reserva.setUsuarioResponsableCancelacion(datos[16].trim());
                    }

                reservas[cantidad] = reserva;
                cantidad++;
        }
        
        ordenarReservas();
        lector.close();

        System.out.println("Reservas leídas: " + cantidad);

   } catch (IOException | RuntimeException e) {
    System.out.println("Error al leer reservas: " + e.getMessage());
    e.printStackTrace();
    ordenarReservas();
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
    
    public boolean cancelarReserva(
        int idReserva,
        String motivo,
        String usuarioResponsable) {

    if (motivo == null || motivo.trim().isEmpty()
            || usuarioResponsable == null
            || usuarioResponsable.trim().isEmpty()) {
        return false;
    }

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva != null && reserva.getIdReserva() == idReserva) {
            String estado = reserva.getEstado();

            if (estado.equalsIgnoreCase("CANCELADA")
                    || estado.equalsIgnoreCase("FINALIZADA")
                    || estado.equalsIgnoreCase("VENCIDA")) {
                return false;
            }

            reserva.cancelar(motivo, usuarioResponsable);

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
        DefaultTableModel modelo = new DefaultTableModel() {
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

        public void mostrarReservasPorEstado(
               javax.swing.JTable tabla,
               String estadoSeleccionado,
               PersistenciaPago persistenciaPagos) {

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
               "Pagado Q",
               "Pendiente Q",
               "Fecha de cancelación",
               "Usuario responsable",
               "Motivo"
           };

           javax.swing.table.DefaultTableModel modelo =
                   new javax.swing.table.DefaultTableModel(columnas, 0) {
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

               // Si se eligió un estado específico, omite las demás reservas.
               if (!estadoSeleccionado.equalsIgnoreCase("TODAS")
                       && !reserva.getEstado().equalsIgnoreCase(estadoSeleccionado)) {
                   continue;
               }

               double totalReserva = reserva.getTotalReserva();
               double pagado = persistenciaPagos.getTotalPagadoReserva(
                       reserva.getIdReserva()
               );
               double pendiente = Math.max(0, totalReserva - pagado);

               // Las cotizaciones no se presentan como deuda.
               if (reserva.getEstado().equalsIgnoreCase("PENDIENTE")) {
                   pagado = 0;
                   pendiente = 0;
               }

               modelo.addRow(new Object[]{
                   "R" + String.format("%03d", reserva.getIdReserva()),
                   reserva.getCliente() == null ? "" : reserva.getCliente().getNombre(),
                   reserva.getCancha() == null ? "" : reserva.getCancha().getNombre(),
                   reserva.getFecha(),
                   reserva.getHoraInicio() + " - " + reserva.getHoraFinal(),
                   reserva.getDuracionHoras(),
                   reserva.getEstado(),
                   reserva.getBalon(),
                   reserva.getArbitro(),
                   reserva.getChalecos(),
                   String.format("Q %.2f", totalReserva),
                   String.format("Q %.2f", pagado),
                   String.format("Q %.2f", pendiente),
                   reserva.getFechaCancelacion() == null
                           ? ""
                           : reserva.getFechaCancelacion().toString().replace("T", " "),
                   reserva.getUsuarioResponsableCancelacion() == null
                           ? ""
                           : reserva.getUsuarioResponsableCancelacion(),
                   reserva.getMotivoCancelacion() == null
                           ? ""
                           : reserva.getMotivoCancelacion()
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
    
    public int vencerCotizacionesEnConflicto(
        String codigoCancha,
        LocalDate fecha,
        LocalTime horaInicio,
        int duracionHoras) {

        int cantidadVencidas = 0;

        LocalTime finNuevo =
                horaInicio.plusHours(duracionHoras).plusMinutes(15);

        for (int i = 0; i < cantidad; i++) {
            Reserva reserva = reservas[i];

            if (reserva == null
                    || !reserva.getEstado().equalsIgnoreCase("PENDIENTE")
                    || !reserva.getCancha().getCodigo().equalsIgnoreCase(codigoCancha)
                    || !reserva.getFecha().equals(fecha)) {
                continue;
            }

            LocalTime finCotizacion =
                    reserva.getHoraFinal().plusMinutes(15);

            boolean seTraslapan =
                    horaInicio.isBefore(finCotizacion)
                    && reserva.getHoraInicio().isBefore(finNuevo);

            if (seTraslapan) {
                reserva.setEstado("VENCIDA");
                cantidadVencidas++;
            }
        }

        return cantidadVencidas;
    }
    
   public boolean actualizarEstadosPorHora() {
    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
    boolean huboCambios = false;

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva == null) {
            continue;
        }

        String estado = reserva.getEstado();

        java.time.LocalDateTime inicio = java.time.LocalDateTime.of(
                reserva.getFecha(),
                reserva.getHoraInicio()
        );

        java.time.LocalDateTime fin = java.time.LocalDateTime.of(
                reserva.getFecha(),
                reserva.getHoraFinal()
        );

        // Una cotización sin pago vence cuando llega su hora de inicio.
        if (estado.equalsIgnoreCase("PENDIENTE")
                && !ahora.isBefore(inicio)) {
            reserva.setEstado("VENCIDA");
            huboCambios = true;
            continue;
        }

        if (!estado.equalsIgnoreCase("CONFIRMADA")
                && !estado.equalsIgnoreCase("EN_CURSO")) {
            continue;
        }

        if (!ahora.isBefore(fin)) {
            reserva.setEstado("FINALIZADA");
            huboCambios = true;

        } else if (estado.equalsIgnoreCase("CONFIRMADA")
                && !ahora.isBefore(inicio)) {
            reserva.setEstado("EN_CURSO");
            huboCambios = true;
        }
    }

    return huboCambios;
}
    
    
    
   public boolean guardarReservas() {
    try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutareserva))) {
        escritor.write(
            "idReserva,codigoCliente,codigoCancha,fecha,estado,"
            + "horaInicio,duracion,horaFinal,balon,arbitro,chalecos,"
            + "subtotalCancha,totalExtras,totalReserva,"
            + "fechaCancelacion,motivoCancelacion,usuarioResponsableCancelacion"
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
                + String.format(java.util.Locale.US, "%.2f", r.getSubtotalCancha()) + ","
                + String.format(java.util.Locale.US, "%.2f", r.getTotalExtras()) + ","
                + String.format(java.util.Locale.US, "%.2f", r.getTotalReserva()) + ","
                + (r.getFechaCancelacion() == null ? "" : r.getFechaCancelacion()) + ","
                + (r.getMotivoCancelacion() == null ? "" : r.getMotivoCancelacion().replace(",", " ")) + ","
                + (r.getUsuarioResponsableCancelacion() == null ? "" : r.getUsuarioResponsableCancelacion())
            );
            escritor.newLine();
        }

        return true;
    } catch (IOException e) {
        System.out.println("Error al guardar reservas: " + e.getMessage());
        return false;
    }
}
    
   public void mostrarReservasPorRangoFechas(
        javax.swing.JTable tabla,
        java.time.LocalDate desde,
        java.time.LocalDate hasta,
        PersistenciaPago persistenciaPagos) {

    String[] columnas = {
        "Reserva", "Cliente", "Cancha", "Fecha", "Horario",
        "Duración", "Estado", "Balones", "Árbitros", "Chalecos",
        "Total Q", "Pagado Q", "Pendiente Q",
        "Fecha de cancelación", "Usuario responsable", "Motivo"
    };

    javax.swing.table.DefaultTableModel modelo =
            new javax.swing.table.DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    for (int i = 0; i < cantidad; i++) {
        Reserva reserva = reservas[i];

        if (reserva == null
                || reserva.getFecha().isBefore(desde)
                || reserva.getFecha().isAfter(hasta)) {
            continue;
        }

        double total = reserva.getTotalReserva();
        double pagado = persistenciaPagos.getTotalPagadoReserva(
                reserva.getIdReserva()
        );
        double pendiente = Math.max(0, total - pagado);

        // Las cotizaciones todavía no cuentan como deuda ni como pago.
        if (reserva.getEstado().equalsIgnoreCase("PENDIENTE")) {
            pagado = 0;
            pendiente = 0;
        }

        modelo.addRow(new Object[]{
            "R" + String.format("%03d", reserva.getIdReserva()),
            reserva.getCliente() == null ? "" : reserva.getCliente().getNombre(),
            reserva.getCancha() == null ? "" : reserva.getCancha().getNombre(),
            reserva.getFecha(),
            reserva.getHoraInicio() + " - " + reserva.getHoraFinal(),
            reserva.getDuracionHoras(),
            reserva.getEstado(),
            reserva.getBalon(),
            reserva.getArbitro(),
            reserva.getChalecos(),
            String.format("Q %.2f", total),
            String.format("Q %.2f", pagado),
            String.format("Q %.2f", pendiente),
            reserva.getFechaCancelacion() == null
                    ? ""
                    : reserva.getFechaCancelacion().toString().replace("T", " "),
            reserva.getUsuarioResponsableCancelacion() == null
                    ? ""
                    : reserva.getUsuarioResponsableCancelacion(),
            reserva.getMotivoCancelacion() == null
                    ? ""
                    : reserva.getMotivoCancelacion()
        });
    }

    tabla.setModel(modelo);
}
      
}