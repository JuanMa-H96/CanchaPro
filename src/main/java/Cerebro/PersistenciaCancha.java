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
    
    String rutacancha = "C:\\Users\\juan1\\OneDrive\\Desktop\\persistencia/canchas.csv";
    
    
    public void leerCanchas() {
    cantidadCanchas = 0;

    try {
        BufferedReader lector = new BufferedReader(new FileReader(rutacancha));
        lector.readLine();
        String linea;

        while ((linea = lector.readLine()) != null && cantidadCanchas < 100) {

            String[] datos = linea.replace("\"", "").split(",");
            String codigo = datos[0];
            String nombre = datos[1];
            int tipo = Integer.parseInt(datos[2].replace("Fútbol", "").trim());
            String[] horario = datos[4].split("–");
            LocalTime apertura = LocalTime.parse(horario[0]);
            LocalTime cierre = LocalTime.parse(horario[1]);
            double precio = Double.parseDouble(datos[5]);
            String estado = datos[6];

            Cancha cancha = new Cancha(codigo, nombre, tipo, precio, estado, apertura, cierre);
            cancha.setActivo(Boolean.parseBoolean(datos[7]));

            canchas[cantidadCanchas] = cancha;
            cantidadCanchas++;
        }

        lector.close();

    } catch (IOException e) {
        System.out.println("Error al leer el CSV: " + e.getMessage());
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
    
    public void guardarCanchas() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(rutacancha))) {
            escritor.write("Código,Cancha,Tipo,Capacidad,Horario,PrecioHora,Estado,Activo");
            escritor.newLine();

            for (int i = 0; i < cantidadCanchas; i++) {
                Cancha cancha = canchas[i];

                String horario = cancha.getHoraApertura()
                        + "–" + cancha.getHoraCierre();

                String fila = cancha.getCodigo() + ","
                        + cancha.getNombre() + ","
                        + "Fútbol " + cancha.getTipoFutbol() + ","
                        + cancha.getLimitePorEquipo() + " jugadores,"
                        + horario + ","
                        + cancha.getPrecioHora() + ","
                        + cancha.getEstado() + ","
                        + cancha.isActivo();

                escritor.write(fila);
                escritor.newLine();
            }

            JOptionPane.showMessageDialog(null, "Canchas guardadas.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null, "Error al guardar: " + e.getMessage()
            );
        }
    }
    
   
}
