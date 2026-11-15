package Grafica;

import Archivos.Compra;
import Cerebro.PersistenciaTienda;
import java.util.Locale;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Tienda extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Tienda.class.getName());

        private PersistenciaTienda persistenciaTienda;
        private String idReserva;
   
    public Tienda() {
    initComponents();

    compra.setEditable(false);
    compra.setLineWrap(true);
    compra.setWrapStyleWord(true);

    jSpinner1.setEnabled(false);
    agregarp.setEnabled(false);

   
}    
        
        
        

public void prepararTienda(
        String idReserva,
        String nombreCliente,
        PersistenciaTienda persistenciaTienda) {

    this.idReserva = idReserva;
    this.persistenciaTienda = persistenciaTienda;

    cliente.setText(nombreCliente);

    compra.setEditable(false); // Cambia por el nombre real de tu JTextArea.
    compra.setLineWrap(true);
    compra.setWrapStyleWord(true);

    jSpinner1.setEnabled(false);
    agregarp.setEnabled(false);

    // Inicia el carrito para esta reserva.
    persistenciaTienda.iniciarCarrito(idReserva);

    cargarProductos();

    productos.getSelectionModel().addListSelectionListener(evento -> {
        if (!evento.getValueIsAdjusting()) {
            mostrarProductoSeleccionado();
        }
    });
    System.out.println(
        "Productos cargados: " + persistenciaTienda.getCantidadProductos()
);
}

private void cargarProductos() {
    String[] columnas = {"Código", "Producto", "Precio", "Disponibles"};

    DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };

    for (int i = 0; i < persistenciaTienda.getCantidadProductos(); i++) {
        Archivos.Producto producto = persistenciaTienda.getProducto(i);

        if (producto != null && producto.getCantidad() > 0) {
            int disponibles = persistenciaTienda.getExistenciasDisponibles(
                    producto.getCodigo()
            );

            modelo.addRow(new Object[]{
                producto.getCodigo(),
                producto.getNombre(),
                String.format(Locale.US, "Q %.2f", producto.getPrecio()),
                disponibles
            });
        }
    }

    productos.setModel(modelo);
}

private void mostrarProductoSeleccionado() {
    int fila = productos.getSelectedRow();

    if (fila == -1) {
        detalles.setText("");
        jSpinner1.setEnabled(false);
        agregarp.setEnabled(false);
        return;
    }

    int filaModelo = productos.convertRowIndexToModel(fila);

    String codigo = productos.getModel()
            .getValueAt(filaModelo, 0)
            .toString();

    Archivos.Producto producto =
            persistenciaTienda.buscarProducto(codigo);

    if (producto == null) {
        detalles.setText("No se encontró el producto.");
        jSpinner1.setEnabled(false);
        agregarp.setEnabled(false);
        return;
    }

    int disponibles =
            persistenciaTienda.getExistenciasDisponibles(codigo);

    detalles.setText(
            producto.getNombre()
            + " | Precio: Q "
            + String.format("%.2f", producto.getPrecio())
            + " | Disponibles: "
            + disponibles
    );

    if (disponibles <= 0) {
        jSpinner1.setEnabled(false);
        agregarp.setEnabled(false);
        return;
    }

    jSpinner1.setModel(new javax.swing.SpinnerNumberModel(
            1, 1, disponibles, 1
    ));

    jSpinner1.setEnabled(true);
    agregarp.setEnabled(true);
}
    


    
    private void mostrarCarrito() {
    String texto = "";

    for (int i = 0; i < persistenciaTienda.getCantidadCarrito(); i++) {
        Compra compra = persistenciaTienda.getCompraCarrito(i);

        if (compra != null) {
            texto += compra.getNombreProducto()
                    + " | " + compra.getCantidad()
                    + " x Q " + String.format(
                            "%.2f", compra.getPrecioUnitario()
                    )
                    + " = Q " + String.format(
                            "%.2f", compra.getSubtotal()
                    )
                    + "\n";
        }
    }

    compra.setText(texto); // Cambia por el nombre real de tu JTextArea.
    total.setText(
            "Q " + String.format("%.2f", persistenciaTienda.getTotalCarrito())
    );
}
    
    
    
    
    
    
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        productos = new javax.swing.JTable();
        detalles = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        agregarp = new javax.swing.JButton();
        total = new javax.swing.JLabel();
        comprar = new javax.swing.JButton();
        cliente = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        compra = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Tienda");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 10, 310, 60));

        productos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(productos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 490, -1));

        detalles.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.add(detalles, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 550, 380, 50));

        jSpinner1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jPanel1.add(jSpinner1, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 550, 120, 50));

        agregarp.setBackground(new java.awt.Color(204, 255, 255));
        agregarp.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        agregarp.setText("Agregar");
        agregarp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarpActionPerformed(evt);
            }
        });
        jPanel1.add(agregarp, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 550, 160, 50));

        total.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jPanel1.add(total, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 620, 200, 80));

        comprar.setBackground(new java.awt.Color(204, 255, 255));
        comprar.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        comprar.setText("Comprar");
        comprar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comprarActionPerformed(evt);
            }
        });
        jPanel1.add(comprar, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 620, 230, 80));
        jPanel1.add(cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 210, 50));

        compra.setColumns(20);
        compra.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        compra.setRows(5);
        jScrollPane2.setViewportView(compra);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 80, 330, 430));

        jLabel2.setText("Elija un producto para agregargo al carrito");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 640, 260, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 884, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 719, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void comprarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comprarActionPerformed
     
    if (persistenciaTienda.getCantidadCarrito() == 0) {
        JOptionPane.showMessageDialog(
                this,
                "Agrega al menos un producto."
        );
        return;
    }

    if (!persistenciaTienda.confirmarCompra()) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo confirmar la compra. Revisa las existencias."
        );
        return;
    }

    JOptionPane.showMessageDialog(
            this,
            "Compra agregada a la reserva " + idReserva + "."
    );

    dispose();
    
    }//GEN-LAST:event_comprarActionPerformed

    private void agregarpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarpActionPerformed
 
    int fila = productos.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(this, "Selecciona un producto.");
        return;
    }

    int filaModelo = productos.convertRowIndexToModel(fila);

    String codigo = productos.getModel()
            .getValueAt(filaModelo, 0)
            .toString();

    int cantidad = ((Number) jSpinner1.getValue()).intValue();

    boolean agregado =
            persistenciaTienda.agregarAlCarrito(codigo, cantidad);

    if (!agregado) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo agregar el producto. Revisa las existencias."
        );
        return;
    }

    cargarProductos();
    mostrarCarrito();

    }//GEN-LAST:event_agregarpActionPerformed

 
    public static void main(String args[]) {
 
        java.awt.EventQueue.invokeLater(() -> new Tienda().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregarp;
    private javax.swing.JLabel cliente;
    private javax.swing.JTextArea compra;
    private javax.swing.JButton comprar;
    private javax.swing.JLabel detalles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable productos;
    private javax.swing.JLabel total;
    // End of variables declaration//GEN-END:variables
}
