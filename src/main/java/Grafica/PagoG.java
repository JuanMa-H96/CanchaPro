package Grafica;

import Archivos.Reserva;
import Cerebro.PersistenciaTienda;
import Cerebro.PersistenciaPago;
import javax.swing.JOptionPane;

public class PagoG extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PagoG.class.getName());

        private Archivos.Reserva reservaActual;
        private Cerebro.PersistenciaPago persistenciaPagos;
        private Cerebro.PersistenciaReservas persistenciaReservas;
        private Cerebro.Disponibilidad disponibilidad; 
        private PersistenciaTienda persistenciaTienda;

   public PagoG(
        Reserva reserva,
        PersistenciaPago persistenciaPagos,
        Cerebro.PersistenciaReservas persistenciaReservas,
        Cerebro.Disponibilidad disponibilidad,
         PersistenciaTienda persistenciaTienda)                                       {

    initComponents();

    this.reservaActual = reserva;
    this.persistenciaPagos = persistenciaPagos;
    this.persistenciaReservas = persistenciaReservas;
    this.disponibilidad = disponibilidad;
     this.persistenciaTienda = persistenciaTienda;

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

    int id = reservaActual.getIdReserva();
    String codigoReserva = "R" + String.format("%03d", id);

    double totalCancha = reservaActual.getTotalReserva();
    double totalTienda =
            persistenciaTienda.calcularTotalComprasReserva(codigoReserva);

    double pagadoCancha = persistenciaPagos.getTotalPagadoReserva(id);
    double pagadoTienda = persistenciaPagos.getTotalPagadoTienda(id);

    double saldoCancha = Math.max(0, totalCancha - pagadoCancha);
    double saldoTienda = Math.max(0, totalTienda - pagadoTienda);
    double totalGeneral = totalCancha + totalTienda;
    double saldoGeneral = saldoCancha + saldoTienda;

    String informacion =
            "INFORMACIÓN DE LA RESERVA\n"
            + "Código: " + codigoReserva
            + "\nCliente: " + reservaActual.getCliente().getNombre()
            + "\nCancha: " + reservaActual.getCancha().getNombre()
            + "\nFecha: " + reservaActual.getFecha()
            + "\nHorario: " + reservaActual.getHoraInicio()
            + " - " + reservaActual.getHoraFinal()
            + "\nEstado: " + reservaActual.getEstado()
            + "\n\nBALANCE"
            + "\nCancha y extras: Q " + String.format("%.2f", totalCancha)
            + "\nTienda: Q " + String.format("%.2f", totalTienda)
            + "\nTotal general: Q " + String.format("%.2f", totalGeneral)
            + "\n\nPagado para cancha: Q " + String.format("%.2f", pagadoCancha)
            + "\nPagado para tienda: Q " + String.format("%.2f", pagadoTienda)
            + "\nSaldo cancha: Q " + String.format("%.2f", saldoCancha)
            + "\nSaldo tienda: Q " + String.format("%.2f", saldoTienda)
            + "\nSaldo total: Q " + String.format("%.2f", saldoGeneral);

    if (reservaActual.getEstado().equalsIgnoreCase("PENDIENTE")) {
        double anticipoMinimo = Math.round(totalCancha * 0.10 * 100.0) / 100.0;

        informacion += "\n\nEs una cotización."
                + "\nPago mínimo para confirmarla: Q "
                + String.format("%.2f", anticipoMinimo);
    }

    inforeserva.setText(informacion);
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

    int idReserva = reservaActual.getIdReserva();
    String codigoReserva = "R" + String.format("%03d", idReserva);

    // Saldos independientes para cancha/extras y tienda.
    double totalCancha = reservaActual.getTotalReserva();
    double totalTienda =
            persistenciaTienda.calcularTotalComprasReserva(codigoReserva);

    double pagadoCancha =
            persistenciaPagos.getTotalPagadoReserva(idReserva);
    double pagadoTienda =
            persistenciaPagos.getTotalPagadoTienda(idReserva);

    double saldoCancha = Math.max(0, totalCancha - pagadoCancha);
    double saldoTienda = Math.max(0, totalTienda - pagadoTienda);
    double saldoTotal = saldoCancha + saldoTienda;

    if (saldoTotal <= 0) {
        JOptionPane.showMessageDialog(
                this,
                "La cuenta ya está pagada completamente."
        );
        return;
    }

    double montoPago;

    if (total.isSelected()) {
        // Pago total: cobra todo el saldo combinado.
        montoPago = saldoTotal;
    } else {
        // Pago parcial: toma el monto escrito.
        try {
            montoPago = Double.parseDouble(
                    monto.getText().trim().replace(",", ".")
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escribe un monto válido.");
            return;
        }
    }

    // Redondear a centavos antes de distribuirlo.
    montoPago = Math.round(montoPago * 100.0) / 100.0;

    if (montoPago <= 0) {
        JOptionPane.showMessageDialog(
                this,
                "El pago debe ser mayor que cero."
        );
        return;
    }

    if (montoPago > saldoTotal) {
        JOptionPane.showMessageDialog(
                this,
                "El pago supera el saldo pendiente de Q "
                + String.format("%.2f", saldoTotal)
        );
        return;
    }

    boolean esCotizacion = estado.equalsIgnoreCase("PENDIENTE");

    if (esCotizacion) {
        double anticipoMinimo =
                Math.round(totalCancha * 0.10 * 100.0) / 100.0;

        double anticipoPendiente =
                Math.max(0, anticipoMinimo - pagadoCancha);

        if (montoPago < anticipoPendiente) {
            JOptionPane.showMessageDialog(
                    this,
                    "Para confirmar la cotización, paga al menos Q "
                    + String.format("%.2f", anticipoPendiente)
            );
            return;
        }

        // Comprueba que la cancha siga libre, excluyendo esta cotización.
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

    // Primero se aplica el dinero pendiente a cancha y extras.
    double montoCanchaPago = Math.min(montoPago, saldoCancha);
    montoCanchaPago = Math.round(montoCanchaPago * 100.0) / 100.0;

    // El sobrante se aplica a la tienda.
    double montoTiendaPago =
            Math.round((montoPago - montoCanchaPago) * 100.0) / 100.0;

    Archivos.Pago nuevoPago = new Archivos.Pago(
            "P" + System.currentTimeMillis(),
            idReserva,
            java.time.LocalDateTime.now(),
            montoPago,
            metodoSeleccionado.toString(),
            "USR001",
            montoCanchaPago,
            montoTiendaPago
    );

    if (!persistenciaPagos.agregarPago(nuevoPago)) {
        JOptionPane.showMessageDialog(
                this,
                "No hay espacio para registrar el pago."
        );
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
            "Pago registrado: Q " + String.format("%.2f", montoPago)
            + "\nCancha y extras: Q "
            + String.format("%.2f", montoCanchaPago)
            + "\nTienda: Q " + String.format("%.2f", montoTiendaPago)
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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        inforeserva.setColumns(20);
        inforeserva.setRows(5);
        jScrollPane1.setViewportView(inforeserva);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Pagar Reserva");

        total.setText(" Pago total");

        pagar.setBackground(new java.awt.Color(153, 204, 255));
        pagar.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        pagar.setText("Pagar");
        pagar.addActionListener(this::pagarActionPerformed);

        jLabel2.setText("Metodo de Pago");

        metodopago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        metodopago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ".", "Efectivo", "Tarjeta de Credito", "Transferencia" }));
        metodopago.addActionListener(this::metodopagoActionPerformed);

        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setText("Cancelar");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3)))
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(monto, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(9, 9, 9))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(46, 46, 46))
                            .addComponent(metodopago, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(105, 105, 105)
                        .addComponent(total)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(total)
                .addGap(18, 18, 18)
                .addComponent(monto, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pagar, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
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
