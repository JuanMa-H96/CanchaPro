package Cerebro;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Archivos.Compra;
import Archivos.Producto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class PersistenciaTienda {

    private static final int CAPACIDAD_PRODUCTOS = 100;
    private static final int CAPACIDAD_COMPRAS = 1000;
    private static final int CAPACIDAD_CARRITO = 100;

    private Producto[] productos = new Producto[CAPACIDAD_PRODUCTOS];
    private int cantidadProductos = 0;

    private Compra[] compras = new Compra[CAPACIDAD_COMPRAS];
    private int cantidadCompras = 0;

    // Carrito temporal, todavía no guardado como compra.
    private Compra[] carrito = new Compra[CAPACIDAD_CARRITO];
    private int cantidadCarrito = 0;
    private String idReservaCarrito;

    private boolean activa = false;

    private String rutaProductos =
            "C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\persistenciaproducto.csv";

    private String rutaCompras =
            "C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\persistencia/compra.csv";

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public int getCantidadProductos() {
        return cantidadProductos;
    }

    public Producto getProducto(int posicion) {
        if (posicion < 0 || posicion >= cantidadProductos) {
            return null;
        }

        return productos[posicion];
    }

    public int getCantidadCompras() {
        return cantidadCompras;
    }

    public Compra getCompra(int posicion) {
        if (posicion < 0 || posicion >= cantidadCompras) {
            return null;
        }

        return compras[posicion];
    }

    public boolean agregarProducto(Producto producto) {
        if (producto == null || cantidadProductos >= CAPACIDAD_PRODUCTOS) {
            return false;
        }

        productos[cantidadProductos] = producto;
        cantidadProductos++;
        return true;
    }

    public Producto buscarProducto(String codigo) {
        if (codigo == null) {
            return null;
        }

        for (int i = 0; i < cantidadProductos; i++) {
            if (productos[i] != null
                    && productos[i].getCodigo().equalsIgnoreCase(codigo)) {
                return productos[i];
            }
        }

        return null;
    }

    public void leerProductos() {
        cantidadProductos = 0;

        try (BufferedReader lector =
                new BufferedReader(new FileReader(rutaProductos))) {

            lector.readLine(); // Saltar encabezado
            String linea;

            while ((linea = lector.readLine()) != null
                    && cantidadProductos < CAPACIDAD_PRODUCTOS) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.replace("\"", "").split(",", -1);

                if (datos.length < 4) {
                    continue;
                }

                try {
                    String codigo = datos[0].trim();
                    String nombre = datos[1].trim();
                    double precio = Double.parseDouble(datos[2].trim());
                    int cantidad = Integer.parseInt(datos[3].trim());

                    productos[cantidadProductos] =
                            new Producto(codigo, nombre, precio, cantidad);

                    cantidadProductos++;
                } catch (NumberFormatException e) {
                    System.out.println("Producto ignorado: " + linea);
                }
            }

            System.out.println("Productos leídos: " + cantidadProductos);

        } catch (IOException e) {
            System.out.println("Error al leer productos: " + e.getMessage());
        }
    }

    public boolean guardarProductos() {
        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(rutaProductos))) {

            escritor.write("codigo,nombre,precio,cantidad");
            escritor.newLine();

            for (int i = 0; i < cantidadProductos; i++) {
                Producto producto = productos[i];

                if (producto != null) {
                    escritor.write(
                            producto.getCodigo() + ","
                            + producto.getNombre().replace(",", " ") + ","
                            + String.format(
                                    Locale.US, "%.2f", producto.getPrecio()
                            ) + ","
                            + producto.getCantidad()
                    );
                    escritor.newLine();
                }
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar productos: " + e.getMessage());
            return false;
        }
    }

    public void leerCompras() {
        cantidadCompras = 0;

        try (BufferedReader lector =
                new BufferedReader(new FileReader(rutaCompras))) {

            lector.readLine(); // Saltar encabezado
            String linea;

            while ((linea = lector.readLine()) != null
                    && cantidadCompras < CAPACIDAD_COMPRAS) {

                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] datos = linea.replace("\"", "").split(",", -1);

                if (datos.length < 5) {
                    continue;
                }

                try {
                    compras[cantidadCompras] = new Compra(
                            datos[0].trim(),
                            datos[1].trim(),
                            datos[2].trim(),
                            Integer.parseInt(datos[3].trim()),
                            Double.parseDouble(datos[4].trim())
                    );

                    cantidadCompras++;
                } catch (RuntimeException e) {
                    System.out.println("Compra ignorada: " + linea);
                }
            }

            System.out.println("Compras leídas: " + cantidadCompras);

        } catch (IOException e) {
            System.out.println("Error al leer compras: " + e.getMessage());
        }
    }

    public boolean agregarCompra(Compra compra) {
        if (compra == null || cantidadCompras >= CAPACIDAD_COMPRAS) {
            return false;
        }

        compras[cantidadCompras] = compra;
        cantidadCompras++;
        return true;
    }

    public boolean guardarCompras() {
        try (BufferedWriter escritor =
                new BufferedWriter(new FileWriter(rutaCompras))) {

            escritor.write(
                    "idReserva,codigoProducto,nombreProducto,"
                    + "cantidad,precioUnitario,subtotal"
            );
            escritor.newLine();

            for (int i = 0; i < cantidadCompras; i++) {
                Compra compra = compras[i];

                if (compra != null) {
                    escritor.write(
                            compra.getIdReserva() + ","
                            + compra.getCodigoProducto() + ","
                            + compra.getNombreProducto().replace(",", " ") + ","
                            + compra.getCantidad() + ","
                            + String.format(
                                    Locale.US, "%.2f",
                                    compra.getPrecioUnitario()
                            ) + ","
                            + String.format(
                                    Locale.US, "%.2f", compra.getSubtotal()
                            )
                    );
                    escritor.newLine();
                }
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error al guardar compras: " + e.getMessage());
            return false;
        }
    }

    public Compra[] buscarComprasReserva(String idReserva) {
        int cantidadEncontrada = 0;

        for (int i = 0; i < cantidadCompras; i++) {
            if (compras[i] != null
                    && compras[i].getIdReserva().equalsIgnoreCase(idReserva)) {
                cantidadEncontrada++;
            }
        }

        Compra[] resultado = new Compra[cantidadEncontrada];
        int posicion = 0;

        for (int i = 0; i < cantidadCompras; i++) {
            if (compras[i] != null
                    && compras[i].getIdReserva().equalsIgnoreCase(idReserva)) {
                resultado[posicion] = compras[i];
                posicion++;
            }
        }

        return resultado;
    }

    public double calcularTotalComprasReserva(String idReserva) {
        double total = 0;

        for (int i = 0; i < cantidadCompras; i++) {
            if (compras[i] != null
                    && compras[i].getIdReserva().equalsIgnoreCase(idReserva)) {
                total += compras[i].getSubtotal();
            }
        }

        return total;
    }

    // Inicia el carrito para una reserva.
    public void iniciarCarrito(String idReserva) {
        idReservaCarrito = idReserva;
        cantidadCarrito = 0;

        for (int i = 0; i < carrito.length; i++) {
            carrito[i] = null;
        }
    }

    // Agrega un producto al carrito y valida existencias.
    public boolean agregarAlCarrito(String codigoProducto, int cantidad) {
        if (!activa || idReservaCarrito == null || cantidad <= 0) {
            return false;
        }

        Producto producto = buscarProducto(codigoProducto);

        if (producto == null) {
            return false;
        }

        int posicion = buscarEnCarrito(codigoProducto);
        int cantidadEnCarrito = 0;

        if (posicion != -1) {
            cantidadEnCarrito = carrito[posicion].getCantidad();
        }

        if (cantidadEnCarrito + cantidad > producto.getCantidad()) {
            return false;
        }

        if (posicion == -1) {
            if (cantidadCarrito >= CAPACIDAD_CARRITO) {
                return false;
            }

            carrito[cantidadCarrito] = new Compra(
                    idReservaCarrito,
                    producto.getCodigo(),
                    producto.getNombre(),
                    cantidad,
                    producto.getPrecio()
            );

            cantidadCarrito++;
        } else {
            carrito[posicion].setCantidad(cantidadEnCarrito + cantidad);
        }

        return true;
    }

    private int buscarEnCarrito(String codigoProducto) {
        for (int i = 0; i < cantidadCarrito; i++) {
            if (carrito[i].getCodigoProducto()
                    .equalsIgnoreCase(codigoProducto)) {
                return i;
            }
        }

        return -1;
    }

    public boolean quitarDelCarrito(String codigoProducto) {
        int posicion = buscarEnCarrito(codigoProducto);

        if (posicion == -1) {
            return false;
        }

        for (int i = posicion; i < cantidadCarrito - 1; i++) {
            carrito[i] = carrito[i + 1];
        }

        cantidadCarrito--;
        carrito[cantidadCarrito] = null;

        return true;
    }

    public int getCantidadCarrito() {
        return cantidadCarrito;
    }

    public Compra getCompraCarrito(int posicion) {
        if (posicion < 0 || posicion >= cantidadCarrito) {
            return null;
        }

        return carrito[posicion];
    }

    public double getTotalCarrito() {
        double total = 0;

        for (int i = 0; i < cantidadCarrito; i++) {
            total += carrito[i].getSubtotal();
        }

        return total;
    }

    // Confirma todas las líneas y descuenta el inventario.
    public boolean confirmarCompra() {
        if (cantidadCarrito == 0
                || cantidadCompras + cantidadCarrito > CAPACIDAD_COMPRAS) {
            return false;
        }

        // Validar todo antes de modificar inventario o compras.
        for (int i = 0; i < cantidadCarrito; i++) {
            Producto producto =
                    buscarProducto(carrito[i].getCodigoProducto());

            if (producto == null
                    || producto.getCantidad() < carrito[i].getCantidad()) {
                return false;
            }
        }

        // Registrar las compras y descontar existencias.
        for (int i = 0; i < cantidadCarrito; i++) {
            Compra compra = carrito[i];
            Producto producto =
                    buscarProducto(compra.getCodigoProducto());

            compras[cantidadCompras] = compra;
            cantidadCompras++;

            producto.setCantidad(
                    producto.getCantidad() - compra.getCantidad()
            );
        }

        iniciarCarrito(null);
        return true;
    }

    public void cancelarCarrito() {
        iniciarCarrito(null);
    }
    
    public int getCantidadEnCarrito(String codigoProducto) {
    int cantidad = 0;

    for (int i = 0; i < cantidadCarrito; i++) {
        if (carrito[i].getCodigoProducto()
                .equalsIgnoreCase(codigoProducto)) {
            cantidad += carrito[i].getCantidad();
        }
    }

    return cantidad;
}

public int getExistenciasDisponibles(String codigoProducto) {
    Producto producto = buscarProducto(codigoProducto);

    if (producto == null) {
        return 0;
    }

    return producto.getCantidad() - getCantidadEnCarrito(codigoProducto);
}
    
    public void mostrarCompras(
        JTable tabla,
        String filtro,
        PersistenciaPago persistenciaPagos) {

    String[] columnas = {
        "Reserva", "Código", "Producto", "Cantidad",
        "Precio unitario", "Subtotal", "Pagado tienda",
        "Saldo tienda", "Estado de pago"
    };

    DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    for (int i = 0; i < cantidadCompras; i++) {
        Compra compra = compras[i];

        if (compra == null) {
            continue;
        }

        int idReserva = Integer.parseInt(
                compra.getIdReserva().replace("R", "").trim()
        );

        double totalTienda =
                calcularTotalComprasReserva(compra.getIdReserva());

        double pagadoTienda =
                persistenciaPagos.getTotalPagadoTienda(idReserva);

        double saldoTienda = Math.max(0, totalTienda - pagadoTienda);

        String estadoPago = saldoTienda <= 0
                ? "PAGADO COMPLETO"
                : "SALDO PENDIENTE";

        if (filtro.equalsIgnoreCase("SALDO PENDIENTE")
                && !estadoPago.equals("SALDO PENDIENTE")) {
            continue;
        }

        if (filtro.equalsIgnoreCase("PAGADO COMPLETO")
                && !estadoPago.equals("PAGADO COMPLETO")) {
            continue;
        }

        modelo.addRow(new Object[]{
            compra.getIdReserva(),
            compra.getCodigoProducto(),
            compra.getNombreProducto(),
            compra.getCantidad(),
            String.format("Q %.2f", compra.getPrecioUnitario()),
            String.format("Q %.2f", compra.getSubtotal()),
            String.format("Q %.2f", pagadoTienda),
            String.format("Q %.2f", saldoTienda),
            estadoPago
        });
    }

    tabla.setModel(modelo);
}
    
}