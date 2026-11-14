
package Grafica;

import Cerebro.PersistenciaPago;
import Cerebro.PersistenciaReservas;

public class Reservas extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reservas.class.getName());

   private PersistenciaReservas persistenciaReservas;
private PersistenciaPago persistenciaPagos;

public Reservas(
        PersistenciaReservas persistenciaReservas,
        PersistenciaPago persistenciaPagos) {

    initComponents();

    this.persistenciaReservas = persistenciaReservas;
    this.persistenciaPagos = persistenciaPagos;

    persistenciaReservas.actualizarEstadosPorHora();

    // El primer elemento del combo debe ser "TODAS".
       ver.setSelectedItem("TODAS");

    persistenciaReservas.mostrarReservasPorEstado(
            todasreservas,
            "TODAS",
            persistenciaPagos
    );

    colorearFilasPorEstado();
}

private void colorearFilasPorEstado() {
    todasreservas.setDefaultRenderer(
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

            // No cambiar los colores de la fila mientras está seleccionada.
            if (!seleccionado) {
                int filaModelo = tabla.convertRowIndexToModel(fila);
                Object datoEstado = tabla.getModel().getValueAt(filaModelo, 6);
                String estado = datoEstado == null ? "" : datoEstado.toString();

                if (estado.equalsIgnoreCase("CANCELADA")) {
                    componente.setBackground(java.awt.Color.RED);
                    componente.setForeground(java.awt.Color.WHITE);

                } else if (estado.equalsIgnoreCase("FINALIZADA")) {
                    componente.setBackground(java.awt.Color.LIGHT_GRAY);
                    componente.setForeground(java.awt.Color.BLACK);

                } else if (estado.equalsIgnoreCase("VENCIDA")) {
                    componente.setBackground(java.awt.Color.ORANGE);
                    componente.setForeground(java.awt.Color.BLACK);

                } else {
                    componente.setBackground(java.awt.Color.GREEN);
                    componente.setForeground(java.awt.Color.BLACK);
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
        atras = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        todasreservas = new javax.swing.JTable();
        ver = new javax.swing.JComboBox<>();
        atras1 = new javax.swing.JButton();
        atras2 = new javax.swing.JButton();
        atras3 = new javax.swing.JButton();
        atras4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        Panelreserva.setBackground(new java.awt.Color(255, 255, 255));
        Panelreserva.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        titulo.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titulo.setText("Todas las Reservas");
        Panelreserva.add(titulo, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 20, 390, 57));

        atras.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        atras.setText("Atras");
        atras.addActionListener(this::atrasActionPerformed);
        Panelreserva.add(atras, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 630, 140, -1));

        todasreservas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(todasreservas);

        Panelreserva.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 1430, 520));

        ver.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        ver.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TODAS", "PENDIENTE", "CONFIRMADA", "EN_CURSO", "FINALIZADA", "CANCELADA", "VENCIDA" }));
        ver.addActionListener(this::verActionPerformed);
        Panelreserva.add(ver, new org.netbeans.lib.awtextra.AbsoluteConstraints(860, 630, 290, 40));

        atras1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        atras1.setText("Atras");
        atras1.addActionListener(this::atras1ActionPerformed);
        Panelreserva.add(atras1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1230, 630, 219, -1));

        atras2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        atras2.setText("Clientes");
        atras2.addActionListener(this::atras2ActionPerformed);
        Panelreserva.add(atras2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 630, 130, -1));

        atras3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        atras3.setText("Reservas");
        atras3.addActionListener(this::atras3ActionPerformed);
        Panelreserva.add(atras3, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 630, 160, -1));

        atras4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        atras4.setText("Atras");
        atras4.addActionListener(this::atras4ActionPerformed);
        Panelreserva.add(atras4, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 630, 140, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(Panelreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 1479, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void atrasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atrasActionPerformed
       this.dispose();
    }//GEN-LAST:event_atrasActionPerformed

    private void verActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verActionPerformed

    String estado = ver.getSelectedItem().toString();

    persistenciaReservas.mostrarReservasPorEstado(
            todasreservas,
            estado,
            persistenciaPagos
    );

    colorearFilasPorEstado();

    }//GEN-LAST:event_verActionPerformed

    private void atras1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atras1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_atras1ActionPerformed

    private void atras2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atras2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_atras2ActionPerformed

    private void atras3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atras3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_atras3ActionPerformed

    private void atras4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atras4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_atras4ActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Panelreserva;
    private javax.swing.JButton atras;
    private javax.swing.JButton atras1;
    private javax.swing.JButton atras2;
    private javax.swing.JButton atras3;
    private javax.swing.JButton atras4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel titulo;
    private javax.swing.JTable todasreservas;
    private javax.swing.JComboBox<String> ver;
    // End of variables declaration//GEN-END:variables
}
