package Grafica;

import Cerebro.PersistenciaCancha;
import Cerebro.PersistenciaClientes;
import Cerebro.PersistenciaPago;
import Cerebro.PersistenciaReservas;

public class Reportes extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reportes.class.getName());

   private PersistenciaClientes persistenciaClientes;
    private PersistenciaCancha persistenciaCanchas;
    private PersistenciaReservas persistenciaReservas;
    private PersistenciaPago persistenciaPagos;

private String moduloSeleccionado = "RESERVAS";



public Reportes(
        PersistenciaClientes clientes,
        PersistenciaCancha canchas,
        PersistenciaReservas reservas,
        PersistenciaPago pagos) {

    initComponents();

    this.persistenciaClientes = clientes;
    this.persistenciaCanchas = canchas;
    this.persistenciaReservas = reservas;
    this.persistenciaPagos = pagos;
    
    colorearFilasPorEstado();
    mostrarReservas();
}

private void configurarFiltro(String modulo, String[] opciones) {
    moduloSeleccionado = modulo;

    javax.swing.DefaultComboBoxModel<String> modelo =
            new javax.swing.DefaultComboBoxModel<>(opciones);

    filtroReporte.setModel(modelo);
    mostrarReporte();
}

private void mostrarReporte() {
    if (filtroReporte.getSelectedItem() == null) {
        return;
    }

    // Texto visible en el combo y en el título.
    String textoFiltro = filtroReporte.getSelectedItem().toString();

    // Texto usado para buscar en los estados guardados.
    String filtro = textoFiltro;

    if (textoFiltro.equalsIgnoreCase("EN CURSO")) {
        filtro = "EN_CURSO";
    }

    switch (moduloSeleccionado) {
        case "RESERVAS":
            titulo.setText("Reservas: " + textoFiltro);
            persistenciaReservas.mostrarReservasPorEstado(
                    tablareporte, filtro, persistenciaPagos
            );
            break;

        case "CLIENTES":
            titulo.setText("Clientes: " + textoFiltro);
            persistenciaClientes.mostrarClientes(tablareporte, filtro);
            break;

        case "CANCHAS":
            titulo.setText("Canchas: " + textoFiltro);
            persistenciaCanchas.mostrarCanchas(tablareporte, filtro);
            break;

        case "PAGOS":
            titulo.setText("Pagos: " + textoFiltro);
            persistenciaPagos.mostrarPagos(tablareporte, filtro);
            break;
    }

    colorearFilasPorEstado();
}

private void mostrarReservas() {
    configurarFiltro("RESERVAS", new String[]{
        "TODAS", "PENDIENTE", "CONFIRMADA", "EN CURSO",
        "FINALIZADA", "CANCELADA", "VENCIDA"
    });
}

