package Grafica;

import Archivos.Cancha;
import Archivos.Cliente;
import Archivos.Pago;
import Archivos.Reserva;
import Cerebro.PersistenciaTienda;

import Cerebro.PersistenciaClientes;
import Cerebro.PersistenciaCancha; 
import Cerebro.PersistenciaReservas;
import Cerebro.Disponibilidad;
import Cerebro.CalcularPrecio;
import Cerebro.PersistenciaPago;
import Cerebro.PersistenciaTienda;

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

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Font;




public class Grafica extends javax.swing.JFrame {
    
    private boolean miVariableBooleana = false;
    private int pasoSecuencia = 0;
    private final int[] secuenciaEsperada = {1, 1, 9, 4, 3}; 
       
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Grafica.class.getName());
    private PersistenciaClientes persistencia = new PersistenciaClientes();
    private PersistenciaCancha persistenciacancha = new PersistenciaCancha();
    private PersistenciaReservas persistenciaReservas;
    private Disponibilidad disponibilidad;
    
    private CalcularPrecio calculadora = new CalcularPrecio();
    private PersistenciaPago persistenciaPagos = new PersistenciaPago();
    private Reserva reservaSeleccionada;
    private Reserva reservaEnEdicion = null;
    private javax.swing.Timer temporizadorEstados;
    private PersistenciaTienda persistenciaTienda = new PersistenciaTienda();
    
    private Cancha canchaSelecionada;
    private Cliente clienteSelecionado;
    private double totalReserva;
    private double subtotalCanchaActual;
    private double totalExtrasActual;
   
   
    
    public Grafica() {
    initComponents();
     configuracionAdicionalVentana();  
    persistenciaTienda.setActiva(true);

    setResizable(false);
    pack();
    setLocationRelativeTo(null);
    ocultarPestanas();

    // Cargar clientes y canchas antes de cargar las reservas.
    persistencia.leerClientes();
    mostrarClientesEnTabla();

    persistenciacancha.leerCanchas();
    mostrarCanchasEnGrafica();

    // Crear una sola persistencia de reservas y cargar su arreglo.
    persistenciaReservas = new PersistenciaReservas(persistencia, persistenciacancha);

    persistenciaReservas.leerReservas();
    persistenciaReservas.actualizarEstadosPorHora();

    // Cargar los pagos una sola vez.
    persistenciaPagos.leerPagos();

       //cargo tienda
       persistenciaTienda.leerProductos();
       persistenciaTienda.leerCompras();
 
    // Disponibilidad usa el mismo arreglo de reservas del programa.
    disponibilidad = new Disponibilidad(persistenciacancha, persistenciaReservas);

    // Mostrar información que depende de las persistencias ya cargadas.
    mostrarCanchasDashboard();
    actualizarDashboard();

    // Configurar spinners antes de agregar sus listeners.
    balon.setModel(
            new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
    );
    arbitro.setModel(
            new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
    );
    chaleco.setModel(
            new javax.swing.SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)
    );

    pagoparcial.setEnabled(false);
    metodopago.setEnabled(false);

    balon.addChangeListener(e -> actualizarPrecios());
    arbitro.addChangeListener(e -> actualizarPrecios());
    chaleco.addChangeListener(e -> actualizarPrecios());
    canchadisponible.addActionListener(e -> actualizarPrecios());

    mostrarPreciosExtras();

    datoscliente.setEditable(false);
    datoscliente.setLineWrap(true);
    datoscliente.setWrapStyleWord(true);

    adelanto.addActionListener(e -> actualizarMetodoPago());
    total.addActionListener(e -> actualizarMetodoPago());
    parcial.addActionListener(e -> actualizarMetodoPago());

    // Actualizar estados y el dashboard cada 30 segundos.
    temporizadorEstados = new javax.swing.Timer(30_000, e -> {
        boolean huboCambios =
                persistenciaReservas.actualizarEstadosPorHora();

        if (huboCambios) {
            actualizarDashboard();
            mostrarCanchasDashboard();
        }
    });

    temporizadorEstados.start();

    CONT.setLayout(new java.awt.GridLayout(1, 2, 10, 0));
    CONT.revalidate();
    CONT.repaint();
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
   
    private void actualizarMetodoPago() {
        boolean hayPagoSeleccionado =
                adelanto.isSelected()
                || total.isSelected()
                || parcial.isSelected();

        metodopago.setEnabled(hayPagoSeleccionado);

        if (hayPagoSeleccionado) {
            metodopago.requestFocus();
        } else {
            metodopago.setSelectedIndex(0); // Debe ser la opción inicial, por ejemplo "."
        }
    }
    
    private void limpiarFormularioReserva() {
    reservaEnEdicion = null;
    clienteSelecionado = null;

    textocliente.setText("");
    textocliente.setEnabled(true);
    busquedacliente.setEnabled(true);
    datoscliente.setText("");

    jDateChooser2.setDate(null);
    timePicker2.setTime(null);

    numerojugadores.setSelectedIndex(0);
    duracion.setSelectedIndex(0);
    canchadisponible.removeAllItems();

    balon.setValue(0);
    arbitro.setValue(0);
    chaleco.setValue(0);

    // --- CORRECCIÓN DE PAGOS ---
    pago.clearSelection();
    
    // Habilitar nuevamente los checkboxes de pago por si se bloquearon en edición
    adelanto.setEnabled(true);
    total.setEnabled(true);
    parcial.setEnabled(true);

    actualizarMetodoPago();

    pagoparcial.setText("");
    pagoparcial.setEnabled(false); // Se mantiene en false hasta que seleccionen pago parcial

    metodopago.setSelectedIndex(0);
    metodopago.setEnabled(true); // <--- HABILITADO para que se pueda elegir método de pago

    subtotal.setText("Q 0.00");
    totalt.setText("Q 0.00");
    anticipo.setText("Anticipo 10%: Q 0.00");

    reservar.setText("Reservar");
}
   
      
    private void guardarCambiosReserva() {
        
        if (!validarFechaYHora()) {
            return;
        }
        
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
     int cotizacionesVencidas = 0;

        if (reservaEnEdicion.getEstado().equalsIgnoreCase("CONFIRMADA")) {
            cotizacionesVencidas =
                    persistenciaReservas.vencerCotizacionesEnConflicto(
                            canchaNueva.getCodigo(),
                            fecha,
                            hora,
                            horas
                    );
        }
          
    

     // El objeto ya fue actualizado por PersistenciaReservas.
     String mensaje =
             "Reserva modificada: R"
             + String.format("%03d", reservaEnEdicion.getIdReserva())
             + "\nCliente: " + reservaEnEdicion.getCliente().getNombre()
             + "\nCancha: " + reservaEnEdicion.getCancha().getNombre()
             + "\nFecha: " + reservaEnEdicion.getFecha()
             + "\nHorario: " + reservaEnEdicion.getHoraInicio()
             + " - " + reservaEnEdicion.getHoraFinal()
             + "\nDuración: " + reservaEnEdicion.getDuracionHoras() + " hora(s)"
             + "\nTotal: Q "
             + String.format("%.2f", reservaEnEdicion.getTotalReserva());

     JOptionPane.showMessageDialog(
             this,
             mensaje,
             "Reserva modificada",
             JOptionPane.INFORMATION_MESSAGE
     );

        
            limpiarFormularioReserva();

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo modificar la reserva."
            );
        } 
}
    
    private boolean validarFechaYHora() {
        if (jDateChooser2.getDate() == null || timePicker2.getTime() == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una fecha y una hora.");
            return false;
        }

        java.time.LocalDate fecha = jDateChooser2.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        java.time.LocalTime hora = timePicker2.getTime();
        java.time.LocalDate hoy = java.time.LocalDate.now();

        if (fecha.isBefore(hoy)) {
            JOptionPane.showMessageDialog(this, "No puedes seleccionar una fecha pasada.");
            return false;
        }

        if (fecha.equals(hoy) && hora.isBefore(java.time.LocalTime.now())) {
            JOptionPane.showMessageDialog(this, "La hora seleccionada ya pasó.");
            return false;
        }

        return true;
    }
    
    private void actualizarDashboard() {
    java.time.LocalDate hoy = java.time.LocalDate.now();

    int reservasDeHoy = 0;
    int cotizacionesPendientes = 0;
    double saldoPorRecaudar = 0;

    for (int i = 0; i < persistenciaReservas.getCantidadReservas(); i++) {
        Archivos.Reserva reserva = persistenciaReservas.getReserva(i);

        if (reserva == null) {
            continue;
        }

        String estado = reserva.getEstado();

        // Cuenta las reservas confirmadas o realizadas para hoy.
        if (reserva.getFecha().equals(hoy)
                && (estado.equalsIgnoreCase("CONFIRMADA")
                || estado.equalsIgnoreCase("EN_CURSO")
                || estado.equalsIgnoreCase("FINALIZADA"))) {
            reservasDeHoy++;
        }

        // Cuenta cotizaciones pendientes, sin tratarlas como deuda.
        if (estado.equalsIgnoreCase("PENDIENTE")
                && reserva.getFecha().equals(hoy)) {
            cotizacionesPendientes++;
        }

        // El saldo por cobrar excluye cotizaciones y reservas canceladas/vencidas.
        if (estado.equalsIgnoreCase("CONFIRMADA")
                || estado.equalsIgnoreCase("EN_CURSO")
                || estado.equalsIgnoreCase("FINALIZADA")) {

            double total = reserva.getTotalReserva();
            double pagado = persistenciaPagos.getTotalPagadoReserva(
                    reserva.getIdReserva()
            );

            saldoPorRecaudar += Math.max(0, total - pagado);
        }
    }

    double ingresosHoy = persistenciaPagos.getTotalPagadoEnFecha(java.time.LocalDate.now());
    recaudado.setText("Q " + String.format("%.2f", ingresosHoy));

    reservashoy.setText(String.valueOf(reservasDeHoy));
    cotizaciones.setText(String.valueOf(cotizacionesPendientes));
    cobrar.setText("Q " + String.format("%.2f", saldoPorRecaudar));
}
    
  private void mostrarCanchasDashboard() {
    if (panelcancha == null) return;

    panelcancha.removeAll();
    panelcancha.setLayout(new GridLayout(0, 2, 10, 10));

    int totalCanchas = persistenciacancha.getCantidadCanchas();

    for (int i = 0; i < totalCanchas; i++) {
        Cancha cancha = persistenciacancha.getCancha(i);

        if (cancha == null) {
            continue;
        }

        // 🛑 SI LA CANCHA ESTÁ INACTIVA (ELIMINADA), LA IGNORAMOS Y NO LA MOSTRAMOS
        if (!cancha.isActivo()) {
            continue; 
        }

        String estado = cancha.getEstado();

        JPanel tarjeta = new JPanel(new GridLayout(0, 1));
        tarjeta.setBorder(
                BorderFactory.createTitledBorder(cancha.getNombre())
        );

        tarjeta.add(new JLabel("Tipo: Fútbol " + cancha.getTipoFutbol()));
        tarjeta.add(new JLabel("Estado: " + estado));

        // Color según el estado
        if (estado.equalsIgnoreCase("DISPONIBLE")) {
            tarjeta.setBackground(new java.awt.Color(198, 239, 206));
        } else if (estado.equalsIgnoreCase("MANTENIMIENTO")) {
            tarjeta.setBackground(new java.awt.Color(255, 235, 156));
        } else if (estado.equalsIgnoreCase("EN_CURSO")) {
            tarjeta.setBackground(new java.awt.Color(189, 215, 238));
        } else {
            tarjeta.setBackground(new java.awt.Color(217, 217, 217));
        }

        for (java.awt.Component componente : tarjeta.getComponents()) {
            componente.setBackground(tarjeta.getBackground());
            componente.setForeground(java.awt.Color.BLACK);
            if (componente instanceof JLabel) {
                ((JLabel) componente).setOpaque(true);
            }
        }

        tarjeta.setOpaque(true);
        panelcancha.add(tarjeta);
    }

    panelcancha.revalidate();
    panelcancha.repaint();
    if (panelcancha.getParent() != null) {
        panelcancha.getParent().revalidate();
        panelcancha.getParent().repaint();
    }
}
    
    private void mostrarDetalleReserva() {
    if (reservaSeleccionada == null) {
        return;
    }

    String codigo = "R"
            + String.format("%03d", reservaSeleccionada.getIdReserva());

    datosReserva.setText(
            "Código: " + codigo
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
            + "\nTotal de reserva: Q "
            + String.format("%.2f", reservaSeleccionada.getTotalReserva())
    );

    datosReserva.append("\n\nTIENDA\n");

    Archivos.Compra[] compras =
            persistenciaTienda.buscarComprasReserva(codigo);

    if (compras.length == 0) {
        datosReserva.append("Sin productos comprados.");
        return;
    }

    for (Archivos.Compra compra : compras) {
        datosReserva.append(
                compra.getNombreProducto()
                + " | Cantidad: " + compra.getCantidad()
                + " | Subtotal: Q "
                + String.format("%.2f", compra.getSubtotal())
                + "\n"
        );
    }

    datosReserva.append(
            "Total de tienda: Q "
            + String.format(
                    "%.2f",
                    persistenciaTienda.calcularTotalComprasReserva(codigo)
            )
    );
}
       //estos captan el evento carnet
       private void configuracionAdicionalVentana() {
      
        lblEstado.setFont(new Font("Arial", Font.BOLD, 16));
        lblEstado.setOpaque(true);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setBackground(new Color(255, 182, 193)); // Color inicial (Rosa = false)
        
        // Inicializamos el texto
        lblEstado.setText("Tienda: Desactivada");

        // Registramos el escuchador global de teclas
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.isControlDown()) {
                        int keyCode = e.getKeyCode();
                        int digitoPresionado = -1;
                        
                        // Mapeo de teclas numéricas principales y Numpad
                        if (keyCode == KeyEvent.VK_1 || keyCode == KeyEvent.VK_NUMPAD1) digitoPresionado = 1;
                        else if (keyCode == KeyEvent.VK_9 || keyCode == KeyEvent.VK_NUMPAD9) digitoPresionado = 9;
                        else if (keyCode == KeyEvent.VK_4 || keyCode == KeyEvent.VK_NUMPAD4) digitoPresionado = 4;
                        else if (keyCode == KeyEvent.VK_3 || keyCode == KeyEvent.VK_NUMPAD3) digitoPresionado = 3;
                        
                        if (digitoPresionado != -1) {
                            if (digitoPresionado == secuenciaEsperada[pasoSecuencia]) {
                                pasoSecuencia++;
                                if (pasoSecuencia == secuenciaEsperada.length) {
                                    alternarValorBooleano();
                                    pasoSecuencia = 0;
                                }
                            } else {
                                pasoSecuencia = 0;
                            }
                        } else if (keyCode != KeyEvent.VK_CONTROL) {
                            pasoSecuencia = 0;
                        }
                    } else {
                        pasoSecuencia = 0;
                    }
                }
                return false;
            }
        });
    }

        private void alternarValorBooleano() {
        miVariableBooleana = !miVariableBooleana;
        
        if (miVariableBooleana) {
            lblEstado.setText("Tienda: Activda ");
            lblEstado.setBackground(new Color(144, 238, 144)); // Verde claro
        } else {
            lblEstado.setText("Tienda: Desactivada");
            lblEstado.setBackground(new Color(255, 182, 193)); // Rosa claro
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
        inicio = new javax.swing.JButton();
        disponible = new javax.swing.JButton();
        cancha = new javax.swing.JButton();
        clientes = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        tiendabt = new javax.swing.JButton();
        reportes = new javax.swing.JButton();
        PanelContenido = new javax.swing.JTabbedPane();
        panelinicio = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        reservashoy = new javax.swing.JLabel();
        jPanel24 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        cotizaciones = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        recaudado = new javax.swing.JLabel();
        jPanel26 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        cobrar = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        panelcancha = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblEstado = new javax.swing.JLabel();
        canchapanel = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
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
        canchadisponible = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel39 = new javax.swing.JLabel();
        subtotal = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        metodopago = new javax.swing.JComboBox<>();
        derecho = new javax.swing.JPanel();
        adelanto = new javax.swing.JCheckBox();
        total = new javax.swing.JCheckBox();
        jLabel30 = new javax.swing.JLabel();
        parcial = new javax.swing.JCheckBox();
        anticipo = new javax.swing.JLabel();
        pagoparcial = new javax.swing.JTextField();
        reservar = new javax.swing.JButton();
        CONT = new javax.swing.JPanel();
        izquierdo = new javax.swing.JPanel();
        jbalon = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        balon = new javax.swing.JSpinner();
        arbitro = new javax.swing.JSpinner();
        chaleco = new javax.swing.JSpinner();
        jarbitro = new javax.swing.JLabel();
        jchaleco = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        totalt = new javax.swing.JLabel();
        menureserva1 = new javax.swing.JPanel();
        Reservafinal = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        codigoReserva = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        datosReserva = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        btcancelar = new javax.swing.JButton();
        pagar = new javax.swing.JButton();
        tienda = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
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

        jButton1.setText("jButton1");

        jTextField3.setText("jTextField3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        PanelPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PanelPrincipal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelmenu.setBackground(new java.awt.Color(0, 0, 102));

        logo.setIcon(new javax.swing.ImageIcon("C:\\Users\\juan1\\OneDrive\\Documentos\\NetBeansProjects\\CanchaPro\\src\\main\\java\\Grafica\\logo.png")); // NOI18N

        inicio.setText("Inicio");
        inicio.setBackground(new java.awt.Color(0, 0, 102));
        inicio.setBorder(null);
        inicio.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        inicio.setForeground(new java.awt.Color(255, 255, 255));
        inicio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inicioActionPerformed(evt);
            }
        });

        disponible.setText("Reservas");
        disponible.setBackground(new java.awt.Color(0, 0, 102));
        disponible.setBorder(null);
        disponible.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        disponible.setForeground(new java.awt.Color(255, 255, 255));
        disponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disponibleActionPerformed(evt);
            }
        });

        cancha.setText("Canchas");
        cancha.setBackground(new java.awt.Color(0, 0, 102));
        cancha.setBorder(null);
        cancha.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        cancha.setForeground(new java.awt.Color(255, 255, 255));
        cancha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canchaActionPerformed(evt);
            }
        });

        clientes.setText("Clientes");
        clientes.setBackground(new java.awt.Color(0, 0, 102));
        clientes.setBorder(null);
        clientes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        clientes.setForeground(new java.awt.Color(255, 255, 255));
        clientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clientesActionPerformed(evt);
            }
        });

        guardar.setText(" Actualizar");
        guardar.setBackground(new java.awt.Color(0, 102, 0));
        guardar.setBorder(null);
        guardar.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        guardar.setForeground(new java.awt.Color(255, 255, 255));
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });

        tiendabt.setBackground(new java.awt.Color(0, 0, 102));
        tiendabt.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        tiendabt.setForeground(new java.awt.Color(255, 255, 255));
        tiendabt.setText("Tienda");
        tiendabt.setBorder(null);
        tiendabt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tiendabtActionPerformed(evt);
            }
        });

        reportes.setText("Reportes");
        reportes.setBackground(new java.awt.Color(0, 0, 102));
        reportes.setBorder(null);
        reportes.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N
        reportes.setForeground(new java.awt.Color(255, 255, 255));
        reportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelmenuLayout = new javax.swing.GroupLayout(panelmenu);
        panelmenu.setLayout(panelmenuLayout);
        panelmenuLayout.setHorizontalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(disponible, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clientes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cancha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelmenuLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tiendabt, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(panelmenuLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(logo)
                        .addGap(0, 14, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(reportes, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelmenuLayout.setVerticalGroup(
            panelmenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelmenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logo)
                .addGap(52, 52, 52)
                .addComponent(inicio, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disponible, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancha, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clientes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tiendabt, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(guardar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        PanelPrincipal.add(panelmenu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 800));

        PanelContenido.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        PanelContenido.setPreferredSize(new java.awt.Dimension(800, 740));

        panelinicio.setBackground(new java.awt.Color(255, 255, 255));
        panelinicio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));

        jPanel22.setBackground(new java.awt.Color(51, 255, 204));
        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel22.setPreferredSize(new java.awt.Dimension(177, 105));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Reservas Hoy");
        jLabel9.setBackground(new java.awt.Color(0, 255, 204));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel9.setOpaque(true);

        reservashoy.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        reservashoy.setText("3");
        reservashoy.setBackground(new java.awt.Color(153, 255, 255));
        reservashoy.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        reservashoy.setOpaque(true);

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reservashoy, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reservashoy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel24.setBackground(new java.awt.Color(51, 255, 204));
        jPanel24.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("Cotizaciones");
        jLabel11.setBackground(new java.awt.Color(0, 255, 204));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel11.setOpaque(true);

        cotizaciones.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cotizaciones.setText("3");
        cotizaciones.setBackground(new java.awt.Color(153, 255, 255));
        cotizaciones.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        cotizaciones.setOpaque(true);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cotizaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cotizaciones, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel25.setBackground(new java.awt.Color(51, 255, 204));
        jPanel25.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel25.setPreferredSize(new java.awt.Dimension(177, 105));

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Recaudado Hoy");
        jLabel13.setBackground(new java.awt.Color(0, 255, 204));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel13.setOpaque(true);

        recaudado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        recaudado.setText("3");
        recaudado.setBackground(new java.awt.Color(153, 255, 255));
        recaudado.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        recaudado.setOpaque(true);

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(recaudado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(recaudado, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel26.setBackground(new java.awt.Color(51, 255, 204));
        jPanel26.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel26.setForeground(new java.awt.Color(153, 255, 255));
        jPanel26.setPreferredSize(new java.awt.Dimension(177, 105));

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Por Recaudar Hoy");
        jLabel15.setBackground(new java.awt.Color(0, 255, 204));
        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel15.setOpaque(true);

        cobrar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cobrar.setText("3");
        cobrar.setBackground(new java.awt.Color(153, 255, 255));
        cobrar.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        cobrar.setOpaque(true);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                    .addComponent(cobrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cobrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                            .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        panelinicio.add(jPanel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 970, -1));

        panelcancha.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelcanchaLayout = new javax.swing.GroupLayout(panelcancha);
        panelcancha.setLayout(panelcanchaLayout);
        panelcanchaLayout.setHorizontalGroup(
            panelcanchaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelcanchaLayout.setVerticalGroup(
            panelcanchaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(panelcancha);

        panelinicio.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 360, 910, 360));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel10.setText("Cancha Pro");
        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI Semibold", 3, 48)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 51));

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("Para Activar o Desactivar la tienda precione Ctrl + 11943");

        lblEstado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblEstado.setToolTipText("precione Ctrl + 11943");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(14, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        panelinicio.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 970, -1));

        PanelContenido.addTab("inicio", panelinicio);

        canchapanel.setBackground(new java.awt.Color(255, 255, 255));
        canchapanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        canchapanel.setPreferredSize(new java.awt.Dimension(800, 740));
        canchapanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Reservas");
        jLabel46.setBackground(new java.awt.Color(255, 255, 255));
        jLabel46.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(765, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel46)
                .addContainerGap())
        );

        canchapanel.add(jPanel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 13, 980, -1));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
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
        datoscliente.setBorder(null);
        jScrollPane4.setViewportView(datoscliente);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Fecha");
        jLabel24.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel43.setText("Hora");
        jLabel43.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel44.setText("Duración");
        jLabel44.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        duracion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1 Hora", "2 Hora", "3 Hora", "4 Hora", "5 Hora", "6 Hora" }));
        duracion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Jugadores");
        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        numerojugadores.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "5 ", "7", "11 " }));
        numerojugadores.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        canchadisponible.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        canchadisponible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                canchadisponibleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(52, 52, 52)
                                .addComponent(canchadisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 498, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(72, 72, 72)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(numerojugadores, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(textocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(busquedacliente)))))
                        .addGap(0, 94, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(42, 42, 42)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel43, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(timePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(43, 43, 43)
                        .addComponent(consultar)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(busquedacliente, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(textocliente, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(consultar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(timePicker2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel43)
                                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(22, 22, 22)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel44)
                            .addComponent(duracion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(numerojugadores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addComponent(canchadisponible, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        canchapanel.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 104, 980, 200));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel39.setText("SubTotal Q.");
        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel39.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        subtotal.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        subtotal.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        jLabel40.setText("Metodo de Pago");
        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        metodopago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { ".", "Efectivo", "Tarjeta de Credito", "Transferencia" }));
        metodopago.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        metodopago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metodopagoActionPerformed(evt);
            }
        });

        derecho.setBackground(new java.awt.Color(255, 255, 255));

        pago.add(adelanto);
        adelanto.setText("Anticipo 10%");
        adelanto.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        adelanto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adelantoActionPerformed(evt);
            }
        });

        pago.add(total);
        total.setText("Total");
        total.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        total.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                totalActionPerformed(evt);
            }
        });

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Pago");
        jLabel30.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        pago.add(parcial);
        parcial.setText("Parcial");
        parcial.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        parcial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parcialActionPerformed(evt);
            }
        });

        anticipo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        anticipo.setText("Anticipo ");
        anticipo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        pagoparcial.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pagoparcial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagoparcialActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout derechoLayout = new javax.swing.GroupLayout(derecho);
        derecho.setLayout(derechoLayout);
        derechoLayout.setHorizontalGroup(
            derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(derechoLayout.createSequentialGroup()
                .addGroup(derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(derechoLayout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(derechoLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(derechoLayout.createSequentialGroup()
                                .addComponent(adelanto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(parcial, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30))
                            .addGroup(derechoLayout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addComponent(anticipo, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pagoparcial, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        derechoLayout.setVerticalGroup(
            derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(derechoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(total)
                    .addComponent(parcial)
                    .addComponent(adelanto))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(derechoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(anticipo, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagoparcial, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(66, Short.MAX_VALUE))
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(derecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(derecho, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(metodopago, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        reservar.setText("Reservar");
        reservar.setFont(new java.awt.Font("Segoe UI", 2, 24)); // NOI18N
        reservar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reservarActionPerformed(evt);
            }
        });

        CONT.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        CONT.setLayout(new java.awt.GridLayout(1, 2));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CONT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(reservar, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(CONT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(reservar, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        canchapanel.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 530, 430));

        izquierdo.setBackground(new java.awt.Color(255, 255, 255));
        izquierdo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jbalon.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jbalon.setText("Balon");
        jbalon.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Extras");
        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jarbitro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jarbitro.setText("Arbitro");
        jarbitro.setBackground(new java.awt.Color(255, 255, 255));
        jarbitro.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jchaleco.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jchaleco.setText("Chalecos");
        jchaleco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jButton9.setText("Ir a tienda");
        jButton9.setBackground(new java.awt.Color(153, 204, 255));
        jButton9.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jLabel1.setText(" Total Q.");
        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        totalt.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalt.setBackground(new java.awt.Color(255, 255, 255));
        totalt.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        javax.swing.GroupLayout izquierdoLayout = new javax.swing.GroupLayout(izquierdo);
        izquierdo.setLayout(izquierdoLayout);
        izquierdoLayout.setHorizontalGroup(
            izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(izquierdoLayout.createSequentialGroup()
                .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(izquierdoLayout.createSequentialGroup()
                        .addGap(139, 139, 139)
                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(izquierdoLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jchaleco, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                            .addComponent(jarbitro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbalon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(balon, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                            .addComponent(arbitro)
                            .addComponent(chaleco)))
                    .addGroup(izquierdoLayout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(izquierdoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalt, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        izquierdoLayout.setVerticalGroup(
            izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, izquierdoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel29)
                .addGap(25, 25, 25)
                .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(balon, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbalon, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(arbitro)
                    .addComponent(jarbitro, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jchaleco)
                    .addComponent(chaleco, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addGroup(izquierdoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(totalt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        canchapanel.add(izquierdo, new org.netbeans.lib.awtextra.AbsoluteConstraints(564, 320, 420, 430));

        PanelContenido.addTab("reserva", canchapanel);

        Reservafinal.setBackground(new java.awt.Color(255, 255, 255));
        Reservafinal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Reservafinal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));
        jPanel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Buscar Reserva");
        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Ingrese Codigo de Reserva ");

        codigoReserva.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        codigoReserva.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jButton2.setText("Buscar");
        jButton2.setBackground(new java.awt.Color(153, 204, 255));
        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
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
        jButton3.setBackground(new java.awt.Color(153, 204, 255));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btcancelar.setText("Cancelar");
        btcancelar.setBackground(new java.awt.Color(153, 204, 255));
        btcancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcancelarActionPerformed(evt);
            }
        });

        pagar.setText("Pagar");
        pagar.setBackground(new java.awt.Color(153, 204, 255));
        pagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(114, 114, 114))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pagar, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btcancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(codigoReserva, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(14, Short.MAX_VALUE))
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(157, 157, 157)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codigoReserva, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btcancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        tienda.setText("Tienda");
        tienda.setBackground(new java.awt.Color(153, 204, 255));
        tienda.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tienda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tiendaActionPerformed(evt);
            }
        });

        jButton5.setText("Nueva Reserva");
        jButton5.setBackground(new java.awt.Color(153, 204, 255));
        jButton5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Control Reservas");
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(161, 161, 161)
                                .addComponent(tienda, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tienda, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16))
        );

        Reservafinal.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 570, 730));

        javax.swing.GroupLayout menureserva1Layout = new javax.swing.GroupLayout(menureserva1);
        menureserva1.setLayout(menureserva1Layout);
        menureserva1Layout.setHorizontalGroup(
            menureserva1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Reservafinal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)
        );
        menureserva1Layout.setVerticalGroup(
            menureserva1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Reservafinal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        PanelContenido.addTab("tab9", menureserva1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel62.setText("Gestion de Canchas");
        jLabel62.setBackground(new java.awt.Color(255, 255, 255));
        jLabel62.setFont(new java.awt.Font("Segoe UI", 3, 48)); // NOI18N

        panelListaCanchas.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelListaCanchasLayout = new javax.swing.GroupLayout(panelListaCanchas);
        panelListaCanchas.setLayout(panelListaCanchasLayout);
        panelListaCanchasLayout.setHorizontalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1598, Short.MAX_VALUE)
        );
        panelListaCanchasLayout.setVerticalGroup(
            panelListaCanchasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 572, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(panelListaCanchas);

        nuevachancha.setText("Crear Nueva Cancha");
        nuevachancha.setBackground(new java.awt.Color(204, 255, 255));
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE))
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
                .addContainerGap(77, Short.MAX_VALUE))
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

        panelclientes.setBackground(new java.awt.Color(255, 255, 255));

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel58.setText("Telefono");
        jLabel58.setFont(new java.awt.Font("Segoe UI", 3, 24)); // NOI18N

        bRegistrar.setText("Registrar");
        bRegistrar.setBackground(new java.awt.Color(204, 255, 255));
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
                .addContainerGap(267, Short.MAX_VALUE))
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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
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
        buscar.setBackground(new java.awt.Color(204, 255, 255));
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
        Modificar.setBackground(new java.awt.Color(204, 255, 255));
        Modificar.setFont(new java.awt.Font("Segoe UI", 3, 18)); // NOI18N
        Modificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ModificarActionPerformed(evt);
            }
        });

        eliminar.setText("Eliminar");
        eliminar.setBackground(new java.awt.Color(255, 102, 102));
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(258, 258, 258))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(63, 63, 63)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(258, 258, 258))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mtelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 665, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(225, 225, 225)
                        .addComponent(buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(busqueda, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mnombre, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mtelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(mcorreo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Modificar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(eliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(42, 42, 42))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(mcodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout panelclientesLayout = new javax.swing.GroupLayout(panelclientes);
        panelclientes.setLayout(panelclientesLayout);
        panelclientesLayout.setHorizontalGroup(
            panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelclientesLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panelclientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelclientesLayout.createSequentialGroup()
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(216, Short.MAX_VALUE))
                    .addGroup(panelclientesLayout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 691, Short.MAX_VALUE)
                        .addGap(67, 67, 67))))
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

        PanelPrincipal.add(PanelContenido, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 1010, 800));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void inicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inicioActionPerformed
        PanelContenido.setSelectedIndex(0);
        actualizarDashboard();
    }//GEN-LAST:event_inicioActionPerformed
    private void disponibleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disponibleActionPerformed
        PanelContenido.setSelectedIndex(2);
    }//GEN-LAST:event_disponibleActionPerformed
    private void canchaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_canchaActionPerformed
        PanelContenido.setSelectedIndex(3);
    }//GEN-LAST:event_canchaActionPerformed
    private void clientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clientesActionPerformed
        PanelContenido.setSelectedIndex(4);
    }//GEN-LAST:event_clientesActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
 
      boolean clientesGuardados = persistencia.guardarClientes();
    boolean canchasGuardadas = persistenciacancha.guardarCanchas();
    boolean reservasGuardadas = persistenciaReservas.guardarReservas();
    boolean pagosGuardados = persistenciaPagos.guardarPagos();

    boolean productosGuardados = persistenciaTienda.guardarProductos();
    boolean comprasGuardadas = persistenciaTienda.guardarCompras();

    if (clientesGuardados && canchasGuardadas
            && reservasGuardadas && pagosGuardados
            && productosGuardados && comprasGuardadas) {

        JOptionPane.showMessageDialog(
                this,
                "Todos los archivos CSV fueron guardados."
        );

    } else {
        JOptionPane.showMessageDialog(
                this,
                "Ocurrió un error al guardar uno o más archivos."
        );
    }

    }//GEN-LAST:event_guardarActionPerformed

    private void reportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportesActionPerformed
    Reportes ventana = new Reportes(
        persistencia,          // PersistenciaClientes
        persistenciacancha,    // PersistenciaCancha
        persistenciaReservas,
        persistenciaPagos,
        persistenciaTienda
);

