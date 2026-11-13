package Grafica;

import Archivos.Cancha;
import Archivos.Cliente;
import Archivos.Pago;
import Archivos.Reserva;

import Cerebro.PersistenciaClientes;
import Cerebro.PersistenciaCancha; 
import Cerebro.PersistenciaReservas;
import Cerebro.Disponibilidad;
import Cerebro.CalcularPrecio;
import Cerebro.PersistenciaPago;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;




public class Grafica extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Grafica.class.getName());
    private PersistenciaClientes persistencia = new PersistenciaClientes();
    private PersistenciaCancha persistenciacancha = new PersistenciaCancha();
    private PersistenciaReservas persistenciaReservas;
    private Disponibilidad disponibilidad;
    
    private CalcularPrecio calculadora = new CalcularPrecio();
    private PersistenciaPago persistenciaPagos = new PersistenciaPago();
    private Reserva reservaSeleccionada;
    private Reserva reservaEnEdicion = null;
    

    
    private Cancha canchaSelecionada;
    private Cliente clienteSelecionado;
    private double totalReserva;
    private double subtotalCanchaActual;
    private double totalExtrasActual;
  
   

    public Grafica() {
       initComponents();
       ocultarPestanas();
    
       persistenciaPagos.leerPagos();
       
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
        
        //Disponible
       disponibilidad = new Disponibilidad(persistenciacancha, persistenciaReservas);

      //calcualr precios
        pagoparcial.setEnabled(false);
       balon.addChangeListener(e -> actualizarPrecios());
        arbitro.addChangeListener(e -> actualizarPrecios());
      chaleco.addChangeListener(e -> actualizarPrecios());
       canchadisponible.addActionListener(e -> actualizarPrecios());
       mostrarPreciosExtras();
        
        balon.setModel(new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        arbitro.setModel(new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
       chaleco.setModel(new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

       datoscliente.setEditable(false);
        datoscliente.setLineWrap(true);
       datoscliente.setWrapStyleWord(true);
       
   

       setResizable(false);
       setLocationRelativeTo(null);   
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
    
   private void actualizarPrecios() {
    if (canchadisponible.getSelectedItem() == null) {
        return;
    }

    // Recupera la cancha seleccionada, por ejemplo C01 - La Bombonera
    String seleccion = canchadisponible.getSelectedItem().toString();
    String codigo = seleccion.split(" - ")[0];
    Cancha cancha = persistenciacancha.buscarCancha(codigo);

    java.time.LocalDate fecha = jDateChooser2.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

    java.time.LocalTime hora = timePicker2.getTime();

    int horas = Integer.parseInt(
            duracion.getSelectedItem().toString().replaceAll("[^0-9]", "")
    );

    int balones = ((Number) balon.getValue()).intValue();
    int arbitros = ((Number) arbitro.getValue()).intValue();
    int chalecos = ((Number) chaleco.getValue()).intValue();

    double costoCancha = calculadora.calcularSubtotalCancha(
        cancha, fecha, hora, horas
        );

        double costoExtras = calculadora.calcularTotalExtras(balones, arbitros, chalecos);
        
        subtotalCanchaActual = costoCancha;
        totalExtrasActual = costoExtras;
        
        totalReserva = calculadora.calcularTotalReserva(costoCancha, costoExtras);

        // Subtotal de la reserva: cancha más extras
        subtotal.setText(String.format("Q %.2f", totalReserva));

        // Por ahora no hay tienda, así que el total es el mismo
        totalt.setText(String.format("Q %.2f", totalReserva));
        
        double anticipoMinimo = totalReserva * 0.10;
        anticipo.setText("Anticipo 10%: Q " + String.format("%.2f", anticipoMinimo));
}
   
   private void mostrarPreciosExtras() {
    jbalon.setText("Balón: Q " +
            String.format("%.2f", calculadora.getPrecioBalon()));

    jarbitro.setText("Árbitro: Q " +
            String.format("%.2f", calculadora.getPrecioArbitro()));

    jchaleco.setText("Chalecos: Q " +
            String.format("%.2f", calculadora.getPrecioChaleco()));
}
   
    private void actualizarCampoPagoParcial() {
      boolean parcialSeleccionado = parcial.isSelected();

      pagoparcial.setEnabled(parcialSeleccionado);

      if (!parcialSeleccionado) {
          pagoparcial.setText("");
      }
  }
   
    public void cargarReservaParaModificar(Reserva reserva) {
    if (reserva == null) {
        JOptionPane.showMessageDialog(this, "No se encontró la reserva.");
        return;
    }

    String estado = reserva.getEstado();

    if (!estado.equalsIgnoreCase("PENDIENTE")
            && !estado.equalsIgnoreCase("CONFIRMADA")) {
        JOptionPane.showMessageDialog(
                this,
                "Solo puedes modificar reservas pendientes o confirmadas."
        );
        return;
    }

    reservaEnEdicion = reserva;

    // El cliente se conserva; no se modifica desde este formulario.
    clienteSelecionado = reserva.getCliente();
    textocliente.setText(clienteSelecionado.getCodigo());

    datoscliente.setText(
            "Código: " + clienteSelecionado.getCodigo()
            + "\nNombre: " + clienteSelecionado.getNombre()
            + "\nTeléfono: " + clienteSelecionado.getTelefono()
            + "\nEmail: " + clienteSelecionado.getEmail()
    );

    // Cargar fecha, hora y duración.
    java.util.Date fecha = java.sql.Date.valueOf(reserva.getFecha());
    jDateChooser2.setDate(fecha);
    timePicker2.setTime(reserva.getHoraInicio());
    duracion.setSelectedItem(reserva.getDuracionHoras() + " Hora");

    // Cargar extras.
    balon.setValue(reserva.getBalon());
    arbitro.setValue(reserva.getArbitro());
    chaleco.setValue(reserva.getChalecos());

    // Seleccionar la cancha por código.
    for (int i = 0; i < canchadisponible.getItemCount(); i++) {
        String opcion = canchadisponible.getItemAt(i);
        if (opcion.startsWith(reserva.getCancha().getCodigo() + " - ")) {
            canchadisponible.setSelectedIndex(i);
            break;
        }
    }

    // En edición no se debe registrar un pago nuevo.
    busquedacliente.setEnabled(false);
    textocliente.setEnabled(false);
    cambiarControlesPago(false);

    reservar.setText("Guardar cambios");
    actualizarPrecios();
}

private void cambiarControlesPago(boolean habilitados) {
    adelanto.setEnabled(habilitados);
    total.setEnabled(habilitados);
    parcial.setEnabled(habilitados);
    pagoparcial.setEnabled(habilitados && parcial.isSelected());
    metodopago.setEnabled(habilitados);
}
   
    private void guardarCambiosReserva() {
    if (reservaEnEdicion == null) {
        JOptionPane.showMessageDialog(this, "No hay una reserva en edición.");
        return;
    }

    if (jDateChooser2.getDate() == null
            || timePicker2.getTime() == null
            || duracion.getSelectedItem() == null
            || canchadisponible.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this, "Completa fecha, hora, duración y cancha.");
        return;
    }

    java.time.LocalDate fecha = jDateChooser2.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

    java.time.LocalTime hora = timePicker2.getTime();

    int horas = Integer.parseInt(
            duracion.getSelectedItem().toString().replaceAll("[^0-9]", "")
    );

    String seleccion = canchadisponible.getSelectedItem().toString();
    String codigoCancha = seleccion.split(" - ")[0].trim();
    Cancha canchaNueva = persistenciacancha.buscarCancha(codigoCancha);

    if (canchaNueva == null) {
        JOptionPane.showMessageDialog(this, "No se encontró la cancha seleccionada.");
        return;
    }

    // Comprueba disponibilidad excluyendo la reserva que estamos editando.
    int jugadores = Integer.parseInt(
            numerojugadores.getSelectedItem().toString().replaceAll("[^0-9]", "")
    );

    int idExcluir = reservaEnEdicion.getIdReserva();

    Cancha[] disponibles = disponibilidad.buscarCanchasDisponibles(
            fecha, hora, horas, jugadores, idExcluir
    );

    boolean canchaDisponible = false;

    for (Cancha disponible : disponibles) {
        if (disponible == null) {
            break;
        }

        if (disponible.getCodigo().equalsIgnoreCase(codigoCancha)) {
            canchaDisponible = true;
            break;
        }
    }

    if (!canchaDisponible) {
        JOptionPane.showMessageDialog(
                this,
                "La cancha no está disponible para ese horario y cantidad de jugadores."
        );
        return;
    }

    actualizarPrecios();

    boolean modificada = persistenciaReservas.modificarReserva(
            reservaEnEdicion.getIdReserva(),
            canchaNueva,
            fecha,
            hora,
            horas,
            ((Number) balon.getValue()).intValue(),
            ((Number) arbitro.getValue()).intValue(),
            ((Number) chaleco.getValue()).intValue(),
            subtotalCanchaActual,
            totalExtrasActual,
            totalReserva
    );

    if (modificada) {
        persistenciaReservas.mostrarReservas(tablareserva);

        JOptionPane.showMessageDialog(
                this,
                "Reserva modificada"
        );

        reservaEnEdicion = null;
        reservar.setText("Reservar");
    } else {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo modificar la reserva."
        );
    }
}
    @SuppressWarnings("unchecked")  
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        pago = new javax.swing.ButtonGroup();
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
        textocliente = new javax.swing.JTextField();
        busquedacliente = new javax.swing.JButton();
        consultar = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        datoscliente = new javax.swing.JTextArea();
        jLabel24 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        timePicker2 = new com.github.lgooddatepicker.components.TimePicker();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        duracion = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        numerojugadores = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        canchadisponible = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        adelanto = new javax.swing.JCheckBox();
        total = new javax.swing.JCheckBox();
        jLabel30 = new javax.swing.JLabel();
        parcial = new javax.swing.JCheckBox();
        jPanel6 = new javax.swing.JPanel();
        jbalon = new javax.swing.JLabel();
        jarbitro = new javax.swing.JLabel();
        jchaleco = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        balon = new javax.swing.JSpinner();
        arbitro = new javax.swing.JSpinner();
        chaleco = new javax.swing.JSpinner();
        jPanel10 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        subtotal = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        metodopago = new javax.swing.JComboBox<>();
        pagoparcial = new javax.swing.JTextField();
        anticipo = new javax.swing.JLabel();
        reservar = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel47 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jSpinner4 = new javax.swing.JSpinner();
        jSpinner5 = new javax.swing.JSpinner();
        jSpinner6 = new javax.swing.JSpinner();
        jSpinner7 = new javax.swing.JSpinner();
        jLabel36 = new javax.swing.JLabel();
        jSpinner8 = new javax.swing.JSpinner();
        jLabel37 = new javax.swing.JLabel();
        jSpinner9 = new javax.swing.JSpinner();
        jLabel38 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        totalt = new javax.swing.JLabel();
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
        menureserva1 = new javax.swing.JPanel();
        Reservafinal = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        codigoReserva = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        datosReserva = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        jTextField3.setText("jTextField3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PanelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelmenu.setBackground(new java.awt.Color(0, 0, 51));

        panelbotones.setBackground(new java.awt.Color(0, 0, 51));
        panelbotones.setLayout(new javax.swing.BoxLayout(panelbotones, javax.swing.BoxLayout.LINE_AXIS));

        inicio.setText("Inicio");
        inicio.setBackground(new java.awt.Color(0, 0, 51));
        inicio.setBorder(null);
        inicio.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        inicio.setForeground(new java.awt.Color(255, 255, 255));
        inicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioActionPerformed(evt);
            }
        });

        reserva.setText("Reserva");
        reserva.setBackground(new java.awt.Color(0, 0, 51));
        reserva.setBorder(null);
        reserva.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reserva.setForeground(new java.awt.Color(255, 255, 255));
        reserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservaActionPerformed(evt);
            }
        });

        disponible.setText("Disponibilidad");
        disponible.setBackground(new java.awt.Color(0, 0, 51));
        disponible.setBorder(null);
        disponible.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        disponible.setForeground(new java.awt.Color(255, 255, 255));
        disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disponibleActionPerformed(evt);
            }
        });

        cancha.setText("Cancha");
        cancha.setBackground(new java.awt.Color(0, 0, 51));
        cancha.setBorder(null);
        cancha.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        cancha.setForeground(new java.awt.Color(255, 255, 255));
        cancha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canchaActionPerformed(evt);
            }
        });

        clientes.setText("Cientes");
        clientes.setBackground(new java.awt.Color(0, 0, 51));
        clientes.setBorder(null);
        clientes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        clientes.setForeground(new java.awt.Color(255, 255, 255));
        clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientesActionPerformed(evt);
            }
        });

        pagos.setText("Pagos");
        pagos.setBackground(new java.awt.Color(0, 0, 51));
        pagos.setBorder(null);
        pagos.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        pagos.setForeground(new java.awt.Color(255, 255, 255));
        pagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagosActionPerformed(evt);
            }
        });

        reportes.setText(" Actualizar");
        reportes.setBackground(new java.awt.Color(0, 102, 0));
        reportes.setBorder(null);
        reportes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reportes.setForeground(new java.awt.Color(255, 255, 255));
        reportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportesActionPerformed(evt);
            }
        });

        actualizar.setText("Reportes");
        actualizar.setBackground(new java.awt.Color(0, 0, 51));
        actualizar.setBorder(null);
        actualizar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        actualizar.setForeground(new java.awt.Color(255, 255, 255));
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
            .addGap(0, 890, Short.MAX_VALUE)
        );
        panelinicioLayout.setVerticalGroup(
            panelinicioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 685, Short.MAX_VALUE)
        );

        PanelContenido.addTab("inicio", panelinicio);

        canchapanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Reservas");
        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N

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
                .addContainerGap()
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 483, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        busquedacliente.setText("Buscar Cliente");
        busquedacliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        busquedacliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                busquedaclienteActionPerformed(evt);
            }
        });

        consultar.setText("Consultar");
        consultar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        consultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultarActionPerformed(evt);
            }
        });

        datoscliente.setColumns(20);
        datoscliente.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        datoscliente.setRows(5);
        jScrollPane4.setViewportView(datoscliente);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Fecha");
        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Hora");
        jLabel43.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Duración");
        jLabel44.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        duracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Hora", "2 Hora", "3 Hora", "4 Hora", "5 Hora", "6 Hora" }));
        duracion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Jugadores");
        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        numerojugadores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5 ", "7", "11 " }));
        numerojugadores.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(textocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(busquedacliente))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numerojugadores, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(timePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(consultar)))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(textocliente)
                            .addComponent(busquedacliente, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(timePicker2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel43)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel25)
                            .addComponent(numerojugadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44)
                            .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(consultar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        canchadisponible.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        canchadisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canchadisponibleActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        pago.add(adelanto);
        adelanto.setText("Anticipo 10%");
        adelanto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adelantoActionPerformed(evt);
            }
        });

        pago.add(total);
        total.setText("Total");
        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Pago");
        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        pago.add(parcial);
        parcial.setText("Parcial");
        parcial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parcialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(adelanto, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(29, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(total)
                            .addComponent(parcial))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(adelanto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(total)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parcial)
                .addGap(38, 38, 38))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbalon.setText("Balon");
        jbalon.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jarbitro.setText("Arbitro");
        jarbitro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jchaleco.setText("Chalecos");
        jchaleco.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Extras");
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jarbitro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbalon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jchaleco, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chaleco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(arbitro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(balon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbalon)
                    .addComponent(balon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jarbitro)
                    .addComponent(arbitro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchaleco)
                    .addComponent(chaleco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("SubTotal Q.");
        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel39.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        subtotal.setText("jLabel1");
        subtotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        subtotal.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel40.setText("Metodo de Pago");
        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        metodopago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ".", "Efectivo", "Tarjeta de Credito", "Transferencia" }));
        metodopago.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        metodopago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metodopagoActionPerformed(evt);
            }
        });

        pagoparcial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagoparcialActionPerformed(evt);
            }
        });

        anticipo.setText("Anticipo");
        anticipo.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(anticipo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pagoparcial, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(35, 35, 35))))))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anticipo, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagoparcial, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(subtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        reservar.setText("Reservar");
        reservar.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        reservar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(canchadisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(reservar, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(canchadisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reservar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel47.setText("Tienda");
        jLabel47.setBackground(new java.awt.Color(255, 255, 255));
        jLabel47.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N

        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel32.setText("Botella de agua");
        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel33.setText("Garrafon de agua");
        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel34.setText("Cerveza");
        jLabel34.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel35.setText("Productos");
        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel36.setText("Panes con Carne");
        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel37.setText("Hamburgueza");
        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel38.setText("Hidratante");
        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel37, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSpinner7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jSpinner9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(jSpinner9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jSpinner8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSpinner7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel47, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel47)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText(" Total Q.");
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        totalt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalt.setText("jLabel2");
        totalt.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(totalt, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addComponent(totalt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout canchapanelLayout = new javax.swing.GroupLayout(canchapanel);
        canchapanel.setLayout(canchapanelLayout);
        canchapanelLayout.setHorizontalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canchapanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(canchapanelLayout.createSequentialGroup()
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(137, Short.MAX_VALUE))
        );
        canchapanelLayout.setVerticalGroup(
            canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(canchapanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(canchapanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, canchapanelLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelContenido.addTab("reserva", canchapanel);

        jLabel5.setText("jLabel5");

        javax.swing.GroupLayout clientespanelLayout = new javax.swing.GroupLayout(clientespanel);
        clientespanel.setLayout(clientespanelLayout);
        clientespanelLayout.setHorizontalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientespanelLayout.createSequentialGroup()
                .addContainerGap(811, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        clientespanelLayout.setVerticalGroup(
            clientespanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientespanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel5)
                .addContainerGap(648, Short.MAX_VALUE))
        );

        PanelContenido.addTab("disponiblidad", clientespanel);

        jLabel62.setText("Gestion de Clientes");
        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N

        javax.swing.GroupLayout panelListaCanchasLayout = new javax.swing.GroupLayout(panelListaCanchas);
        panelListaCanchas.setLayout(panelListaCanchasLayout);
        panelListaCanchasLayout.setHorizontalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 854, Short.MAX_VALUE)
        );
        panelListaCanchasLayout.setVerticalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelListaCanchas);

        nuevachancha.setText("Crear Nueva Cancha");
        nuevachancha.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setText("Telefono");
        jLabel58.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N

        bRegistrar.setText("Registrar");
        bRegistrar.setBackground(new java.awt.Color(204, 204, 204));
        bRegistrar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        bRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bRegistrarActionPerformed(evt);
            }
        });

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel61.setText("Nombre");
        jLabel61.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N

        telefono.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        email.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        email.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emailActionPerformed(evt);
            }
        });

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel59.setText("e-mail");
        jLabel59.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel55.setText("Registrar Cliente");
        jLabel55.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(255, 0, 0));

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
                .addContainerGap(194, Short.MAX_VALUE))
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(120, 120, 120)
                    .addComponent(nombre1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(422, Short.MAX_VALUE)))
        );

        jPanel14.setBackground(new java.awt.Color(255, 255, 255));

        jLabel60.setText("Gestion de Clientes");
        jLabel60.setBackground(new java.awt.Color(255, 255, 255));
        jLabel60.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N

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

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Lista de Clientes");
        jLabel54.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 0, 0));

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

        buscar.setText("Buscar");
        buscar.setBackground(new java.awt.Color(204, 204, 204));
        buscar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
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

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel56.setText("Buscar Cliente");
        jLabel56.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(255, 0, 0));

        Modificar.setText("Modificar");
        Modificar.setBackground(new java.awt.Color(204, 204, 204));
        Modificar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarActionPerformed(evt);
            }
        });

        eliminar.setText("Eliminar");
        eliminar.setBackground(new java.awt.Color(204, 204, 204));
        eliminar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGap(0, 890, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 685, Short.MAX_VALUE)
        );

        PanelContenido.addTab("reportes", jPanel11);

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 890, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 685, Short.MAX_VALUE)
        );

        PanelContenido.addTab("tienda", jPanel12);

        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Buscar Reserva");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Ingrese Codigo de Reserva ");

        codigoReserva.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        codigoReserva.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton2.setText("Buscar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        datosReserva.setColumns(20);
        datosReserva.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        datosReserva.setRows(5);
        jScrollPane5.setViewportView(datosReserva);

        jButton3.setText("Modificar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Cancelar");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane5)
                            .addGroup(jPanel18Layout.createSequentialGroup()
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(codigoReserva))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(codigoReserva, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jButton5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton5.setText("Nueva Reserva");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton6.setText("Ver Reservas");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Control Reservas");

        javax.swing.GroupLayout ReservafinalLayout = new javax.swing.GroupLayout(Reservafinal);
        Reservafinal.setLayout(ReservafinalLayout);
        ReservafinalLayout.setHorizontalGroup(
            ReservafinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservafinalLayout.createSequentialGroup()
                .addGroup(ReservafinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ReservafinalLayout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(202, 202, 202)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ReservafinalLayout.createSequentialGroup()
                        .addGap(179, 179, 179)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ReservafinalLayout.createSequentialGroup()
                        .addGap(235, 235, 235)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(236, Short.MAX_VALUE))
        );
        ReservafinalLayout.setVerticalGroup(
            ReservafinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReservafinalLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addGap(56, 56, 56)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(ReservafinalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout menureserva1Layout = new javax.swing.GroupLayout(menureserva1);
        menureserva1.setLayout(menureserva1Layout);
        menureserva1Layout.setHorizontalGroup(
            menureserva1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menureserva1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Reservafinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        menureserva1Layout.setVerticalGroup(
            menureserva1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menureserva1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Reservafinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        PanelContenido.addTab("tab9", menureserva1);

        PanelPrincipal.add(PanelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 890, 720));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 996, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 6, Short.MAX_VALUE))
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
         PanelContenido.setSelectedIndex(8);
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

    private void consultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consultarActionPerformed
        if (clienteSelecionado == null) {
        JOptionPane.showMessageDialog(this, "Primero busca o registra un cliente.\n\nIngresa Carnet, Nombre o Telefono");return;
    }

    if (jDateChooser2.getDate() == null
            || timePicker2.getTime() == null
            || duracion.getSelectedItem() == null
            || numerojugadores.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(this,"Completa fecha, hora, duración y número de jugadores.");return;
    }

    //fecha
    java.time.LocalDate fecha = jDateChooser2.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

    // hora
    java.time.LocalTime horaInicio = timePicker2.getTime();

    // Convertir el texto de duración a número de horas
    String textoDuracion = duracion.getSelectedItem().toString();
    int duracionHoras = Integer.parseInt(
            textoDuracion.replaceAll("[^0-9]", "")
    );

    // Convertir el texto del combo a número de jugadores
    String textoJugadores = numerojugadores.getSelectedItem().toString();
    int jugadores = Integer.parseInt(
            textoJugadores.replaceAll("[^0-9]", "")
    );

    // Buscar canchas que cumplan capacidad y disponibilidad horaria
    int idExcluir = (reservaEnEdicion == null)
        ? -1
        : reservaEnEdicion.getIdReserva();

    Cancha[] disponibles = disponibilidad.buscarCanchasDisponibles(fecha,horaInicio,duracionHoras,jugadores,idExcluir);

    // Limpiar y llenar el combo de canchas disponibles
    javax.swing.DefaultComboBoxModel<String> modelo =
            new javax.swing.DefaultComboBoxModel<>();

    for (Archivos.Cancha cancha : disponibles) {
        if (cancha == null) {
            break;
        }

        modelo.addElement(
                cancha.getCodigo() + " - " + cancha.getNombre()
        );
    }

    canchadisponible.setModel(modelo);

    if (modelo.getSize() == 0) {
        subtotal.setText("Q 0.00");
        totalt.setText("Q 0.00");
        anticipo.setText("Anticipo 10%: Q 0.00");

        JOptionPane.showMessageDialog(
                this,
                "No hay canchas disponibles."
        );
        return;
    }

    canchadisponible.setSelectedIndex(0);
    actualizarPrecios();
    }//GEN-LAST:event_consultarActionPerformed

    private void adelantoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adelantoActionPerformed
    
    actualizarCampoPagoParcial();

    }//GEN-LAST:event_adelantoActionPerformed

    private void canchadisponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canchadisponibleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_canchadisponibleActionPerformed

    private void metodopagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metodopagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_metodopagoActionPerformed

    private void reservarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reservarActionPerformed
        
    if (reservaEnEdicion != null) {
        guardarCambiosReserva();
        return;
    }

    
      
    if (clienteSelecionado == null) {
        JOptionPane.showMessageDialog(this, "Primero selecciona un cliente.");
        return;
    }

    if (jDateChooser2.getDate() == null
            || timePicker2.getTime() == null
            || duracion.getSelectedItem() == null
            || canchadisponible.getSelectedItem() == null) {
        JOptionPane.showMessageDialog(
                this,
                "Completa cliente, fecha, hora, duración y cancha."
        );
        return;
    }

    // Debe estar seleccionada exactamente una opción de pago.
    int opcionesPago = 0;
    if (adelanto.isSelected()) opcionesPago++;
    if (total.isSelected()) opcionesPago++;
    if (parcial.isSelected()) opcionesPago++;

    if (opcionesPago != 1) {
        JOptionPane.showMessageDialog(
                this,
                "Selecciona solo una opción de pago."
        );
        return;
    }

    Object metodoSeleccionado = metodopago.getSelectedItem();

    if (metodoSeleccionado == null
            || metodoSeleccionado.toString().trim().equals(".")
            || metodoSeleccionado.toString().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Selecciona un método de pago.");
        return;
    }
    String metodo = metodoSeleccionado.toString();

    // Actualiza el total con la cancha y los extras actuales.
    actualizarPrecios();

    double anticipoMinimo =
            Math.round(totalReserva * 0.10 * 100.0) / 100.0;

    double montoPago;

    if (adelanto.isSelected()) {
        montoPago = anticipoMinimo;

    } else if (total.isSelected()) {
        montoPago = totalReserva;

    } else {
        try {
            montoPago = Double.parseDouble(
                    pagoparcial.getText().trim().replace(",", ".")
            );
        } catch (NumberFormatException e) {JOptionPane.showMessageDialog(this,"Ingresa una cantidad válida.");
            return;
        }

        if (montoPago <= anticipoMinimo || montoPago >= totalReserva) {
            JOptionPane.showMessageDialog(this,"El pago parcial debe ser mayor al anticipo y menor al total.");
            return;
        }
    }

    String seleccionCancha =
            canchadisponible.getSelectedItem().toString();
    String codigoCancha = seleccionCancha.split(" - ")[0].trim();

    canchaSelecionada =
            persistenciacancha.buscarCancha(codigoCancha);

    if (canchaSelecionada == null) {
        JOptionPane.showMessageDialog(this, "No se encontró la cancha.");
        return;
    }

    java.time.LocalDate fecha = jDateChooser2.getDate().toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();

    java.time.LocalTime hora = timePicker2.getTime();

    int horas = Integer.parseInt(
            duracion.getSelectedItem().toString().replaceAll("[^0-9]", "")
    );

    int idReserva = persistenciaReservas.generarSiguienteId();

    Reserva reserva = new Reserva(
            idReserva,
            clienteSelecionado,
            canchaSelecionada,
            fecha,
            hora,
            horas,
            ((Number) balon.getValue()).intValue(),
            ((Number) arbitro.getValue()).intValue(),
            ((Number) chaleco.getValue()).intValue()
    );

    reserva.setEstado("CONFIRMADA");
    reserva.setSubtotalCancha(subtotalCanchaActual);
    reserva.setTotalExtras(totalExtrasActual);
    reserva.setTotalReserva(totalReserva);

    if (!persistenciaReservas.agregarReserva(reserva)) {
        JOptionPane.showMessageDialog(
                this,
                "No se pudo guardar la reserva."
        );
        return;
    }

    Pago pagoInicial = new Pago(
            "P" + System.currentTimeMillis(),
            idReserva,
            LocalDateTime.now(),
            montoPago,
            metodo
    );

    if (!persistenciaPagos.agregarPago(pagoInicial)) {
        JOptionPane.showMessageDialog(
                this,
                "No hay espacio para agregar el pago."
        );
        return;
    }

    persistenciaReservas.mostrarReservas(tablareserva);

   double saldoPendiente = totalReserva - montoPago;

    String mensaje =
            "Reserva: R" + String.format("%03d", reserva.getIdReserva()) + "\n"
            + "Cliente: " + reserva.getCliente().getNombre() + "\n"
            + "Cancha: " + reserva.getCancha().getNombre() + "\n"
            + "Fecha: " + reserva.getFecha() + "\n"
            + "Horario: " + reserva.getHoraInicio()
            + " - " + reserva.getHoraFinal() + "\n"
            + "Duración: " + reserva.getDuracionHoras() + " hora(s)\n\n"
            + "Balones: " + reserva.getBalon() + "\n"
            + "Árbitros: " + reserva.getArbitro() + "\n"
            + "Chalecos: " + reserva.getChalecos() + "\n\n"
            + "Total: Q " + String.format("%.2f", totalReserva) + "\n"
            + "Pagado: Q " + String.format("%.2f", montoPago) + "\n"
            + "Pendiente: Q " + String.format("%.2f", saldoPendiente);

    JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Reserva realizada",
            JOptionPane.INFORMATION_MESSAGE
    );

        
        
        
        
        
        
    }//GEN-LAST:event_reservarActionPerformed

    private void pagoparcialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagoparcialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pagoparcialActionPerformed

    private void parcialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parcialActionPerformed
      actualizarCampoPagoParcial();
    
    }//GEN-LAST:event_parcialActionPerformed

    private void totalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalActionPerformed
        actualizarCampoPagoParcial();
    }//GEN-LAST:event_totalActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         PanelContenido.setSelectedIndex(1);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
       Reservas ventana = new Reservas(persistenciaReservas, persistenciaPagos);
