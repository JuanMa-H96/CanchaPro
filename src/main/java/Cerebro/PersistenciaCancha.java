package Cerebro;

import Archivos.Cancha;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import javax.swing.JOptionPane;

public class PersistenciaCancha {
    private Cancha[] canchas = new Cancha[100];
    private int cantidadCanchas = 0;
    
    String rutacancha = "C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\persistencia/canchas.csv";
    
    
  public void leerCanchas() {
    cantidadCanchas = 0;

    try (BufferedReader lector = new BufferedReader(new FileReader(rutacancha))) {
        lector.readLine(); // Saltar encabezado

        String linea;

        while ((linea = lector.readLine()) != null
                && cantidadCanchas < canchas.length) {

            if (linea.trim().isEmpty()) {
                continue;
            }

            String[] datos = linea.replace("\"", "").split(",", -1);

            if (datos.length < 8) {
                System.out.println("Fila de cancha incompleta: " + linea);
                continue;
            }

            String codigo = datos[0].trim();
            String nombre = datos[1].trim();
            int tipo = Integer.parseInt(
                    datos[2].replace("Fútbol", "").trim()
            );

            String[] horario = datos[4].split("–", -1);
            LocalTime apertura = LocalTime.parse(horario[0].trim());
            LocalTime cierre = LocalTime.parse(horario[1].trim());

            double precio = Double.parseDouble(datos[5].trim());

            boolean activo = Boolean.parseBoolean(datos[7].trim());

            // Si el CSV dice INACTIVA, respetamos ese estado.
            if (datos[6].trim().equalsIgnoreCase("INACTIVA")) {
                activo = false;
            }

            String estado = activo ? "ACTIVA" : "INACTIVA";

            Cancha cancha = new Cancha(
                    codigo, nombre, tipo, precio, estado, apertura, cierre
            );
            cancha.setActivo(activo);

            canchas[cantidadCanchas] = cancha;
            cantidadCanchas++;
        }

        System.out.println("Canchas leídas: " + cantidadCanchas);

    } catch (IOException | RuntimeException e) {
        System.out.println("Error al leer canchas: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    public void mostrarCanchas() {
        String lista = "";

        for (int i = 0; i < cantidadCanchas; i++) {
            Cancha cancha = canchas[i];

            if (!cancha.isActivo()) {
                continue; // No muestra esta cancha
            }

            lista += "Código: " + cancha.getCodigo()
                    + "\nNombre: " + cancha.getNombre()
                    + "\nTipo: Fútbol " + cancha.getTipoFutbol()
                    + "\nPrecio por hora: Q" + cancha.getPrecioHora()
                    + "\nEstado: " + cancha.getEstado()
                    + "\nHorario: " + cancha.getHoraApertura()
                    + " - " + cancha.getHoraCierre()
                    + "\nLímite por equipo: " + cancha.getLimitePorEquipo()
                    + "\n\n";
        }

        if (lista.isEmpty()) {
            lista = "No hay canchas activas.";
        }

        JOptionPane.showMessageDialog(null, lista);
    }
    
     public Cancha buscarCancha(String codigo) {
    for (int i = 0; i < cantidadCanchas; i++) {
        Cancha cancha = canchas[i];

        if (cancha != null
                && cancha.getCodigo().equalsIgnoreCase(codigo.trim())
                && cancha.isActivo()) {
            return cancha;
        }
    }

    return null;
}
    
    public int getCantidadCanchas() {
    return cantidadCanchas; 
    }
    public Cancha getCancha(int posicion) {
        return canchas[posicion];
    }
    
    public String agregarCancha(String nombre, int tipo, double precioHora,LocalTime apertura, LocalTime cierre) {
        if (cantidadCanchas >= 100) {
            return null;
        }

        String codigo = String.format("C%02d", cantidadCanchas + 1);

        Cancha nueva = new Cancha(
            codigo,
            nombre,
            tipo,
            precioHora,
            "Libre",
            apertura,
            cierre
        );

        canchas[cantidadCanchas] = nueva;
        cantidadCanchas++;

        return codigo;
    }
    
    public boolean modificarCancha(String codigo, String nombre, int tipo,double precio, LocalTime apertura, LocalTime cierre) {
    for (int i = 0; i < cantidadCanchas; i++) {
        if (canchas[i].getCodigo().equals(codigo)) {
            canchas[i].setNombre(nombre);
            canchas[i].setTipoFutbol(tipo);
            canchas[i].setPrecioHora(precio);
            canchas[i].setHoraApertura(apertura);
            canchas[i].setHoraCierre(cierre);
            return true;
        }
    }

    return false;
}
    
    
    public boolean eliminarCancha(String codigo) {
        for (int i = 0; i < cantidadCanchas; i++) {
            Cancha cancha = canchas[i];

            if (cancha.getCodigo().equalsIgnoreCase(codigo)
                    && cancha.isActivo()) {
                cancha.setActivo(false);
                return true;
            }
        }

        return false;
    }
    
        public void mostrarCanchas(javax.swing.JTable tabla, String filtro) {
        String[] columnas = {
            "Código", "Cancha", "Tipo", "Capacidad", "Precio por hora", "Estado"
        };

        javax.swing.table.DefaultTableModel modelo =
                new javax.swing.table.DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        for (int i = 0; i < cantidadCanchas; i++) {
            Cancha cancha = canchas[i];

            if (cancha == null) {
                continue;
            }

            String estado = cancha.isActivo()
                    ? cancha.getEstado()
                    : "INACTIVA";

            if (!filtro.equalsIgnoreCase("TODAS")
                    && !estado.equalsIgnoreCase(filtro)) {
                continue;
            }

            modelo.addRow(new Object[]{
                cancha.getCodigo(),
                cancha.getNombre(),
                "Fútbol " + cancha.getTipoFutbol(),
                cancha.getLimitePorEquipo(),
                String.format("Q %.2f", cancha.getPrecioHora()),
                estado
            });
        }

        tabla.setModel(modelo);
    }
    
        public boolean guardarCanchas() {
    try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutacancha))) {
        escritor.write("Código,Cancha,Tipo,Capacidad,Horario,PrecioHora,Estado,Activo");
        escritor.newLine();

        for (int i = 0; i < cantidadCanchas; i++) {
            Cancha cancha = canchas[i];

            if (cancha == null) {
                continue;
            }

            // Solo guardamos el estado permanente de la cancha.
            String estado = cancha.isActivo() ? "ACTIVA" : "INACTIVA";

            escritor.write(
                cancha.getCodigo() + ","
                + cancha.getNombre().replace(",", " ") + ","
                + "Fútbol " + cancha.getTipoFutbol() + ","
                + cancha.getLimitePorEquipo() + " jugadores,"
                + cancha.getHoraApertura() + "–" + cancha.getHoraCierre() + ","
                + String.format(java.util.Locale.US, "%.2f", cancha.getPrecioHora()) + ","
                + estado + ","
                + cancha.isActivo()
            );
            escritor.newLine();
        }

        return true;

    } catch (IOException e) {
        System.out.println("Error al guardar canchas: " + e.getMessage());
        return false;
    }
}
    
  
    
   
}