ventana.setLocationRelativeTo(this);
ventana.setVisible(true);
    }//GEN-LAST:event_reportesActionPerformed

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
        if (!validarFechaYHora()) {
            return;
        }

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
     
        if (!validarFechaYHora()) {
            return;
        }
        
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

   // Pago
    int opcionesPago = 0;

    if (adelanto.isSelected()) opcionesPago++;
    if (total.isSelected()) opcionesPago++;
    if (parcial.isSelected()) opcionesPago++;

    if (opcionesPago > 1) {
        JOptionPane.showMessageDialog(
                this,
                "Selecciona solo una opción de pago, o ninguna para guardar una cotización."
        );
        return;
    }

    String metodo = "";

        if (opcionesPago == 1) {
            Object metodoSeleccionado = metodopago.getSelectedItem();

            if (metodoSeleccionado == null
                    || metodoSeleccionado.toString().trim().equals(".")
                    || metodoSeleccionado.toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un método de pago.");
                return;
            }

            metodo = metodoSeleccionado.toString().trim();
        }

    // Actualiza el total con la cancha y los extras actuales.
    actualizarPrecios();



    double montoPago = 0;
    double anticipoMinimo = Math.round(totalReserva * 0.10 * 100.0) / 100.0;

    if (opcionesPago == 1) {
        if (adelanto.isSelected()) {
            montoPago = anticipoMinimo;

        } else if (total.isSelected()) {
            montoPago = totalReserva;

        } else if (parcial.isSelected()) {
            try {
                montoPago = Double.parseDouble(
                        pagoparcial.getText().trim().replace(",", ".")
                );
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Escribe una cantidad parcial válida."
                );
                return;
            }

            if (montoPago <= anticipoMinimo || montoPago >= totalReserva) {
                JOptionPane.showMessageDialog(
                        this,
                        "El pago parcial debe ser mayor al anticipo mínimo y menor al total."
                );
                return;
            }
        }
    }
