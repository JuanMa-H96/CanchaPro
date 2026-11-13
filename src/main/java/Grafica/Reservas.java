
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

    persistenciaReservas.mostrarTodasReservas(
            todasreservas,
            persistenciaPagos
    );
    
    persistenciaReservas.actualizarEstadosFinalizados();
    persistenciaReservas.mostrarTodasReservas(todasreservas, persistenciaPagos);
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

        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        todasreservas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Todas las Reservas");

        todasreservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Reserva", "Cliente", "Cancha", "Fecha", "Horario", "Duracion", "Estado", "Balones", "Arbitros", "Chalecos", "Total Q", "Pagado Q", "Pendiente Q"
            }
        ));
        jScrollPane6.setViewportView(todasreservas);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jButton1.setText("Atras");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane6)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(513, 513, 513)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(549, 549, 549)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(690, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTable todasreservas;
    // End of variables declaration//GEN-END:variables
}
