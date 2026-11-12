package Grafica;


import Cerebro.PersistenciaClientes;
import Cerebro.PersistenciaCancha; 
import Cerebro.PersistenciaReservas;


import javax.swing.JOptionPane;

import Archivos.Cliente;
import javax.swing.table.DefaultTableModel;

import Archivos.Cancha;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;




public class Grafica extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Grafica.class.getName());
    private PersistenciaClientes persistencia = new PersistenciaClientes();
    private PersistenciaCancha persistenciacancha = new PersistenciaCancha();
    private PersistenciaReservas persistenciaReservas;
    
    private Cancha canchaSelecionada;
    private Cliente clienteSelecionado;
    
    public Grafica() {
        
        
        initComponents();
        ocultarPestanas();
        
        //clientes
         persistencia.leerClientes();
         mostrarClientesEnTabla();
        
         //canchas
         jScrollPane2.setViewportView(panelListaCanchas);
        persistenciacancha.leerCanchas();
         mostrarCanchasEnGrafica();
         
         //reservas 
         persistenciaReservas = new PersistenciaReservas(persistencia,persistenciacancha);

         persistenciaReservas.leerReservas();
         persistenciaReservas.mostrarReservas(tablareserva);
         
            
         
         
         datoscliente.setEditable(false);
            datoscliente.setLineWrap(true);
            datoscliente.setWrapStyleWord(true);

         
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

private void mostrarClientesEnTabla() {
    DefaultTableModel modelo =
            (DefaultTableModel) tablaClientes.getModel();

    modelo.setRowCount(0); // Limpia las filas actuales

    for (int i = 0; i < persistencia.getCantidad(); i++) {
        Cliente cliente = persistencia.getCliente(i);
            
        if(cliente.isActivo()){
        modelo.addRow(new Object[]{
            cliente.getCodigo(),
            cliente.getNombre(),
            cliente.getTelefono(),
            cliente.getEmail()
        });
        }
    }
}

private void limpiarCamposCliente() {
    busqueda.setText("");
    mnombre.setText("");
    mtelefono.setText("");
    mcorreo.setText("");
    mcodigo.setText("");
}

   void mostrarCanchasEnGrafica() {
    panelListaCanchas.removeAll();
    panelListaCanchas.setLayout(new GridLayout(0, 2, 10, 10));

    for (int i = 0; i < persistenciacancha.getCantidadCanchas(); i++) {
        Cancha cancha = persistenciacancha.getCancha(i);
        
         if (!cancha.isActivo()) {
            continue; // No crea una tarjeta para esta cancha
        }
         
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(
            BorderFactory.createTitledBorder(cancha.getNombre())
        );

        tarjeta.add(new JLabel("Código: " + cancha.getCodigo()));
        tarjeta.add(new JLabel("Fútbol " + cancha.getTipoFutbol()));
        tarjeta.add(new JLabel("Precio por hora: Q" + cancha.getPrecioHora()));
        tarjeta.add(new JLabel("Estado: " + cancha.getEstado()));
        tarjeta.add(new JLabel("Horario: " + cancha.getHoraApertura()
                + " - " + cancha.getHoraCierre()));

        JButton modificar = new JButton("Modificar");

        modificar.addActionListener(e -> {
            Grafica.this.setVisible(false);

            nuevaCancha ventana = new nuevaCancha(persistenciacancha,Grafica.this,cancha);
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });

        tarjeta.add(modificar);
        JButton eliminar = new JButton("Eliminar");
        eliminar.addActionListener(e -> {
               int respuesta = JOptionPane.showConfirmDialog(
                   null,
                   "¿Quieres eliminar esta cancha?",
                   "Confirmar",
                   JOptionPane.YES_NO_OPTION
                );

                if (respuesta == JOptionPane.YES_OPTION) {
                    persistenciacancha.eliminarCancha(cancha.getCodigo());
                    mostrarCanchasEnGrafica();
                }
            });

            tarjeta.add(eliminar);
        
        
        
        
        
        panelListaCanchas.add(tarjeta);
    }

    panelListaCanchas.revalidate();
    panelListaCanchas.repaint();
}
   
   
    
    @SuppressWarnings("unchecked")  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
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
        actualizar = new javax.swing.JButton();
        PanelContenido = new javax.swing.JTabbedPane();
        panelinicio = new javax.swing.JPanel();
        canchapanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablareserva = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        textocliente = new javax.swing.JTextField();
        busquedacliente = new javax.swing.JButton();
        consultar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        datoscliente = new javax.swing.JTextArea();
        clientespanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        pagospanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel62 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        panelListaCanchas = new javax.swing.JPanel();
        nuevachancha = new javax.swing.JButton();
        panelclientes = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        bRegistrar = new javax.swing.JButton();
        jLabel61 = new javax.swing.JLabel();
        telefono = new javax.swing.JTextField();
        email = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel55 = new javax.swing.JLabel();
        nombre1 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        jLabel60 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaClientes = new javax.swing.JTable();
        buscar = new javax.swing.JButton();
        busqueda = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        mnombre = new javax.swing.JTextField();
        mcorreo = new javax.swing.JTextField();
        mtelefono = new javax.swing.JTextField();
        Modificar = new javax.swing.JButton();
        mcodigo = new javax.swing.JLabel();
        eliminar = new javax.swing.JButton();
        panelreserva = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();

        jButton1.setText("jButton1");

        jTextField3.setText("jTextField3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PanelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelmenu.setBackground(new java.awt.Color(0, 0, 51));

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

        actualizar.setBackground(new java.awt.Color(0, 0, 51));
        actualizar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        actualizar.setForeground(new java.awt.Color(255, 255, 255));
        actualizar.setText("Reportes");
        actualizar.setBorder(null);
        actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarActionPerformed(evt);
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
                        .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reportes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(actualizar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actualizar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 127, Short.MAX_VALUE)
                .addComponent(reportes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelbotones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        PanelPrincipal.add(panelmenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 740));

        PanelContenido.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        javax.swing.GroupLayout panelinicioLayout = new javax.swing.GroupLayout(panelinicio);
        panelinicio.setLayout(panelinicioLayout);
        panelinicioLayout.setHorizontalGroup(
            panelinicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );
        panelinicioLayout.setVerticalGroup(
            panelinicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );

        PanelContenido.addTab("inicio", panelinicio);

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Reservas");

        tablareserva.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Reserva", "Nombre", "Cancha", "Fecha", "Hora", "Estado"
            }
        ));
        jScrollPane3.setViewportView(tablareserva);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel46)
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Nueva Reserva");

        busquedacliente.setText("Buscar Cliente");
        busquedacliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busquedaclienteActionPerformed(evt);
            }
        });

        consultar.setText("Consultar");

        datoscliente.setColumns(20);
        datoscliente.setRows(5);
        jScrollPane4.setViewportView(datoscliente);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(262, 262, 262)
                .addComponent(consultar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textocliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(busquedacliente, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(textocliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(busquedacliente))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                .addComponent(consultar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout canchapanelLayout = new javax.swing.GroupLayout(canchapanel);
        canchapanel.setLayout(canchapanelLayout);
        canchapanelLayout.setHorizontalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canchapanelLayout.createSequentialGroup()
                .addGap(0, 14, Short.MAX_VALUE)
                .addGroup(canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 14, Short.MAX_VALUE))
        );
        canchapanelLayout.setVerticalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canchapanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(353, 353, 353))
        );

        PanelContenido.addTab("reserva", canchapanel);

        jLabel5.setText("jLabel5");

        javax.swing.GroupLayout clientespanelLayout = new javax.swing.GroupLayout(clientespanel);
        clientespanel.setLayout(clientespanelLayout);
        clientespanelLayout.setHorizontalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientespanelLayout.createSequentialGroup()
                .addContainerGap(731, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        clientespanelLayout.setVerticalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientespanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel5)
                .addContainerGap(668, Short.MAX_VALUE))
        );

        PanelContenido.addTab("disponiblidad", clientespanel);

        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        jLabel62.setText("Gestion de Clientes");

        javax.swing.GroupLayout panelListaCanchasLayout = new javax.swing.GroupLayout(panelListaCanchas);
        panelListaCanchas.setLayout(panelListaCanchasLayout);
        panelListaCanchasLayout.setHorizontalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 778, Short.MAX_VALUE)
        );
        panelListaCanchasLayout.setVerticalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelListaCanchas);

        nuevachancha.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        nuevachancha.setText("Crear Nueva Cancha");
        nuevachancha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevachanchaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 786, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(261, 261, 261)
                .addComponent(nuevachancha, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 535, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nuevachancha, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pagospanelLayout = new javax.swing.GroupLayout(pagospanel);
        pagospanel.setLayout(pagospanelLayout);
        pagospanelLayout.setHorizontalGroup(
            pagospanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pagospanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pagospanelLayout.setVerticalGroup(
            pagospanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pagospanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelContenido.addTab("cancha", pagospanel);

        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setText("Telefono");

        bRegistrar.setBackground(new java.awt.Color(204, 204, 204));
        bRegistrar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        bRegistrar.setText("Registrar");
        bRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarActionPerformed(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setText("Nombre");

        telefono.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        email.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailActionPerformed(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText("e-mail");

        jLabel55.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 0, 0));
        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("Registrar Cliente");

        nombre1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        nombre1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombre1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(email, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(telefono, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel58, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(bRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(nombre1)
                    .addContainerGap()))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jLabel61)
                .addGap(54, 54, 54)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(telefono, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel59)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(email, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(bRegistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(207, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(120, 120, 120)
                    .addComponent(nombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(422, Short.MAX_VALUE)))
        );

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N
        jLabel60.setText("Gestion de Clientes");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 294, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel60)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel54.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 0, 0));
        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Lista de Clientes");

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nombre", "Telefono", "E-mail"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tablaClientes.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(tablaClientes);

        buscar.setBackground(new java.awt.Color(204, 204, 204));
        buscar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        buscar.setText("Buscar");
        buscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buscarActionPerformed(evt);
            }
        });

        busqueda.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        busqueda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busquedaActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 0, 0));
        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("Buscar Cliente");

        Modificar.setBackground(new java.awt.Color(204, 204, 204));
        Modificar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Modificar.setText("Modificar");
        Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarActionPerformed(evt);
            }
        });

        eliminar.setBackground(new java.awt.Color(204, 204, 204));
        eliminar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        eliminar.setText("Eliminar");
        eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(95, 95, 95))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(176, 176, 176)
                                .addComponent(buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 457, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mtelefono, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                            .addComponent(mcodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mtelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mcorreo, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(mcodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
        );

        javax.swing.GroupLayout panelclientesLayout = new javax.swing.GroupLayout(panelclientes);
        panelclientes.setLayout(panelclientesLayout);
        panelclientesLayout.setHorizontalGroup(
            panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelclientesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelclientesLayout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        panelclientesLayout.setVerticalGroup(
            panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelclientesLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        PanelContenido.addTab("clientes", panelclientes);

        panelreserva.setBackground(new java.awt.Color(51, 255, 255));
        panelreserva.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        PanelContenido.addTab("pagos", panelreserva);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );

        PanelContenido.addTab("reportes", jPanel11);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 810, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );

        PanelContenido.addTab("tienda", jPanel12);

        PanelPrincipal.add(PanelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 810, 740));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 739, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

    private void actualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarActionPerformed
      
    }//GEN-LAST:event_actualizarActionPerformed

    private void eliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarActionPerformed

        boolean resultado = persistencia.eliminarCliente(mcodigo.getText());

        if (resultado) {

            mostrarClientesEnTabla();

            JOptionPane.showMessageDialog(null, "Cliente eliminado correctamente.");

            limpiarCamposCliente();

        } else {
            JOptionPane.showMessageDialog(null, "Busque primero un cliente.");
        }

    }//GEN-LAST:event_eliminarActionPerformed

    private void ModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ModificarActionPerformed

        if (mnombre.getText().isEmpty() ||
            mtelefono.getText().isEmpty() ||
            mcorreo.getText().isEmpty()) {

            JOptionPane.showMessageDialog(null, "Ingrese todos los campos.");
            return;
        }

        boolean resultado = persistencia.modificarCliente(
            mcodigo.getText(),
            mnombre.getText(),
            mtelefono.getText(),
            mcorreo.getText()
        );

        if (resultado) {

            mostrarClientesEnTabla();

            JOptionPane.showMessageDialog(null, "Cliente modificado correctamente.");
            limpiarCamposCliente();

        } else {

            JOptionPane.showMessageDialog(null, "No se pudo modificar el cliente.");
        }
    }//GEN-LAST:event_ModificarActionPerformed

    private void busquedaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busquedaActionPerformed

    }//GEN-LAST:event_busquedaActionPerformed

    private void buscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buscarActionPerformed
        String dato = busqueda.getText().trim();

        if (dato.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese un dato para buscar.");
            return;
        }

        Cliente encontrado = persistencia.buscarCliente(dato);

        if (encontrado != null) {

            mnombre.setText(encontrado.getNombre());
            mtelefono.setText(encontrado.getTelefono());
            mcorreo.setText(encontrado.getEmail());
            mcodigo.setText(encontrado.getCodigo());

            Modificar.setEnabled(true);

        } else {

            JOptionPane.showMessageDialog(null, "Cliente no encontrado.");

            limpiarCamposCliente();

            Modificar.setEnabled(false);
        }

    }//GEN-LAST:event_buscarActionPerformed

    private void nombre1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombre1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombre1ActionPerformed

    private void emailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emailActionPerformed

    private void bRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bRegistrarActionPerformed
        String nombre = nombre1.getText().trim();
        String numeroTelefono = telefono.getText().trim();
        String correo = email.getText().trim();

        if (nombre.isEmpty() || numeroTelefono.isEmpty() || correo.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Ingrese todos lo campos.");
        } else {

            String codigo = persistencia.agregarCliente(nombre, numeroTelefono, correo);

            if (codigo != null) {
                mostrarClientesEnTabla();

                JOptionPane.showMessageDialog(null, "Cliente registrado.\n Nombre: "+ nombre + "\nTelefono: "+ numeroTelefono + "\nEmail :" + correo + "\nCódigo: " + codigo);

                nombre1.setText("");
                telefono.setText("");
                email.setText("");
            } else {
                JOptionPane.showMessageDialog(null, "No se pueden agregar más clientes.");
            }
        }
    }//GEN-LAST:event_bRegistrarActionPerformed

    private void nuevachanchaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevachanchaActionPerformed
            setVisible(false);

    nuevaCancha ventana = new nuevaCancha(persistenciacancha, this, null);
    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true);
    }//GEN-LAST:event_nuevachanchaActionPerformed

    private void busquedaclienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_busquedaclienteActionPerformed

    String dato = textocliente.getText().trim();

    if (dato.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingresa Codigo, Nombre o Numero del Cliente.");
        textocliente.requestFocus();
        return;
    }

    Cliente cliente = persistencia.buscarCliente(dato);

    if (cliente != null && cliente.isActivo()) {
        clienteSelecionado = cliente;

        datoscliente.setText(
            "Datos del Cliente\n"
            + "\nCódigo: " + cliente.getCodigo() 
            + "\nNombre: " + cliente.getNombre()
            + "\nTeléfono: " + cliente.getTelefono() 
            + "\nEmail: " + cliente.getEmail()
        );
    } else {
        
        clienteSelecionado = null;
        datoscliente.setText("");

        if (cliente != null) {
            JOptionPane.showMessageDialog(this, "El cliente está inactivo.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
            this,
            "No se encontró el cliente. ¿Deseas registrarlo?",
            "Cliente no encontrado",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
             PanelContenido.setSelectedIndex(4);
        }
    }

    }//GEN-LAST:event_busquedaclienteActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Modificar;
    private javax.swing.JTabbedPane PanelContenido;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JButton actualizar;
    private javax.swing.JButton bRegistrar;
    private javax.swing.JButton buscar;
    private javax.swing.JTextField busqueda;
    private javax.swing.JButton busquedacliente;
    private javax.swing.JButton cancha;
    private javax.swing.JPanel canchapanel;
    private javax.swing.JButton clientes;
    private javax.swing.JPanel clientespanel;
    private javax.swing.JButton consultar;
    private javax.swing.JTextArea datoscliente;
    private javax.swing.JButton disponible;
    private javax.swing.JButton eliminar;
    private javax.swing.JTextField email;
    private javax.swing.JButton inicio;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel mcodigo;
    private javax.swing.JTextField mcorreo;
    private javax.swing.JTextField mnombre;
    private javax.swing.JTextField mtelefono;
    private javax.swing.JTextField nombre1;
    private javax.swing.JButton nuevachancha;
    private javax.swing.JButton pagos;
    private javax.swing.JPanel pagospanel;
    private javax.swing.JPanel panelListaCanchas;
    private javax.swing.JPanel panelbotones;
    private javax.swing.JPanel panelclientes;
    private javax.swing.JPanel panelinicio;
    private javax.swing.JPanel panelmenu;
    private javax.swing.JPanel panelreserva;
    private javax.swing.JButton reportes;
    private javax.swing.JButton reserva;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablareserva;
    private javax.swing.JTextField telefono;
    private javax.swing.JTextField textocliente;
    // End of variables declaration//GEN-END:variables
}
