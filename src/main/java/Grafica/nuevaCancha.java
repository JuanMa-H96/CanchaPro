
package Grafica;

import Cerebro.PersistenciaCancha;

import java.time.LocalTime;
import javax.swing.JOptionPane;


public class nuevaCancha extends javax.swing.JFrame {
   private Archivos.Cancha canchaEditar;
   private PersistenciaCancha persistencia;
   private Grafica graficaPrincipal;

public nuevaCancha(PersistenciaCancha persistencia, Grafica graficaPrincipal, Archivos.Cancha canchaEditar) {

    this.persistencia = persistencia;
    this.graficaPrincipal = graficaPrincipal;
    this.canchaEditar = canchaEditar;
    initComponents();

    // Cierra solo este formulario, no todo el programa
    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

  
    // Hora de apertura inicial: 08:00
    aperturahoras.setModel(
        new javax.swing.SpinnerNumberModel(8, 0, 23, 1)
    );
    aperturaminutos.setModel(
        new javax.swing.SpinnerNumberModel(0, 0, 59, 1)
    );

    // Hora de cierre inicial: 23:00
    cierrehoras.setModel(
        new javax.swing.SpinnerNumberModel(22, 0, 23, 1)
    );
    cierreminutos.setModel(
        new javax.swing.SpinnerNumberModel(0, 0, 59, 1)
    );

    //moustro los datos a editar
    if (canchaEditar != null) {
    setTitle("Modificar cancha");
    jLabel57.setText("Modificar cancha");
    Crear.setText("Guardar cambios");

    nombrecancha.setText(canchaEditar.getNombre());
    tipocancha.setSelectedItem(
        "Futbol " + canchaEditar.getTipoFutbol()
    );
    preciocancha.setText(
        String.valueOf(canchaEditar.getPrecioHora())
    );

    aperturahoras.setValue(
        canchaEditar.getHoraApertura().getHour()
    );
    aperturaminutos.setValue(
        canchaEditar.getHoraApertura().getMinute()
    );
    cierrehoras.setValue(
        canchaEditar.getHoraCierre().getHour()
    );
    cierreminutos.setValue(
        canchaEditar.getHoraCierre().getMinute()
    );
}
    
    
    
    // Al cerrar este formulario, regresa a la gráfica
    addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            graficaPrincipal.setVisible(true);
            graficaPrincipal.mostrarCanchasEnGrafica();
        }
    });
}
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSpinner1 = new javax.swing.JSpinner();
        jPanel15 = new javax.swing.JPanel();
        Crear = new javax.swing.JButton();
        jLabel64 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        preciocancha = new javax.swing.JTextField();
        tipocancha = new javax.swing.JComboBox<>();
        nombrecancha = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel63 = new javax.swing.JLabel();
        aperturaminutos = new javax.swing.JSpinner();
        aperturahoras = new javax.swing.JSpinner();
        jLabel67 = new javax.swing.JLabel();
        cierrehoras = new javax.swing.JSpinner();
        cierreminutos = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel15.setBorder(new javax.swing.border.MatteBorder(null));

        Crear.setBackground(new java.awt.Color(204, 204, 204));
        Crear.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Crear.setText("Crear");
        Crear.addActionListener(this::CrearActionPerformed);

        jLabel64.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel64.setText("Nombre");

        jLabel65.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel65.setText("Precio por Hora");

        jLabel57.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(255, 0, 0));
        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel57.setText("Nueva Cancha");

        preciocancha.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        preciocancha.addActionListener(this::preciocanchaActionPerformed);

        tipocancha.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N
        tipocancha.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Futbol 5", "Futbol 7", "Futbol 11" }));

        nombrecancha.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        nombrecancha.addActionListener(this::nombrecanchaActionPerformed);

        jLabel66.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel66.setText("Tipo");

        jLabel63.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel63.setText("Hora de Apertura");

        aperturaminutos.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        aperturahoras.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        jLabel67.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel67.setText("Hora de Cierre");

        cierrehoras.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        cierreminutos.setFont(new java.awt.Font("Segoe UI", 2, 18)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(aperturahoras, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(aperturaminutos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel63, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(jLabel67, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(cierrehoras, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cierreminutos, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel63)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aperturaminutos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aperturahoras, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel67)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cierreminutos, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cierrehoras, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel65, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel64, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel57, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(nombrecancha, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                            .addComponent(jLabel66, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(64, 64, 64)
                                .addComponent(Crear, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(tipocancha, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addComponent(preciocancha, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel64)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nombrecancha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel66)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipocancha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(jLabel65)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(preciocancha, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(Crear, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CrearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CrearActionPerformed

 String nombre = nombrecancha.getText().trim();

    try {
        int tipo = Integer.parseInt(
            tipocancha.getSelectedItem().toString()
                .replace("Futbol", "").trim()
        );

        double precio = Double.parseDouble(
            preciocancha.getText().trim()
        );

        LocalTime apertura = LocalTime.of(
            (int) aperturahoras.getValue(),
            (int) aperturaminutos.getValue()
        );

        LocalTime cierre = LocalTime.of(
            (int) cierrehoras.getValue(),
            (int) cierreminutos.getValue()
        );

        if (nombre.isEmpty() || !cierre.isAfter(apertura)) {
            JOptionPane.showMessageDialog(
                null, "Revisa el nombre y el horario."
            );
            return;
        }

        if (canchaEditar == null) {
        // cancha nueva
        String codigo = persistencia.agregarCancha(
            nombre, tipo, precio, apertura, cierre
        );

        if (codigo == null) {
            JOptionPane.showMessageDialog(null, "El arreglo está lleno.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Cancha creada: " + codigo);

    } else {
        // cancha existente
        boolean cambio = persistencia.modificarCancha(
            canchaEditar.getCodigo(),
            nombre,
            tipo,
            precio,
            apertura,
            cierre
        );

        if (!cambio) {
            JOptionPane.showMessageDialog(null, "No se encontró la cancha.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Cancha modificada.");
    }

    dispose();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Revisa el tipo y el precio.");
    }
    }//GEN-LAST:event_CrearActionPerformed

    private void preciocanchaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preciocanchaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_preciocanchaActionPerformed

    private void nombrecanchaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombrecanchaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombrecanchaActionPerformed

  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Crear;
    private javax.swing.JSpinner aperturahoras;
    private javax.swing.JSpinner aperturaminutos;
    private javax.swing.JSpinner cierrehoras;
    private javax.swing.JSpinner cierreminutos;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField nombrecancha;
    private javax.swing.JTextField preciocancha;
    private javax.swing.JComboBox<String> tipocancha;
    // End of variables declaration//GEN-END:variables
}