ventana.setLocationRelativeTo(this);
ventana.setVisible(true);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

    String codigo = codigoReserva.getText().trim().toUpperCase();

    if (!codigo.matches("R\\d{3,}")) {
        JOptionPane.showMessageDialog(
                this,
                "Escribe un código como R001."
        );
        return;
    }

    try {
        int id = Integer.parseInt(codigo.substring(1));
        String codigoCorrecto = "R" + String.format("%03d", id);

        if (!codigo.equals(codigoCorrecto)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato incorrecto. ej. R001."
            );
            return;
        }

        reservaSeleccionada = persistenciaReservas.buscarReserva(id);

        if (reservaSeleccionada == null) {
            datosReserva.setText("No se encontró la reserva " + codigo);
            return;
        }

        datosReserva.setText(
                "Código: " + codigoCorrecto
                + "\nCliente: " + reservaSeleccionada.getCliente().getNombre()
                + "\nCancha: " + reservaSeleccionada.getCancha().getNombre()
                + "\nFecha: " + reservaSeleccionada.getFecha()
                + "\nHorario: " + reservaSeleccionada.getHoraInicio()
                + " - " + reservaSeleccionada.getHoraFinal()
                + "\nDuración: " + reservaSeleccionada.getDuracionHoras() + " hora(s)"
                + "\nEstado: " + reservaSeleccionada.getEstado()
                + "\nBalones: " + reservaSeleccionada.getBalon()
                + "\nÁrbitros: " + reservaSeleccionada.getArbitro()
                + "\nChalecos: " + reservaSeleccionada.getChalecos()
                + "\nTotal: Q"
                + String.format("%.2f", reservaSeleccionada.getTotalReserva())
        );

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
                this,
                "El código debe tener formato R001."
        );
    }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

    if (reservaSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Primero busca una reserva.");
        return;
    }

    String estado = reservaSeleccionada.getEstado();

    if (estado.equalsIgnoreCase("FINALIZADA")
            || estado.equalsIgnoreCase("VENCIDA")
            || estado.equalsIgnoreCase("CANCELADA")) {
        JOptionPane.showMessageDialog(
                this,
                "No se puede Cancelar una reserva que ya sido " + estado + "."
        );
        return;
    }

    int respuesta = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas cancelar la reserva R"
                    + String.format("%03d", reservaSeleccionada.getIdReserva())
                    + "?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION
    );

    if (respuesta == JOptionPane.YES_OPTION) {
        int id = reservaSeleccionada.getIdReserva();
        boolean cancelada = persistenciaReservas.cancelarReserva(id);

        if (cancelada) {
            reservaSeleccionada = persistenciaReservas.buscarReserva(id);
            datosReserva.setText("Estado: CANCELADA");

            JOptionPane.showMessageDialog(
                    this,
                    "Reserva cancelada."
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cancelar la reserva."
            );
        }
    }


    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (reservaSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Primero busca una reserva.");
        return;
    }

    String estado = reservaSeleccionada.getEstado();

    if (!estado.equalsIgnoreCase("PENDIENTE")
            && !estado.equalsIgnoreCase("CONFIRMADA")) {
        JOptionPane.showMessageDialog(
                this,
                "Solo puedes modificar reservas pendientes o confirmadas."
        );
        return;
    }

    cargarReservaParaModificar(reservaSeleccionada);
    PanelContenido.setSelectedIndex(1);
    }//GEN-LAST:event_jButton3ActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Modificar;
    private javax.swing.JTabbedPane PanelContenido;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JPanel Reservafinal;
    private javax.swing.JButton actualizar;
    private javax.swing.JCheckBox adelanto;
    private javax.swing.JLabel anticipo;
    private javax.swing.JSpinner arbitro;
    private javax.swing.JButton bRegistrar;
    private javax.swing.JSpinner balon;
    private javax.swing.JButton buscar;
    private javax.swing.JTextField busqueda;
    private javax.swing.JButton busquedacliente;
    private javax.swing.JButton cancha;
    private javax.swing.JComboBox<String> canchadisponible;
    private javax.swing.JPanel canchapanel;
    private javax.swing.JSpinner chaleco;
    private javax.swing.JButton clientes;
    private javax.swing.JPanel clientespanel;
    private javax.swing.JTextField codigoReserva;
    private javax.swing.JButton consultar;
    private javax.swing.JTextArea datosReserva;
    private javax.swing.JTextArea datoscliente;
    private javax.swing.JButton disponible;
    private javax.swing.JComboBox<String> duracion;
    private javax.swing.JButton eliminar;
    private javax.swing.JTextField email;
    private javax.swing.JButton inicio;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
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
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSpinner jSpinner4;
    private javax.swing.JSpinner jSpinner5;
    private javax.swing.JSpinner jSpinner6;
    private javax.swing.JSpinner jSpinner7;
    private javax.swing.JSpinner jSpinner8;
    private javax.swing.JSpinner jSpinner9;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel jarbitro;
    private javax.swing.JLabel jbalon;
    private javax.swing.JLabel jchaleco;
    private javax.swing.JLabel logo;
    private javax.swing.JLabel mcodigo;
    private javax.swing.JTextField mcorreo;
    private javax.swing.JPanel menureserva1;
    private javax.swing.JComboBox<String> metodopago;
    private javax.swing.JTextField mnombre;
    private javax.swing.JTextField mtelefono;
    private javax.swing.JTextField nombre1;
    private javax.swing.JButton nuevachancha;
    private javax.swing.JComboBox<String> numerojugadores;
    private javax.swing.ButtonGroup pago;
    private javax.swing.JTextField pagoparcial;
    private javax.swing.JButton pagos;
    private javax.swing.JPanel pagospanel;
    private javax.swing.JPanel panelListaCanchas;
    private javax.swing.JPanel panelbotones;
    private javax.swing.JPanel panelclientes;
    private javax.swing.JPanel panelinicio;
    private javax.swing.JPanel panelmenu;
    private javax.swing.JPanel panelreserva;
    private javax.swing.JCheckBox parcial;
    private javax.swing.JButton reportes;
    private javax.swing.JButton reserva;
    private javax.swing.JButton reservar;
    private javax.swing.JLabel subtotal;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTable tablareserva;
    private javax.swing.JTextField telefono;
    private javax.swing.JTextField textocliente;
    private com.github.lgooddatepicker.components.TimePicker timePicker2;
    private javax.swing.JCheckBox total;
    private javax.swing.JLabel totalt;
    // End of variables declaration//GEN-END:variables
}