//seleciona Cancha
    
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
    
    int jugadores = Integer.parseInt(
        numerojugadores.getSelectedItem().toString()
                .replaceAll("[^0-9]", "")
);

// Comprueba otra vez que la cancha siga disponible al guardar.
Cancha[] disponiblesAhora = disponibilidad.buscarCanchasDisponibles(
        fecha,
        hora,
        horas,
        jugadores,
        -1
);

boolean disponibleAhora = false;

for (Cancha cancha : disponiblesAhora) {
    if (cancha == null) {
        break;
    }

    if (cancha.getCodigo().equalsIgnoreCase(
            canchaSelecionada.getCodigo())) {
        disponibleAhora = true;
        break;
    }
}

if (!disponibleAhora) {
    JOptionPane.showMessageDialog(this,"Cancha no disponible");
    return;
}

//crea Reserva

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

            if (montoPago > 0) {
            reserva.setEstado("CONFIRMADA");
        } else {
            reserva.setEstado("PENDIENTE");
        }
    reserva.setSubtotalCancha(subtotalCanchaActual);
    reserva.setTotalExtras(totalExtrasActual);
    reserva.setTotalReserva(totalReserva);
    
    
    
    
    
    
    if (!persistenciaReservas.agregarReserva(reserva)) {
        JOptionPane.showMessageDialog(this,"No se pudo guardar la reserva.");
        return;
    }
    
    if (montoPago > 0) {
        Pago pagoInicial = new Pago(
                "P" + System.currentTimeMillis(),
                idReserva,
                LocalDateTime.now(),
                montoPago,
                metodo,
                "USR001" // Usuario temporal hasta crear el login
        );

    if (!persistenciaPagos.agregarPago(pagoInicial)) {
        JOptionPane.showMessageDialog(
                this,
                "No hay espacio para agregar el pago."
        );
        return;
    }

    // El pago ya se agregó. Ahora vencemos las cotizaciones que se traslapan.
    int cotizacionesVencidas =
            persistenciaReservas.vencerCotizacionesEnConflicto(
                    canchaSelecionada.getCodigo(),
                    fecha,
                    hora,
                    horas
            );
}
    

   

   double saldoPendiente = totalReserva - montoPago;

   String mensaje =
        "Reserva: R" + String.format("%03d", reserva.getIdReserva()) + "\n"
        + "Cliente: " + reserva.getCliente().getNombre() + "\n"
        + "Cancha: " + reserva.getCancha().getNombre() + "\n"
        + "Fecha: " + reserva.getFecha() + "\n"
        + "Horario: " + reserva.getHoraInicio()
        + " - " + reserva.getHoraFinal() + "\n"
        + "Duración: " + reserva.getDuracionHoras() + " hora(s)\n"
        + "Balones: " + reserva.getBalon() + "\n"
        + "Árbitros: " + reserva.getArbitro() + "\n"
        + "Chalecos: " + reserva.getChalecos() + "\n"
        + "Estado: " + reserva.getEstado();

        if (montoPago > 0) {
            double pendiente = Math.max(0, totalReserva - montoPago);

            mensaje += "\n\nTotal: Q " + String.format("%.2f", totalReserva)
                    + "\nPagado: Q " + String.format("%.2f", montoPago)
                    + "\nPendiente: Q " + String.format("%.2f", saldoPendiente);
        } else {
            mensaje += "\n\nTotal estimado de la cotización: Q "
                    + String.format("%.2f", totalReserva)
                    + "\nCotización sin pago; todavía no está confirmada.";
        }
    JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Reserva realizada",
            JOptionPane.INFORMATION_MESSAGE
            
    );