private void colorearFilasPorEstado() {
    tablareporte.setDefaultRenderer(
            Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                javax.swing.JTable tabla,
                Object valor,
                boolean seleccionado,
                boolean tieneFoco,
                int fila,
                int columna) {

            java.awt.Component componente =
                    super.getTableCellRendererComponent(
                            tabla, valor, seleccionado, tieneFoco, fila, columna);

            if (seleccionado) {
                return componente;
            }

            // Deja el color normal como base.
            componente.setBackground(java.awt.Color.WHITE);
            componente.setForeground(java.awt.Color.BLACK);

            int columnaEstado = -1;

            if (moduloSeleccionado.equals("RESERVAS")) {
                columnaEstado = 6;
            } else if (moduloSeleccionado.equals("CLIENTES")) {
                columnaEstado = 4;
            } else if (moduloSeleccionado.equals("CANCHAS")) {
                columnaEstado = 5;
            }
             if (moduloSeleccionado.equals("PAGOS")
                    && tabla.getColumnCount() > 4) {

                int filaModelo = tabla.convertRowIndexToModel(fila);
                Object datoMetodo = tabla.getModel().getValueAt(filaModelo, 4);
                String metodo = datoMetodo == null ? "" : datoMetodo.toString();

                if (metodo.equalsIgnoreCase("EFECTIVO")) {
                    componente.setBackground(new java.awt.Color(198, 239, 206));
                } else if (metodo.equalsIgnoreCase("TRANSFERENCIA")) {
                    componente.setBackground(new java.awt.Color(189, 215, 238));
                } else if (metodo.equalsIgnoreCase("TARJETA_CREDITO")) {
                    componente.setBackground(new java.awt.Color(225, 204, 245));
                }

                return componente;
            }   
        
            if (columnaEstado == -1
                    || tabla.getColumnCount() <= columnaEstado) {
                return componente;
            }

            int filaModelo = tabla.convertRowIndexToModel(fila);
            Object valorEstado =
                    tabla.getModel().getValueAt(filaModelo, columnaEstado);
            String estado = valorEstado == null
                    ? ""
                    : valorEstado.toString();

            if (moduloSeleccionado.equals("RESERVAS")) {
                if (estado.equalsIgnoreCase("CANCELADA")) {
                    componente.setBackground(java.awt.Color.RED);
                    componente.setForeground(java.awt.Color.WHITE);
                } else if (estado.equalsIgnoreCase("FINALIZADA")) {
                    componente.setBackground(java.awt.Color.LIGHT_GRAY);
                } else if (estado.equalsIgnoreCase("VENCIDA")) {
                    componente.setBackground(java.awt.Color.ORANGE);
                } else if (estado.equalsIgnoreCase("EN_CURSO")) {
                    componente.setBackground(new java.awt.Color(189, 215, 238));
                } else if (estado.equalsIgnoreCase("PENDIENTE")) {
                    componente.setBackground(new java.awt.Color(255, 235, 156));
                } else {
                    componente.setBackground(new java.awt.Color(198, 239, 206));
                }
            } else if (moduloSeleccionado.equals("CLIENTES")) {
                if (estado.equalsIgnoreCase("INACTIVO")) {
                    componente.setBackground(new java.awt.Color(255, 199, 206));
                } else {
                    componente.setBackground(new java.awt.Color(198, 239, 206));
                }
            } else if (moduloSeleccionado.equals("CANCHAS")) {
                if (estado.equalsIgnoreCase("MANTENIMIENTO")) {
                    componente.setBackground(new java.awt.Color(255, 235, 156));
                } else if (estado.equalsIgnoreCase("INACTIVA")) {
                    componente.setBackground(new java.awt.Color(217, 217, 217));
                } else {
                    componente.setBackground(new java.awt.Color(198, 239, 206));
                }
            }

            return componente;
        }
    });
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Panelreserva = new javax.swing.JPanel();
        titulo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablareporte = new javax.swing.JTable();
        atras = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        generar = new javax.swing.JButton();
        reporte = new javax.swing.JComboBox<>();
        fechafinal = new com.toedter.calendar.JDateChooser();
        fechainicio = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        filtroReporte = new javax.swing.JComboBox<>();
        pagos = new javax.swing.JButton();
        canchas = new javax.swing.JButton();
        reservas = new javax.swing.JButton();
        clientes = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Panelreserva.setBackground(new java.awt.Color(255, 255, 255));
        Panelreserva.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titulo.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Todas las Reservas");
        Panelreserva.add(titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 10, 1390, 57));

        tablareporte.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablareporte);

        Panelreserva.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 1750, 500));

        atras.setBackground(new java.awt.Color(0, 153, 255));
        atras.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        atras.setText("Salir");
        atras.addActionListener(this::atrasActionPerformed);
        Panelreserva.add(atras, new org.netbeans.lib.awtextra.AbsoluteConstraints(1640, 620, 130, 50));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        generar.setBackground(new java.awt.Color(204, 255, 255));
        generar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        generar.setText("Generar Reporte");
        generar.addActionListener(this::generarActionPerformed);

        reporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "RESERVAS", "INGRESOS" }));

        fechafinal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        fechainicio.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fechainicio, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(fechafinal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(reporte, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(generar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fechainicio, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fechafinal, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reporte, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(generar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        Panelreserva.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 600, 800, 80));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        filtroReporte.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        filtroReporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODAS", "PENDIENTE", "CONFIRMADA", "EN_CURSO", "FINALIZADA", "CANCELADA", "VENCIDA" }));
        filtroReporte.addActionListener(this::filtroReporteActionPerformed);

        pagos.setBackground(new java.awt.Color(204, 255, 255));
        pagos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        pagos.setText("Pagos");
        pagos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pagos.addActionListener(this::pagosActionPerformed);

        canchas.setBackground(new java.awt.Color(204, 255, 255));
        canchas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        canchas.setText("Canchas");
        canchas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        canchas.addActionListener(this::canchasActionPerformed);

        reservas.setBackground(new java.awt.Color(204, 255, 255));
        reservas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        reservas.setText("Reservas");
        reservas.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        reservas.addActionListener(this::reservasActionPerformed);

        clientes.setBackground(new java.awt.Color(204, 255, 255));
        clientes.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        clientes.setText("Clientes");
        clientes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        clientes.addActionListener(this::clientesActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(reservas, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(canchas, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(pagos, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(filtroReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(filtroReporte)
                    .addComponent(pagos, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addComponent(canchas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reservas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(16, 16, 16))
        );

        Panelreserva.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 600, 800, 80));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Panelreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 1788, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Panelreserva, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void pagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagosActionPerformed
      configurarFiltro("PAGOS", new String[]{
        "TODOS", "EFECTIVO", "TARJETA_CREDITO", "TRANSFERENCIA"
    });
    }//GEN-LAST:event_pagosActionPerformed

    private void clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientesActionPerformed
        configurarFiltro("CLIENTES", new String[]{
        "TODOS", "ACTIVOS", "INACTIVOS"
    });
    }//GEN-LAST:event_clientesActionPerformed

    private void reservasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservasActionPerformed
         configurarFiltro("RESERVAS", new String[]{
        "TODAS", "PENDIENTE", "CONFIRMADA", "EN_CURSO",
        "FINALIZADA", "CANCELADA", "VENCIDA"
    });
    }//GEN-LAST:event_reservasActionPerformed

    private void canchasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canchasActionPerformed
         configurarFiltro("CANCHAS", new String[]{
        "TODAS", "DISPONIBLE", "MANTENIMIENTO", "INACTIVA"
    });
    }//GEN-LAST:event_canchasActionPerformed

    private void atrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasActionPerformed
        this.dispose();
    }//GEN-LAST:event_atrasActionPerformed

    private void generarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generarActionPerformed
 
    if (fechainicio.getDate() == null || fechafinal.getDate() == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this, "Selecciona la fecha inicial y la fecha final."
        );
        return;
    }

    java.time.LocalDate desde = fechainicio.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

    java.time.LocalDate hasta = fechafinal.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

    if (desde.isAfter(hasta)) {
        javax.swing.JOptionPane.showMessageDialog(
                this, "La fecha inicial no puede ser posterior a la fecha final."
        );
        return;
    }

    Object seleccion = reporte.getSelectedItem();

    if (seleccion == null) {
        javax.swing.JOptionPane.showMessageDialog(
                this, "Selecciona RESERVAS o INGRESOS."
        );
        return;
    }

    String opcion = seleccion.toString();

    if (opcion.equalsIgnoreCase("RESERVAS")) {
        moduloSeleccionado = "RESERVAS";

        persistenciaReservas.mostrarReservasPorRangoFechas(
                tablareporte, desde, hasta, persistenciaPagos
        );

        if (tablareporte.getRowCount() == 0) {
            titulo.setText("No hay reservas del " + desde + " al " + hasta);
        } else {
            titulo.setText("Reservas del " + desde + " al " + hasta);
        }

    } else if (opcion.equalsIgnoreCase("INGRESOS")) {
        moduloSeleccionado = "PAGOS";

        persistenciaPagos.mostrarIngresosPorPeriodo(
                tablareporte, desde, hasta
        );

        titulo.setText("Ingresos del " + desde + " al " + hasta);

    } else {
        javax.swing.JOptionPane.showMessageDialog(
                this, "Selecciona RESERVAS o INGRESOS."
        );
        return;
    }

    colorearFilasPorEstado();


    }//GEN-LAST:event_generarActionPerformed

    private void filtroReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filtroReporteActionPerformed
       mostrarReporte();
    }//GEN-LAST:event_filtroReporteActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panelreserva;
    private javax.swing.JButton atras;
    private javax.swing.JButton canchas;
    private javax.swing.JButton clientes;
    private com.toedter.calendar.JDateChooser fechafinal;
    private com.toedter.calendar.JDateChooser fechainicio;
    private javax.swing.JComboBox<String> filtroReporte;
    private javax.swing.JButton generar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton pagos;
    private javax.swing.JComboBox<String> reporte;
    private javax.swing.JButton reservas;
    private javax.swing.JTable tablareporte;
    private javax.swing.JLabel titulo;
    // End of variables declaration//GEN-END:variables

}