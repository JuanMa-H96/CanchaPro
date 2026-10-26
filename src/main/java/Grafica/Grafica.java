package Grafica;

public class Grafica extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Grafica.class.getName());

   
    public Grafica() {
        
        initComponents();
         ocultarPestanas();
    }
    
private void ocultarPestanas() {
    PanelContenido.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
        @Override
        protected int calculateTabAreaHeight(int placement, int runCount, int maxTabHeight) {
            return 0;
        }

        @Override
        protected int calculateTabAreaWidth(int placement, int runCount, int maxTabWidth) {
            return 0;
        }
    });
}


    
    
    @SuppressWarnings("unchecked")  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelPrincipal = new javax.swing.JPanel();
        panelmenu = new javax.swing.JPanel();
        logo = new javax.swing.JLabel();
        panelbotones = new javax.swing.JPanel();
        inicio = new javax.swing.JButton();
        reserva = new javax.swing.JButton();
        disponible = new javax.swing.JButton();
        cancha = new javax.swing.JButton();
        clientes = new javax.swing.JButton();
        pagos = new javax.swing.JButton();
        reportes = new javax.swing.JButton();
        reportes1 = new javax.swing.JButton();
        PanelContenido = new javax.swing.JTabbedPane();
        panelinicio = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        panelreserva = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        paneldisponibilidad = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        canchapanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        clientespanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pagospanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        reportespanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PanelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelmenu.setBackground(new java.awt.Color(0, 0, 51));

        logo.setIcon(new javax.swing.ImageIcon("D:\\JM\\Cancha Pro\\final\\logo 1.png")); // NOI18N

        panelbotones.setBackground(new java.awt.Color(0, 0, 51));
        panelbotones.setLayout(new javax.swing.BoxLayout(panelbotones, javax.swing.BoxLayout.LINE_AXIS));

        inicio.setBackground(new java.awt.Color(0, 0, 51));
        inicio.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        inicio.setForeground(new java.awt.Color(255, 255, 255));
        inicio.setText("Inicio");
        inicio.setBorder(null);
        inicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioActionPerformed(evt);
            }
        });

        reserva.setBackground(new java.awt.Color(0, 0, 51));
        reserva.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reserva.setForeground(new java.awt.Color(255, 255, 255));
        reserva.setText("Reserva");
        reserva.setBorder(null);
        reserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservaActionPerformed(evt);
            }
        });

        disponible.setBackground(new java.awt.Color(0, 0, 51));
        disponible.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        disponible.setForeground(new java.awt.Color(255, 255, 255));
        disponible.setText("Disponibilidad");
        disponible.setBorder(null);
        disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disponibleActionPerformed(evt);
            }
        });

        cancha.setBackground(new java.awt.Color(0, 0, 51));
        cancha.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        cancha.setForeground(new java.awt.Color(255, 255, 255));
        cancha.setText("Cancha");
        cancha.setBorder(null);
        cancha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canchaActionPerformed(evt);
            }
        });

        clientes.setBackground(new java.awt.Color(0, 0, 51));
        clientes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        clientes.setForeground(new java.awt.Color(255, 255, 255));
        clientes.setText("Cientes");
        clientes.setBorder(null);
        clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientesActionPerformed(evt);
            }
        });

        pagos.setBackground(new java.awt.Color(0, 0, 51));
        pagos.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        pagos.setForeground(new java.awt.Color(255, 255, 255));
        pagos.setText("Pagos");
        pagos.setBorder(null);
        pagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagosActionPerformed(evt);
            }
        });

        reportes.setBackground(new java.awt.Color(0, 102, 0));
        reportes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reportes.setForeground(new java.awt.Color(255, 255, 255));
        reportes.setText(" Actualizar");
        reportes.setBorder(null);
        reportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportesActionPerformed(evt);
            }
        });

        reportes1.setBackground(new java.awt.Color(0, 0, 51));
        reportes1.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reportes1.setForeground(new java.awt.Color(255, 255, 255));
        reportes1.setText("Reportes");
        reportes1.setBorder(null);
        reportes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportes1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelmenuLayout = new javax.swing.GroupLayout(panelmenu);
        panelmenu.setLayout(panelmenuLayout);
        panelmenuLayout.setHorizontalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelbotones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(disponible, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(reserva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pagos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelmenuLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(logo))
                    .addGroup(panelmenuLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(reportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelmenuLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(reportes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        panelmenuLayout.setVerticalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addComponent(logo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(inicio, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reserva, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disponible, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancha, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(pagos, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 113, Short.MAX_VALUE)
                .addComponent(reportes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelbotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelmenuLayout.createSequentialGroup()
                    .addContainerGap(517, Short.MAX_VALUE)
                    .addComponent(reportes1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(103, 103, 103)))
        );

        PanelPrincipal.add(panelmenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 670));

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout panelinicioLayout = new javax.swing.GroupLayout(panelinicio);
        panelinicio.setLayout(panelinicioLayout);
        panelinicioLayout.setHorizontalGroup(
            panelinicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelinicioLayout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(749, Short.MAX_VALUE))
        );
        panelinicioLayout.setVerticalGroup(
            panelinicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelinicioLayout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jLabel1)
                .addContainerGap(497, Short.MAX_VALUE))
        );

        PanelContenido.addTab("tab1", panelinicio);

        jLabel2.setText("jLabel2");

        javax.swing.GroupLayout panelreservaLayout = new javax.swing.GroupLayout(panelreserva);
        panelreserva.setLayout(panelreservaLayout);
        panelreservaLayout.setHorizontalGroup(
            panelreservaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelreservaLayout.createSequentialGroup()
                .addGap(385, 385, 385)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(428, Short.MAX_VALUE))
        );
        panelreservaLayout.setVerticalGroup(
            panelreservaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelreservaLayout.createSequentialGroup()
                .addGap(234, 234, 234)
                .addComponent(jLabel2)
                .addContainerGap(325, Short.MAX_VALUE))
        );

        PanelContenido.addTab("tab2", panelreserva);

        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout paneldisponibilidadLayout = new javax.swing.GroupLayout(paneldisponibilidad);
        paneldisponibilidad.setLayout(paneldisponibilidadLayout);
        paneldisponibilidadLayout.setHorizontalGroup(
            paneldisponibilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneldisponibilidadLayout.createSequentialGroup()
                .addContainerGap(741, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(72, 72, 72))
        );
        paneldisponibilidadLayout.setVerticalGroup(
            paneldisponibilidadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, paneldisponibilidadLayout.createSequentialGroup()
                .addContainerGap(354, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(205, 205, 205))
        );

        PanelContenido.addTab("tab3", paneldisponibilidad);

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout canchapanelLayout = new javax.swing.GroupLayout(canchapanel);
        canchapanel.setLayout(canchapanelLayout);
        canchapanelLayout.setHorizontalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canchapanelLayout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(768, Short.MAX_VALUE))
        );
        canchapanelLayout.setVerticalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, canchapanelLayout.createSequentialGroup()
                .addContainerGap(536, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(23, 23, 23))
        );

        PanelContenido.addTab("tab4", canchapanel);

        jLabel5.setText("jLabel5");

        javax.swing.GroupLayout clientespanelLayout = new javax.swing.GroupLayout(clientespanel);
        clientespanel.setLayout(clientespanelLayout);
        clientespanelLayout.setHorizontalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientespanelLayout.createSequentialGroup()
                .addContainerGap(771, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        clientespanelLayout.setVerticalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientespanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel5)
                .addContainerGap(538, Short.MAX_VALUE))
        );

        PanelContenido.addTab("tab5", clientespanel);

        jLabel6.setText("jLabel6");

        javax.swing.GroupLayout pagospanelLayout = new javax.swing.GroupLayout(pagospanel);
        pagospanel.setLayout(pagospanelLayout);
        pagospanelLayout.setHorizontalGroup(
            pagospanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pagospanelLayout.createSequentialGroup()
                .addContainerGap(420, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(393, 393, 393))
        );
        pagospanelLayout.setVerticalGroup(
            pagospanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pagospanelLayout.createSequentialGroup()
                .addContainerGap(516, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(43, 43, 43))
        );

        PanelContenido.addTab("tab6", pagospanel);

        jLabel7.setText("jLabel7");

        javax.swing.GroupLayout reportespanelLayout = new javax.swing.GroupLayout(reportespanel);
        reportespanel.setLayout(reportespanelLayout);
        reportespanelLayout.setHorizontalGroup(
            reportespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportespanelLayout.createSequentialGroup()
                .addContainerGap(617, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(196, 196, 196))
        );
        reportespanelLayout.setVerticalGroup(
            reportespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, reportespanelLayout.createSequentialGroup()
                .addContainerGap(339, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(220, 220, 220))
        );

        PanelContenido.addTab("tab7", reportespanel);

        PanelPrincipal.add(PanelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 0, 850, 610));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inicioActionPerformed
        PanelContenido.setSelectedIndex(0);
    }//GEN-LAST:event_inicioActionPerformed

    private void reservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservaActionPerformed
        PanelContenido.setSelectedIndex(1);
    }//GEN-LAST:event_reservaActionPerformed

    private void disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disponibleActionPerformed
        PanelContenido.setSelectedIndex(2);
    }//GEN-LAST:event_disponibleActionPerformed

    private void canchaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canchaActionPerformed
        PanelContenido.setSelectedIndex(3);
    }//GEN-LAST:event_canchaActionPerformed

    private void clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientesActionPerformed
        PanelContenido.setSelectedIndex(4);
    }//GEN-LAST:event_clientesActionPerformed

    private void pagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagosActionPerformed
        PanelContenido.setSelectedIndex(5);
    }//GEN-LAST:event_pagosActionPerformed

    private void reportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportesActionPerformed
        PanelContenido.setSelectedIndex(6);
    }//GEN-LAST:event_reportesActionPerformed

    private void reportes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportes1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_reportes1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Grafica().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane PanelContenido;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JButton cancha;
    private javax.swing.JPanel canchapanel;
    private javax.swing.JButton clientes;
    private javax.swing.JPanel clientespanel;
    private javax.swing.JButton disponible;
    private javax.swing.JButton inicio;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel logo;
    private javax.swing.JButton pagos;
    private javax.swing.JPanel pagospanel;
    private javax.swing.JPanel panelbotones;
    private javax.swing.JPanel paneldisponibilidad;
    private javax.swing.JPanel panelinicio;
    private javax.swing.JPanel panelmenu;
    private javax.swing.JPanel panelreserva;
    private javax.swing.JButton reportes;
    private javax.swing.JButton reportes1;
    private javax.swing.JPanel reportespanel;
    private javax.swing.JButton reserva;
    // End of variables declaration//GEN-END:variables
}
