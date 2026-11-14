package Grafica;

import Archivos.Reserva;
import Cerebro.PersistenciaPago;
import javax.swing.JOptionPane;

public class PagoG extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PagoG.class.getName());

        private Archivos.Reserva reservaActual;
        private Cerebro.PersistenciaPago persistenciaPagos;
        private Cerebro.PersistenciaReservas persistenciaReservas;
        private Cerebro.Disponibilidad disponibilidad; 

   public PagoG(
        Reserva reserva,
        PersistenciaPago persistenciaPagos,
        Cerebro.PersistenciaReservas persistenciaReservas,
        Cerebro.Disponibilidad disponibilidad) {

    initComponents();

    this.reservaActual = reserva;
    this.persistenciaPagos = persistenciaPagos;
    this.persistenciaReservas = persistenciaReservas;
    this.disponibilidad = disponibilidad;

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

    inforeserva.setEditable(false);
    inforeserva.setLineWrap(true);
    inforeserva.setWrapStyleWord(true);

    mostrarInformacionReserva();
}
    
    
    private void mostrarInformacionReserva() {
    if (reservaActual == null) {
        inforeserva.setText("No se recibió una reserva.");
        return;
    }

    double total = reservaActual.getTotalReserva();
    double pagado = persistenciaPagos.getTotalPagadoReserva(
            reservaActual.getIdReserva()
    );

    String informacion =
            "INFORMACIÓN DE LA RESERVA\n"
            + "Código: R"
            + String.format("%03d", reservaActual.getIdReserva())
            + "\nCliente: " + reservaActual.getCliente().getNombre()
            + "\nCancha: " + reservaActual.getCancha().getNombre()
            + "\nFecha: " + reservaActual.getFecha()
            + "\nHorario: " + reservaActual.getHoraInicio()
            + " - " + reservaActual.getHoraFinal()
            + "\nDuración: " + reservaActual.getDuracionHoras() + " hora(s)"
            + "\nEstado: " + reservaActual.getEstado()
            + "\nBalones: " + reservaActual.getBalon()
            + "\nÁrbitros: " + reservaActual.getArbitro()
            + "\nChalecos: " + reservaActual.getChalecos()
            + "\nSubtotal cancha: Q " + String.format("%.2f", reservaActual.getSubtotalCancha())
            + "\nTotal extras: Q " + String.format("%.2f", reservaActual.getTotalExtras());

    // Línea en blanco entre los datos de la reserva y el balance.
    informacion += "\n\nBALANCE\n"
            + "Total: Q " + String.format("%.2f", total);

    if (reservaActual.getEstado().equalsIgnoreCase("PENDIENTE")) {
        double anticipoMinimo = total * 0.10;

        informacion += "\nPagado: Q 0.00"
                + "\nCotización sin saldo exigible"
                + "\nPago mínimo para confirmar: Q "
                + String.format("%.2f", anticipoMinimo);
    } else {
        double saldoPendiente = Math.max(0, total - pagado);

        informacion += "\nPagado: Q " + String.format("%.2f", pagado)
                + "\nSaldo pendiente: Q "
                + String.format("%.2f", saldoPendiente);
    }

    inforeserva.setText(informacion);
    inforeserva.setEditable(false);
    inforeserva.setLineWrap(true);
    inforeserva.setWrapStyleWord(true);
}
  
    private void procesarPago() {
    if (reservaActual == null) {
        JOptionPane.showMessageDialog(this, "No se recibió una reserva.");
        return;
    }

    String estado = reservaActual.getEstado();

    if (estado.equalsIgnoreCase("CANCELADA")
            || estado.equalsIgnoreCase("FINALIZADA")
            || estado.equalsIgnoreCase("VENCIDA")) {
        JOptionPane.showMessageDialog(
                this,
                "La reserva está " + estado + " y no puede recibir pagos."
        );
        return;
    }

    Object metodoSeleccionado = metodopago.getSelectedItem();

    if (metodoSeleccionado == null
            || metodoSeleccionado.toString().trim().equals(".")
            || metodoSeleccionado.toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecciona el método de pago.");
        return;
    }

    double totalReserva = reservaActual.getTotalReserva();
    double pagado = persistenciaPagos.getTotalPagadoReserva(
            reservaActual.getIdReserva()
    );
    double saldo = Math.max(0, totalReserva - pagado);

    if (saldo <= 0) {
        JOptionPane.showMessageDialog(this, "La reserva ya está pagada completamente.");
        return;
    }

    double montoPago;

    // Si está marcado Pago total, cobra el saldo que falta.
    if (total.isSelected()) {
        montoPago = saldo;
    } else {
        // Si no, toma el monto escrito como pago parcial.
        try {
            montoPago = Double.parseDouble(
                    monto.getText().trim().replace(",", ".")
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escribe un monto válido.");
            return;
        }
    }

    if (montoPago <= 0) {
        JOptionPane.showMessageDialog(this, "El pago debe ser mayor que cero.");
        return;
    }

    if (montoPago > saldo) {
        JOptionPane.showMessageDialog(
                this,
                "El pago supera el saldo pendiente de Q "
                + String.format("%.2f", saldo)
        );
        return;
    }

    boolean esCotizacion = estado.equalsIgnoreCase("PENDIENTE");

    if (esCotizacion) {
        double anticipoMinimo =
                Math.round(totalReserva * 0.10 * 100.0) / 100.0;

        if (montoPago < anticipoMinimo) {
            JOptionPane.showMessageDialog(
                    this,
                    "Para confirmar la cotización, paga al menos Q "
                    + String.format("%.2f", anticipoMinimo)
            );
            return;
        }

        // Verifica que la cancha siga disponible, ignorando esta cotización.
        Archivos.Cancha[] disponibles =
                disponibilidad.buscarCanchasDisponibles(
                        reservaActual.getFecha(),
                        reservaActual.getHoraInicio(),
                        reservaActual.getDuracionHoras(),
                        reservaActual.getCancha().getTipoFutbol(),
                        reservaActual.getIdReserva()
                );

        boolean canchaDisponible = false;

        for (Archivos.Cancha cancha : disponibles) {
            if (cancha == null) {
                break;
            }

            if (cancha.getCodigo().equalsIgnoreCase(
                    reservaActual.getCancha().getCodigo())) {
                canchaDisponible = true;
                break;
            }
        }

        if (!canchaDisponible) {
            JOptionPane.showMessageDialog(
                    this,
                    "La cancha ya no está disponible. No se registró el pago."
            );
            return;
        }
    }

    Archivos.Pago nuevoPago = new Archivos.Pago(
            "P" + System.currentTimeMillis(),
            reservaActual.getIdReserva(),
            java.time.LocalDateTime.now(),
            montoPago,
            metodoSeleccionado.toString(),
            "USR001" // Usuario temporal hasta crear el login
    );

    if (!persistenciaPagos.agregarPago(nuevoPago)) {
        JOptionPane.showMessageDialog(this, "No hay espacio para registrar el pago.");
        return;
    }

    if (esCotizacion) {
        reservaActual.setEstado("CONFIRMADA");

        persistenciaReservas.vencerCotizacionesEnConflicto(
                reservaActual.getCancha().getCodigo(),
                reservaActual.getFecha(),
                reservaActual.getHoraInicio(),
                reservaActual.getDuracionHoras()
        );
    }

    mostrarInformacionReserva();

    monto.setText("");
    total.setSelected(false);

    JOptionPane.showMessageDialog(
        this,
        "Pago Realizado"
);

dispose();
}
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        inforeserva = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        total = new javax.swing.JCheckBox();
        monto = new javax.swing.JTextField();
        pagar = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        metodopago = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        inforeserva.setColumns(20);
        inforeserva.setRows(5);
        jScrollPane1.setViewportView(inforeserva);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pagar Reserva");

        total.setText(" Pago total");

        pagar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        pagar.setText("Pagar");
        pagar.addActionListener(this::pagarActionPerformed);

        jLabel2.setText("Metodo de Pago");

        metodopago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        metodopago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ".", "Efectivo", "Tarjeta de Credito", "Transferencia" }));
        metodopago.addActionListener(this::metodopagoActionPerformed);

        jButton2.setText("Cancelar");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(monto, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(66, 66, 66))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(103, 103, 103))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addComponent(total))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pagar, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(total)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(monto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pagar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(211, 211, 211)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(31, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void metodopagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metodopagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_metodopagoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void pagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagarActionPerformed
procesarPago();       
    

    }//GEN-LAST:event_pagarActionPerformed

  
    public static void main(String args[]) {

     
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea inforeserva;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> metodopago;
    private javax.swing.JTextField monto;
    private javax.swing.JButton pagar;
    private javax.swing.JCheckBox total;
    // End of variables declaration//GEN-END:variables
}
