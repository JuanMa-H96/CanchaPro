package cerebro;

import Archivos.Cliente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

public class PersistenciaCsv {

    private Cliente[] clientes = new Cliente[1000];
    private int cantidad = 0;

  

    public void leerClientes() {

        String ruta = "C:\\Users\\juan1\\OneDrive\\Desktop\\persistencia/clientes.csv";

        try {
            BufferedReader lector = new BufferedReader(new FileReader(ruta));
            String linea = lector.readLine();
            
            while ((linea = lector.readLine()) != null && cantidad < 1000) {
            String[] datos = linea.split(",", -1);

                Cliente cliente = new Cliente(
                datos[0].replace("\"", ""),
                datos[1].replace("\"", ""),
                datos[2].replace("\"", ""),
                datos[3].replace("\"", "")
            );

            if (datos.length > 4) {
                String estado = datos[4].replace("\"", "").trim();
                cliente.setActivo(Boolean.parseBoolean(estado));
            }

                clientes[cantidad] = cliente;
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


    public String agregarCliente(String nombre, String telefono, String email) {

        if (cantidad >= 1000) {
            return null;
        }

        String codigo = String.format("C%03d", cantidad + 1);

        clientes[cantidad] = new Cliente(nombre, telefono, email, codigo);

        cantidad++;

        return codigo;
    }

 

    public Cliente buscarCliente(String dato) {

        for (int i = 0; i < cantidad; i++) {

            if (clientes[i] != null) {

                if (clientes[i].getNombre().equalsIgnoreCase(dato)
                        || clientes[i].getTelefono().equals(dato)
                        || clientes[i].getCodigo().equalsIgnoreCase(dato)) {

                    return clientes[i];
                }
            }
        }

        return null;
    }

    

    public boolean modificarCliente(String codigo, String nombre, String telefono, String email) {

        for (int i = 0; i < cantidad; i++) {

            if (clientes[i] != null && clientes[i].getCodigo().equals(codigo)) {

                clientes[i].setNombre(nombre);
                clientes[i].setTelefono(telefono);
                clientes[i].setEmail(email);

                return true;
            }
        }

        return false;
    }



    public boolean eliminarCliente(String codigo) {

        for (int i = 0; i < cantidad; i++) {

            if (clientes[i] != null && clientes[i].getCodigo().equals(codigo)) {

                clientes[i].setActivo(false);

               

                return true;
            }
        }

        return false;
    }

    /*GUARDAR CLIENTES

    public void guardarClientes() {

        String ruta = "C:\\Users\\juan1\\OneDrive\\Desktop\\Clientes.csv";

        try {

            BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta));

            escritor.write("nombre,telefono,email,codigo,activo");
            escritor.newLine();

            for (int i = 0; i < cantidad; i++) {

                escritor.write(
                    clientes[i].getNombre() + ","
                    + clientes[i].getTelefono() + ","
                    + clientes[i].getEmail() + ","
                    + clientes[i].getCodigo() + ","
                    + clientes[i].isActivo()
                );

                escritor.newLine();
            }

            escritor.close();

        } catch (IOException e) {

            JOptionPane.showMessageDialog(null, "Error al guardar clientes.");
        }
    }*/
}