limpiarFormularioReserva();
        
        
        
        
        
        
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

    private void tiendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tiendaActionPerformed

    if (reservaSeleccionada == null) {
        JOptionPane.showMessageDialog(this, "Primero busca una reserva.");
        return;
    }
    
    String estado = reservaSeleccionada.getEstado();

        if (estado.equalsIgnoreCase("CANCELADA")
                || estado.equalsIgnoreCase("VENCIDA")
                || estado.equalsIgnoreCase("FINALIZADA")) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se puede comprar en una reserva  " + estado + "."
            );
            return;
        }

    Tienda ventana = new Tienda();

    ventana.prepararTienda(
            "R" + String.format("%03d", reservaSeleccionada.getIdReserva()),
            reservaSeleccionada.getCliente().getNombre(),
            persistenciaTienda
    );

    ventana.setDefaultCloseOperation(
            javax.swing.WindowConstants.DISPOSE_ON_CLOSE
    );

    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            mostrarDetalleReserva();
        }
    });

    ventana.setLocationRelativeTo(this);
    ventana.setVisible(true);


    }//GEN-LAST:event_tiendaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
String codigo = codigoReserva.getText().trim().toUpperCase();

    if (!codigo.matches("R\\d{3,}")) {
        JOptionPane.showMessageDialog(this, "Escribe un código como R001.");
        return;
    }

    try {
        int id = Integer.parseInt(codigo.substring(1));
        String codigoCorrecto = "R" + String.format("%03d", id);

        if (!codigo.equals(codigoCorrecto)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato incorrecto. Ejemplo: R001."
            );
            return;
        }

        reservaSeleccionada = persistenciaReservas.buscarReserva(id);

        if (reservaSeleccionada == null) {
            datosReserva.setText("No se encontró la reserva " + codigo);
            return;
        }

        mostrarDetalleReserva();

    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(
                this,
                "El código debe tener formato R001."
        );
    }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btcancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcancelarActionPerformed

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
       
        String motivo = JOptionPane.showInputDialog(
        this,
        "Escribe el motivo de la cancelación:"
);

            if (motivo == null) {
                return; // Se canceló el cuadro de diálogo
            }

            motivo = motivo.trim();

            if (motivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes escribir un motivo.");
                return;
            }

            String responsable = "USR001"; // Temporal hasta crear los usuarios

            boolean cancelada = persistenciaReservas.cancelarReserva(
                    id,
                    motivo,
                    responsable
            );

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


    }//GEN-LAST:event_btcancelarActionPerformed

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

    private void pagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagarActionPerformed

   if (reservaSeleccionada == null) {
    JOptionPane.showMessageDialog(this, "Primero busca una reserva.");
    return;
}

