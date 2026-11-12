package cerebro;
import Grafica.Grafica;
import Archivos.Cliente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

public class PersistenciaCsv {
    
    private Cliente[] clientes = new Cliente[1000];
    private int cantidad = 0;

    public void leerClientes() {
        String ruta = "C:\\Users\\juan1\\OneDrive\\Desktop\\clientes.csv";

        try {
            BufferedReader lector = new BufferedReader(new FileReader(ruta));

            String linea = lector.readLine(); 

            while ((linea = lector.readLine()) != null && cantidad < 1000) {
                String[] datos = linea.split(",");

                clientes[cantidad] = new Cliente(
                    datos[0].replace("\"", ""),
                    datos[1].replace("\"", ""),
                    datos[2].replace("\"", ""),
                    datos[3].replace("\"", "")
                );

                cantidad++;
            }

            lector.close();
            System.out.println("Clientes leídos: " + cantidad);

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public Cliente getCliente(int posicion) {
        return clientes[posicion];
    }
    
    //AGREGO NUEVO CLIENTE
    
public String agregarCliente(String nombre, String telefono, String email) {
    if (cantidad >= 1000) {
        return null;
    }

    String codigo = String.format("C%03d", cantidad + 1);

    clientes[cantidad] = new Cliente(nombre, telefono, email, codigo);
    cantidad++;

    return codigo;
}
    
}
