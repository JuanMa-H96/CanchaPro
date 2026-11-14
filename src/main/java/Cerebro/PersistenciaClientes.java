package Cerebro;

import Archivos.Cliente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import javax.swing.JOptionPane;

public class PersistenciaClientes {

    private String ruta =
            "C:\\Users\\juan1\\OneDrive\\Desktop\\persistencia\\clientes.csv";

    private Cliente[] clientes = new Cliente[1000];
    private int cantidad = 0;

    public void leerClientes() {
        cantidad = 0;

        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            lector.readLine(); // Saltar encabezado

            String linea;

            while ((linea = lector.readLine()) != null && cantidad < clientes.length) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.replace("\"", "").split(",", -1);

                if (datos.length < 4) {
                    continue;
                }

                String nombre = datos[0].trim();
                String telefono = datos[1].trim();
                String email = datos[2].trim();
                String codigo = datos[3].trim();

                boolean activo = true;
                if (datos.length > 4 && !datos[4].trim().isEmpty()) {
                    activo = Boolean.parseBoolean(datos[4].trim());
                }

             
                LocalDate fechaRegistro = LocalDate.now();

                if (datos.length > 5 && !datos[5].trim().isEmpty()) {
                    fechaRegistro = LocalDate.parse(datos[5].trim());
                }

                Cliente cliente = new Cliente(
                        nombre,
                        telefono,
                        email,
                        codigo,
                        fechaRegistro
                );

                cliente.setActivo(activo);
                clientes[cantidad] = cliente;
                cantidad++;
            }

            System.out.println("Clientes leídos: " + cantidad);

        } catch (IOException e) {
            System.out.println("Error al leer clientes: " + e.getMessage());
        }
    }

    public int getCantidad() {
        return cantidad;
    }

    public Cliente getCliente(int posicion) {
        if (posicion < 0 || posicion >= cantidad) {
            return null;
        }

        return clientes[posicion];
    }

    public String agregarCliente(String nombre, String telefono, String email) {
        if (cantidad >= clientes.length) {
            return null;
        }

        String codigo = String.format("C%03d", cantidad + 1);

        clientes[cantidad] = new Cliente(
                nombre,
                telefono,
                email,
                codigo,
                LocalDate.now()
        );

        cantidad++;
        return codigo;
    }

    public Cliente buscarCliente(String dato) {
        if (dato == null) {
            return null;
        }

        dato = dato.trim();

        for (int i = 0; i < cantidad; i++) {
            Cliente cliente = clientes[i];

            if (cliente != null
                    && (cliente.getNombre().equalsIgnoreCase(dato)
                    || cliente.getTelefono().equals(dato)
                    || cliente.getCodigo().equalsIgnoreCase(dato))) {
                return cliente;
            }
        }

        return null;
    }

    public boolean modificarCliente(
            String codigo,
            String nombre,
            String telefono,
            String email) {

        for (int i = 0; i < cantidad; i++) {
            Cliente cliente = clientes[i];

            if (cliente != null
                    && cliente.getCodigo().equalsIgnoreCase(codigo)) {
                cliente.setNombre(nombre);
                cliente.setTelefono(telefono);
                cliente.setEmail(email);
                return true;
            }
        }

        return false;
    }

    public boolean eliminarCliente(String codigo) {
        for (int i = 0; i < cantidad; i++) {
            Cliente cliente = clientes[i];

            if (cliente != null
                    && cliente.getCodigo().equalsIgnoreCase(codigo)) {
                cliente.setActivo(false);
                return true;
            }
        }

        return false;
    }
    
    public void mostrarClientes(javax.swing.JTable tabla) {
    javax.swing.table.DefaultTableModel modelo =
            (javax.swing.table.DefaultTableModel) tabla.getModel();

    modelo.setRowCount(0); // Quita las filas anteriores

    for (int i = 0; i < cantidad; i++) {
        Cliente cliente = clientes[i];

        if (cliente != null) {
            modelo.addRow(new Object[]{
                cliente.getCodigo(),
                cliente.getNombre(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isActivo() ? "ACTIVO" : "INACTIVO",
                cliente.getFechaRegistro() == null
                        ? ""
                        : cliente.getFechaRegistro()
            });
        }
    }
}

    public void guardarClientes() {
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(ruta))) {
            escritor.write("nombre,telefono,email,codigo,activo,fechaRegistro");
            escritor.newLine();

            for (int i = 0; i < cantidad; i++) {
                Cliente cliente = clientes[i];

                if (cliente == null) {
                    continue;
                }

                String fecha = cliente.getFechaRegistro() == null
                        ? ""
                        : cliente.getFechaRegistro().toString();

                escritor.write(
                        cliente.getNombre() + ","
                        + cliente.getTelefono() + ","
                        + cliente.getEmail() + ","
                        + cliente.getCodigo() + ","
                        + cliente.isActivo() + ","
                        + fecha
                );

                escritor.newLine();
            }

            JOptionPane.showMessageDialog(null, "Clientes guardados.");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Error al guardar clientes: " + e.getMessage()
            );
        }
    }
}