String estado = reservaSeleccionada.getEstado();

if (estado.equalsIgnoreCase("CANCELADA")
        || estado.equalsIgnoreCase("VENCIDA")
        || estado.equalsIgnoreCase("FINALIZADA")) {
    JOptionPane.showMessageDialog(
            this,
            "No se puede pagar una reserva con estado " + estado + "."
    );
    return;
}

if (!estado.equalsIgnoreCase("PENDIENTE")
        && !estado.equalsIgnoreCase("CONFIRMADA")
        && !estado.equalsIgnoreCase("EN_CURSO")) {
    JOptionPane.showMessageDialog(
            this,
            "La reserva tiene un estado no válido para recibir pagos."
    );
    return;
}

// Las cotizaciones pendientes abren PagoG para cobrar el anticipo mínimo.
if (estado.equalsIgnoreCase("CONFIRMADA")
        || estado.equalsIgnoreCase("EN_CURSO")) {

    int idReserva = reservaSeleccionada.getIdReserva();
    String codigoReserva = "R" + String.format("%03d", idReserva);

    double saldoCancha = Math.max(
            0,
            reservaSeleccionada.getTotalReserva()
            - persistenciaPagos.getTotalPagadoReserva(idReserva)
    );

    double totalTienda =
            persistenciaTienda.calcularTotalComprasReserva(codigoReserva);

    double saldoTienda = Math.max(
            0,
            totalTienda - persistenciaPagos.getTotalPagadoTienda(idReserva)
    );

    if (saldoCancha + saldoTienda <= 0) {
        JOptionPane.showMessageDialog(
                this,
                "La cuenta de la reserva y la tienda ya está pagada."
        );
        return;
    }
  
    
}

