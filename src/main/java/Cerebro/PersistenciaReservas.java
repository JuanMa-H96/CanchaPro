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
    
    public int getCantidadReservas() {
    return cantidad;
    }

    public Reserva getReserva(int posicion) {
    return reservas[posicion];
    }   
    
    
    
    //falta el metodo de guardar reservas
    
      
}