PagoG ventana = new PagoG(
        reservaSeleccionada,
        persistenciaPagos,
        persistenciaReservas,
        disponibilidad,
        persistenciaTienda
);

ventana.setLocationRelativeTo(this);
ventana.setVisible(true);
    
    }//GEN-LAST:event_pagarActionPerformed

    private void tiendabtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tiendabtActionPerformed
      Tienda ventana = new Tienda();

    ventana.prepararTienda(
            "R" + String.format("%03d", reservaSeleccionada.getIdReserva()),
            reservaSeleccionada.getCliente().getNombre(),
            persistenciaTienda
    );

    ventana.setDefaultCloseOperation(
            javax.swing.WindowConstants.DISPOSE_ON_CLOSE
    );

    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
        @Override
        public void windowClosed(java.awt.event.WindowEvent e) {
            mostrarDetalleReserva();
        }
    });

    ventana.setLocationRelativeTo(this);
    ventana.setVisible(true);
    }//GEN-LAST:event_tiendabtActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CONT;
    private javax.swing.JButton Modificar;
    private javax.swing.JTabbedPane PanelContenido;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JPanel Reservafinal;
    private javax.swing.JCheckBox adelanto;
    private javax.swing.JLabel anticipo;
    private javax.swing.JSpinner arbitro;
    private javax.swing.JButton bRegistrar;
    private javax.swing.JSpinner balon;
    private javax.swing.JButton btcancelar;
    private javax.swing.JButton buscar;
    private javax.swing.JTextField busqueda;
    private javax.swing.JButton busquedacliente;
    private javax.swing.JButton cancha;
    private javax.swing.JComboBox<String> canchadisponible;
    private javax.swing.JPanel canchapanel;
    private javax.swing.JSpinner chaleco;
    private javax.swing.JButton clientes;
    private javax.swing.JLabel cobrar;
    private javax.swing.JTextField codigoReserva;
    private javax.swing.JButton consultar;
    private javax.swing.JLabel cotizaciones;
    private javax.swing.JTextArea datosReserva;
    private javax.swing.JTextArea datoscliente;
    private javax.swing.JPanel derecho;
    private javax.swing.JButton disponible;
    private javax.swing.JComboBox<String> duracion;
    private javax.swing.JButton eliminar;
    private javax.swing.JTextField email;
    private javax.swing.JButton guardar;
    private javax.swing.JButton inicio;
    private javax.swing.JPanel izquierdo;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton9;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JLabel jarbitro;
    private javax.swing.JLabel jbalon;
    private javax.swing.JLabel jchaleco;
    private javax.swing.JLabel lblEstado;
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
    private javax.swing.JButton pagar;
    private javax.swing.ButtonGroup pago;
    private javax.swing.JTextField pagoparcial;
    private javax.swing.JPanel pagospanel;
    private javax.swing.JPanel panelListaCanchas;
    private javax.swing.JPanel panelcancha;
    private javax.swing.JPanel panelclientes;
    private javax.swing.JPanel panelinicio;
    private javax.swing.JPanel panelmenu;
    private javax.swing.JCheckBox parcial;
    private javax.swing.JLabel recaudado;
    private javax.swing.JButton reportes;
    private javax.swing.JButton reservar;
    private javax.swing.JLabel reservashoy;
    private javax.swing.JLabel subtotal;
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTextField telefono;
    private javax.swing.JTextField textocliente;
    private javax.swing.JButton tienda;
    private javax.swing.JButton tiendabt;
    private com.github.lgooddatepicker.components.TimePicker timePicker2;
    private javax.swing.JCheckBox total;
    private javax.swing.JLabel totalt;
    // End of variables declaration//GEN-END:variables
public static void main(String args[]) {
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

        java.awt.EventQueue.invokeLater(() -> {
            new Grafica().setVisible(true);
        });
    }

}
