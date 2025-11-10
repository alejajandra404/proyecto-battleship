package views;

import controllers.ControlDisparo;
import dtos.*;
import enums.ResultadoDisparo;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import models.IObserver;
import static views.ConstantesVista.*;

/**
 * Vista principal del juego - Caso de Uso: Realizar Disparo
 * 
 * BAJO ACOPLAMIENTO - ALTA COHESIÓN
 * 
 * Esta vista SOLO conoce DTOs, NO conoce el modelo de dominio
 * Esto permite:
 * - Desacoplamiento total del modelo
 * - Facilidad para cambiar el modelo sin afectar la vista
 * - Preparación para comunicación en red
 * - Arquitectura limpia 
 * 
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaJuego extends javax.swing.JFrame implements IObserver, DragGestureListener {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaJuego.class.getName());
    
    // Componentes visuales
    private JPanel panelTableroPropio;
    private JPanel panelTableroDisparos;
    private JButton[][] botonesTableroPropio;
    private JButton[][] botonesTableroDisparos;
    private JLabel lblTurnoActual;
    private JLabel lblTemporizador;
    private JLabel lblNombreJugador;
    private JLabel lblNombreOponente;
    private JPanel panelMarcador;
    private JTextArea txtNotificaciones;
    
    // Controlador (único punto de comunicación)
    private ControlDisparo controlador;

    // Datos del jugador (solo primitivos y Strings, NO objetos del modelo)
    private String nombreJugadorActual;
    private String nombreJugadorOponente;

    /**
     * Constructor de la vista
     *
     * Solo recibe Strings y el controlador, NO objetos del modelo
     * @param nombreJugador
     * @param nombreOponente
     * @param controlador
     */
    public VistaJuego(String nombreJugador, String nombreOponente, ControlDisparo controlador) {
        this.nombreJugadorActual = nombreJugador;
        this.nombreJugadorOponente = nombreOponente;
        this.controlador = controlador;

        configurarVentana();
        inicializarComponentes();
        
        // Registrarse como observador
        controlador.getPartida().agregarObserver(this);
        
        // Inicializar el temporizador
        controlador.iniciarTemporizador();

        // Configurar observador para actualizaciones
        configurarObservador();
    }
    
    private void configurarVentana() {
        setTitle("Batalla Naval - " + nombreJugadorActual);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(COLOR_FONDO);
        setResizable(false);
    }

    private void inicializarComponentes() {
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelMarcador(), BorderLayout.EAST);
        add(crearPanelInferior(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 2, true),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel panelJugador = new JPanel(new GridLayout(2, 1, 5, 5));
        panelJugador.setBackground(COLOR_FONDO);

        lblNombreJugador = new JLabel("🎮 " + nombreJugadorActual);
        lblNombreJugador.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNombreJugador.setForeground(new Color(0, 100, 200));

        lblNombreOponente = new JLabel("VS: " + nombreJugadorOponente);
        lblNombreOponente.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNombreOponente.setForeground(new Color(150, 150, 150));

        panelJugador.add(lblNombreJugador);
        panelJugador.add(lblNombreOponente);

        lblTurnoActual = new JLabel("Esperando turno...", SwingConstants.CENTER);
        lblTurnoActual.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTurnoActual.setForeground(Color.DARK_GRAY);

        JPanel panelTemporizador = new JPanel(new BorderLayout());
        panelTemporizador.setBackground(COLOR_FONDO);

        JLabel lblTiempoLabel = new JLabel("⏱️ TIEMPO:", SwingConstants.RIGHT);
        lblTiempoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        lblTemporizador = new JLabel("30s", SwingConstants.CENTER);
        lblTemporizador.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTemporizador.setForeground(new Color(0, 150, 0));

        panelTemporizador.add(lblTiempoLabel, BorderLayout.NORTH);
        panelTemporizador.add(lblTemporizador, BorderLayout.CENTER);

        panel.add(panelJugador, BorderLayout.WEST);
        panel.add(lblTurnoActual, BorderLayout.CENTER);
        panel.add(panelTemporizador, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        panel.add(crearPanelTablero("TABLERO DE DISPAROS", true));
        panel.add(crearPanelTablero("MI TABLERO", false));

        return panel;
    }

    private JPanel crearPanelTablero(String titulo, boolean esTableroDisparos) {
        JPanel panelContenedor = new JPanel(new BorderLayout(0, 10));
        panelContenedor.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_BORDE);

        JPanel tablero = crearTablero(esTableroDisparos);
        tablero.setBorder(new LineBorder(COLOR_BORDE, 3, true));

        panelContenedor.add(lblTitulo, BorderLayout.NORTH);
        panelContenedor.add(tablero, BorderLayout.CENTER);

        return panelContenedor;
    }

    private JPanel crearTablero(boolean esTableroDisparos) {
        return new VistaTablero(this, esTableroDisparos);
    }
    
    private JPanel crearPanelMarcador() {
        JPanel panel = new VistaMarcador();
        panelMarcador = panel;
        return panel;
    }

    private JPanel crearPanelInferior() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDE, 2, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Panel de botones en la parte superior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setBackground(COLOR_FONDO);

        JButton btnAbandonar = new JButton("Abandonar Batalla");
        btnAbandonar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbandonar.setBackground(new Color(220, 53, 69));
        btnAbandonar.setForeground(Color.WHITE);
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAbandonar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbandonar.addActionListener(e -> botonAbandonarBatallaActionPerformed(e));

        panelBotones.add(btnAbandonar);

        // Panel de notificaciones
        JPanel panelNotificaciones = new JPanel(new BorderLayout());
        panelNotificaciones.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel("<html>&#128226; NOTIFICACIONES</html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_BORDE);

        txtNotificaciones = new JTextArea(4, 50);
        txtNotificaciones.setEditable(false);
        txtNotificaciones.setLineWrap(true);
        txtNotificaciones.setWrapStyleWord(true);
        txtNotificaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtNotificaciones.setBackground(new Color(250, 250, 250));

        JScrollPane scroll = new JScrollPane(txtNotificaciones);
        scroll.setBorder(new LineBorder(COLOR_AGUA, 1));

        panelNotificaciones.add(lblTitulo, BorderLayout.NORTH);
        panelNotificaciones.add(scroll, BorderLayout.CENTER);

        // Agregar ambos paneles
        panelPrincipal.add(panelBotones, BorderLayout.NORTH);
        panelPrincipal.add(panelNotificaciones, BorderLayout.CENTER);

        return panelPrincipal;
    }
    // MÉTODOS PÚBLICOS (usan DTOs) 
    /**
     * Muestra las naves en el tablero propio usando DTOs
     * 
     * @param naves
     */
    public void mostrarNavesDesdeDTO(NaveDTO[] naves) {
        if (naves == null) {
            return;
        }

        for (NaveDTO nave : naves) {
            CoordenadaDTO[] coordenadas = nave.getCoordenadas();
            for (CoordenadaDTO coord : coordenadas) {
                marcarNaveEnTableroPropio(coord.getX(), coord.getY());
            }
        }
    }

    /**
     * Actualiza el turno usando un TurnoDTO
     * 
     * @param turnoDTO
     */
    public void actualizarTurnoDesdeDTO(TurnoDTO turnoDTO) {
        SwingUtilities.invokeLater(() -> {
            boolean esMiTurno = turnoDTO.getNombreJugadorTurno().equals(nombreJugadorActual);

            if (esMiTurno) {
                lblTurnoActual.setText("¡TU TURNO!");
                lblTurnoActual.setForeground(new Color(0, 150, 0));
            } else {
                lblTurnoActual.setText("Turno de: " + turnoDTO.getNombreJugadorTurno());
                lblTurnoActual.setForeground(Color.RED);
            }

            habilitarTableroDisparos(esMiTurno);
            actualizarTemporizador(turnoDTO.getTiempoRestante());
        });
    }

    /**
     * Actualiza el marcador usando DTOs
     * 
     * @param navesOponente
     */
    public void actualizarMarcadorDesdeDTO(NaveDTO[] navesOponente) {
        // pendiente...
    }

    // MÉTODOS DE ACTUALIZACIÓN VISUAL 
    public void marcarNaveEnTableroPropio(int x, int y) {
        if (x >= 0 && x < TAMANO_TABLERO && y >= 0 && y < TAMANO_TABLERO) {
            botonesTableroPropio[x][y].setBackground(COLOR_NAVE);
            botonesTableroPropio[x][y].setBorder(new LineBorder(Color.BLACK, 2));
        }
    }

    public void habilitarTableroDisparos(boolean habilitar) {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                JButton btn = botonesTableroDisparos[i][j];
                if (btn.getBackground().equals(COLOR_VACIO) || btn.getBackground().equals(COLOR_AGUA)) {
                    btn.setEnabled(habilitar);
                }
            }
        }
    }

    public void marcarResultadoEnTableroDisparos(int x, int y, boolean esImpacto) {
        JButton btn = botonesTableroDisparos[x][y];
        if (esImpacto) {
            btn.setBackground(COLOR_IMPACTO);
            btn.setText("X");
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(COLOR_DISPARO_AGUA);
            btn.setText("•");
            btn.setForeground(Color.WHITE);
        }
        btn.setEnabled(false);
    }

    public void marcarImpactoEnTableroPropio(int x, int y) {
        JButton btn = botonesTableroPropio[x][y];
        btn.setBackground(COLOR_IMPACTO);
        btn.setText("X");
        btn.setForeground(Color.WHITE);
    }

    public void actualizarTemporizador(int segundos) {
        SwingUtilities.invokeLater(() -> {
            lblTemporizador.setText(segundos + "s");
            if (segundos <= 10) {
                lblTemporizador.setForeground(Color.RED);
            } else if (segundos <= 20) {
                lblTemporizador.setForeground(new Color(255, 165, 0));
            } else {
                lblTemporizador.setForeground(new Color(0, 150, 0));
            }
        });
    }

    public void agregarNotificacion(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtNotificaciones.append("• " + mensaje + "\n");
            txtNotificaciones.setCaretPosition(txtNotificaciones.getDocument().getLength());
        });
    }

    public void mostrarDialogo(String titulo, String mensaje, int tipo) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
        });
    }

    // CONFIGURACIÓN DE OBSERVADOR 
    private void configurarObservador() {
        // Timer para actualizar el temporizador desde el controlador
        Timer timer = new Timer(1000, e -> {
            int tiempoRestante = controlador.getTiempoRestante();
            actualizarTemporizador(tiempoRestante);
        });
        timer.start();
    }
    
    @Override
    public void notificar(String mensaje, String nombreJugadorTurno) {
        SwingUtilities.invokeLater(() -> {
            agregarNotificacion(mensaje);

            // Si el mensaje es de tiempo agotado
            if (mensaje.contains("Tiempo agotado")) {
                mostrarDialogo("Tiempo Agotado",
                        "El tiempo se ha agotado. Turno perdido.",
                        JOptionPane.WARNING_MESSAGE);
            }

            // Actualizar el turno en la interfaz
            TurnoDTO turnoDTO = controlador.obtenerTurnoActual();
            actualizarTurnoDesdeDTO(turnoDTO);
        });
    }

    public ControlDisparo getControlador() {return controlador;}
    
    public String getNombreJugadorActual() {return nombreJugadorActual;}
    
    public JButton[][] getBotonesTableroPropio() {return botonesTableroPropio;}

    public JButton[][] getBotonesTableroDisparos() {return botonesTableroDisparos;}
    
    public void setBotonesTableroPropio(JButton[][] botonesTableroPropio) {
        this.botonesTableroPropio = botonesTableroPropio;
    }

    public void setBotonesTableroDisparos(JButton[][] botonesTableroDisparos) {
        this.botonesTableroDisparos = botonesTableroDisparos;
    }
    
    @Override
    public void dragGestureRecognized(DragGestureEvent event) {

        var cursor = Cursor.getDefaultCursor();
        var panel = (JPanel) event.getComponent();

        var color = panel.getBackground();

        if (event.getDragAction() == DnDConstants.ACTION_COPY) {
            cursor = DragSource.DefaultCopyDrop;
        }

        event.startDrag(cursor, new TransferableColor(color));
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jButton23 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jButton30 = new javax.swing.JButton();
        jButton31 = new javax.swing.JButton();
        jButton32 = new javax.swing.JButton();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jButton36 = new javax.swing.JButton();
        jButton37 = new javax.swing.JButton();
        jButton38 = new javax.swing.JButton();
        jButton39 = new javax.swing.JButton();
        jButton40 = new javax.swing.JButton();
        jButton41 = new javax.swing.JButton();
        jButton42 = new javax.swing.JButton();
        jButton43 = new javax.swing.JButton();
        jButton44 = new javax.swing.JButton();
        jButton45 = new javax.swing.JButton();
        jButton46 = new javax.swing.JButton();
        jButton47 = new javax.swing.JButton();
        jButton48 = new javax.swing.JButton();
        jButton49 = new javax.swing.JButton();
        jButton50 = new javax.swing.JButton();
        jButton51 = new javax.swing.JButton();
        jButton52 = new javax.swing.JButton();
        jButton53 = new javax.swing.JButton();
        jButton54 = new javax.swing.JButton();
        jButton55 = new javax.swing.JButton();
        jButton56 = new javax.swing.JButton();
        jButton57 = new javax.swing.JButton();
        jButton58 = new javax.swing.JButton();
        jButton59 = new javax.swing.JButton();
        jButton60 = new javax.swing.JButton();
        jButton61 = new javax.swing.JButton();
        jButton62 = new javax.swing.JButton();
        jButton63 = new javax.swing.JButton();
        jButton64 = new javax.swing.JButton();
        jButton65 = new javax.swing.JButton();
        jButton66 = new javax.swing.JButton();
        jButton67 = new javax.swing.JButton();
        jButton68 = new javax.swing.JButton();
        jButton69 = new javax.swing.JButton();
        jButton70 = new javax.swing.JButton();
        jButton71 = new javax.swing.JButton();
        jButton72 = new javax.swing.JButton();
        jButton73 = new javax.swing.JButton();
        jButton74 = new javax.swing.JButton();
        jButton75 = new javax.swing.JButton();
        jButton76 = new javax.swing.JButton();
        jButton77 = new javax.swing.JButton();
        jButton78 = new javax.swing.JButton();
        jButton79 = new javax.swing.JButton();
        jButton80 = new javax.swing.JButton();
        jButton81 = new javax.swing.JButton();
        jButton82 = new javax.swing.JButton();
        jButton83 = new javax.swing.JButton();
        jButton84 = new javax.swing.JButton();
        jButton85 = new javax.swing.JButton();
        jButton86 = new javax.swing.JButton();
        jButton87 = new javax.swing.JButton();
        jButton88 = new javax.swing.JButton();
        jButton89 = new javax.swing.JButton();
        jButton90 = new javax.swing.JButton();
        jButton91 = new javax.swing.JButton();
        jButton92 = new javax.swing.JButton();
        jButton93 = new javax.swing.JButton();
        jButton94 = new javax.swing.JButton();
        jButton95 = new javax.swing.JButton();
        jButton96 = new javax.swing.JButton();
        jButton97 = new javax.swing.JButton();
        jButton98 = new javax.swing.JButton();
        jButton99 = new javax.swing.JButton();
        jButton100 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton101 = new javax.swing.JButton();
        jButton102 = new javax.swing.JButton();
        jButton103 = new javax.swing.JButton();
        jButton104 = new javax.swing.JButton();
        jButton105 = new javax.swing.JButton();
        jButton106 = new javax.swing.JButton();
        jButton107 = new javax.swing.JButton();
        jButton108 = new javax.swing.JButton();
        jButton109 = new javax.swing.JButton();
        jButton110 = new javax.swing.JButton();
        jButton111 = new javax.swing.JButton();
        jButton112 = new javax.swing.JButton();
        jButton113 = new javax.swing.JButton();
        jButton114 = new javax.swing.JButton();
        jButton115 = new javax.swing.JButton();
        jButton116 = new javax.swing.JButton();
        jButton117 = new javax.swing.JButton();
        jButton118 = new javax.swing.JButton();
        jButton119 = new javax.swing.JButton();
        jButton120 = new javax.swing.JButton();
        jButton121 = new javax.swing.JButton();
        jButton122 = new javax.swing.JButton();
        jButton123 = new javax.swing.JButton();
        jButton124 = new javax.swing.JButton();
        jButton125 = new javax.swing.JButton();
        jButton126 = new javax.swing.JButton();
        jButton127 = new javax.swing.JButton();
        jButton128 = new javax.swing.JButton();
        jButton129 = new javax.swing.JButton();
        jButton130 = new javax.swing.JButton();
        jButton131 = new javax.swing.JButton();
        jButton132 = new javax.swing.JButton();
        jButton133 = new javax.swing.JButton();
        jButton134 = new javax.swing.JButton();
        jButton135 = new javax.swing.JButton();
        jButton136 = new javax.swing.JButton();
        jButton137 = new javax.swing.JButton();
        jButton138 = new javax.swing.JButton();
        jButton139 = new javax.swing.JButton();
        jButton140 = new javax.swing.JButton();
        jButton141 = new javax.swing.JButton();
        jButton142 = new javax.swing.JButton();
        jButton143 = new javax.swing.JButton();
        jButton144 = new javax.swing.JButton();
        jButton145 = new javax.swing.JButton();
        jButton146 = new javax.swing.JButton();
        jButton147 = new javax.swing.JButton();
        jButton148 = new javax.swing.JButton();
        jButton149 = new javax.swing.JButton();
        jButton150 = new javax.swing.JButton();
        jButton151 = new javax.swing.JButton();
        jButton152 = new javax.swing.JButton();
        jButton153 = new javax.swing.JButton();
        jButton154 = new javax.swing.JButton();
        jButton155 = new javax.swing.JButton();
        jButton156 = new javax.swing.JButton();
        jButton157 = new javax.swing.JButton();
        jButton158 = new javax.swing.JButton();
        jButton159 = new javax.swing.JButton();
        jButton160 = new javax.swing.JButton();
        jButton161 = new javax.swing.JButton();
        jButton162 = new javax.swing.JButton();
        jButton163 = new javax.swing.JButton();
        jButton164 = new javax.swing.JButton();
        jButton165 = new javax.swing.JButton();
        jButton166 = new javax.swing.JButton();
        jButton167 = new javax.swing.JButton();
        jButton168 = new javax.swing.JButton();
        jButton169 = new javax.swing.JButton();
        jButton170 = new javax.swing.JButton();
        jButton171 = new javax.swing.JButton();
        jButton172 = new javax.swing.JButton();
        jButton173 = new javax.swing.JButton();
        jButton174 = new javax.swing.JButton();
        jButton175 = new javax.swing.JButton();
        jButton176 = new javax.swing.JButton();
        jButton177 = new javax.swing.JButton();
        jButton178 = new javax.swing.JButton();
        jButton179 = new javax.swing.JButton();
        jButton180 = new javax.swing.JButton();
        jButton181 = new javax.swing.JButton();
        jButton182 = new javax.swing.JButton();
        jButton183 = new javax.swing.JButton();
        jButton184 = new javax.swing.JButton();
        jButton185 = new javax.swing.JButton();
        jButton186 = new javax.swing.JButton();
        jButton187 = new javax.swing.JButton();
        jButton188 = new javax.swing.JButton();
        jButton189 = new javax.swing.JButton();
        jButton190 = new javax.swing.JButton();
        jButton191 = new javax.swing.JButton();
        jButton192 = new javax.swing.JButton();
        jButton193 = new javax.swing.JButton();
        jButton194 = new javax.swing.JButton();
        jButton195 = new javax.swing.JButton();
        jButton196 = new javax.swing.JButton();
        jButton197 = new javax.swing.JButton();
        jButton198 = new javax.swing.JButton();
        jButton199 = new javax.swing.JButton();
        jButton200 = new javax.swing.JButton();
        botonAtacar = new javax.swing.JButton();
        botonAbandonarBatalla = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 153, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel2.setLayout(new java.awt.GridLayout(10, 10));

        jButton1.setText("jButton1");
        jPanel2.add(jButton1);

        jButton2.setText("jButton1");
        jPanel2.add(jButton2);

        jButton3.setText("jButton1");
        jPanel2.add(jButton3);

        jButton4.setText("jButton1");
        jPanel2.add(jButton4);

        jButton5.setText("jButton1");
        jPanel2.add(jButton5);

        jButton6.setText("jButton1");
        jPanel2.add(jButton6);

        jButton7.setText("jButton1");
        jPanel2.add(jButton7);

        jButton8.setText("jButton1");
        jPanel2.add(jButton8);

        jButton9.setText("jButton1");
        jPanel2.add(jButton9);

        jButton10.setText("jButton1");
        jPanel2.add(jButton10);

        jButton11.setText("jButton1");
        jPanel2.add(jButton11);

        jButton12.setText("jButton1");
        jPanel2.add(jButton12);

        jButton13.setText("jButton1");
        jPanel2.add(jButton13);

        jButton14.setText("jButton1");
        jPanel2.add(jButton14);

        jButton15.setText("jButton1");
        jPanel2.add(jButton15);

        jButton16.setText("jButton1");
        jPanel2.add(jButton16);

        jButton17.setText("jButton1");
        jPanel2.add(jButton17);

        jButton18.setText("jButton1");
        jPanel2.add(jButton18);

        jButton19.setText("jButton1");
        jPanel2.add(jButton19);

        jButton20.setText("jButton1");
        jPanel2.add(jButton20);

        jButton21.setText("jButton1");
        jPanel2.add(jButton21);

        jButton22.setText("jButton1");
        jPanel2.add(jButton22);

        jButton23.setText("jButton1");
        jPanel2.add(jButton23);

        jButton24.setText("jButton1");
        jPanel2.add(jButton24);

        jButton25.setText("jButton1");
        jPanel2.add(jButton25);

        jButton26.setText("jButton1");
        jPanel2.add(jButton26);

        jButton27.setText("jButton1");
        jPanel2.add(jButton27);

        jButton28.setText("jButton1");
        jPanel2.add(jButton28);

        jButton29.setText("jButton1");
        jPanel2.add(jButton29);

        jButton30.setText("jButton1");
        jPanel2.add(jButton30);

        jButton31.setText("jButton1");
        jPanel2.add(jButton31);

        jButton32.setText("jButton1");
        jPanel2.add(jButton32);

        jButton33.setText("jButton1");
        jPanel2.add(jButton33);

        jButton34.setText("jButton1");
        jPanel2.add(jButton34);

        jButton35.setText("jButton1");
        jPanel2.add(jButton35);

        jButton36.setText("jButton1");
        jPanel2.add(jButton36);

        jButton37.setText("jButton1");
        jPanel2.add(jButton37);

        jButton38.setText("jButton1");
        jPanel2.add(jButton38);

        jButton39.setText("jButton1");
        jPanel2.add(jButton39);

        jButton40.setText("jButton1");
        jPanel2.add(jButton40);

        jButton41.setText("jButton1");
        jPanel2.add(jButton41);

        jButton42.setText("jButton1");
        jPanel2.add(jButton42);

        jButton43.setText("jButton1");
        jPanel2.add(jButton43);

        jButton44.setText("jButton1");
        jPanel2.add(jButton44);

        jButton45.setText("jButton1");
        jPanel2.add(jButton45);

        jButton46.setText("jButton1");
        jPanel2.add(jButton46);

        jButton47.setText("jButton1");
        jPanel2.add(jButton47);

        jButton48.setText("jButton1");
        jPanel2.add(jButton48);

        jButton49.setText("jButton1");
        jPanel2.add(jButton49);

        jButton50.setText("jButton1");
        jPanel2.add(jButton50);

        jButton51.setText("jButton1");
        jPanel2.add(jButton51);

        jButton52.setText("jButton1");
        jPanel2.add(jButton52);

        jButton53.setText("jButton1");
        jPanel2.add(jButton53);

        jButton54.setText("jButton1");
        jPanel2.add(jButton54);

        jButton55.setText("jButton1");
        jPanel2.add(jButton55);

        jButton56.setText("jButton1");
        jPanel2.add(jButton56);

        jButton57.setText("jButton1");
        jPanel2.add(jButton57);

        jButton58.setText("jButton1");
        jPanel2.add(jButton58);

        jButton59.setText("jButton1");
        jPanel2.add(jButton59);

        jButton60.setText("jButton1");
        jPanel2.add(jButton60);

        jButton61.setText("jButton1");
        jPanel2.add(jButton61);

        jButton62.setText("jButton1");
        jPanel2.add(jButton62);

        jButton63.setText("jButton1");
        jPanel2.add(jButton63);

        jButton64.setText("jButton1");
        jPanel2.add(jButton64);

        jButton65.setText("jButton1");
        jPanel2.add(jButton65);

        jButton66.setText("jButton1");
        jPanel2.add(jButton66);

        jButton67.setText("jButton1");
        jPanel2.add(jButton67);

        jButton68.setText("jButton1");
        jPanel2.add(jButton68);

        jButton69.setText("jButton1");
        jPanel2.add(jButton69);

        jButton70.setText("jButton1");
        jPanel2.add(jButton70);

        jButton71.setText("jButton1");
        jPanel2.add(jButton71);

        jButton72.setText("jButton1");
        jPanel2.add(jButton72);

        jButton73.setText("jButton1");
        jPanel2.add(jButton73);

        jButton74.setText("jButton1");
        jPanel2.add(jButton74);

        jButton75.setText("jButton1");
        jPanel2.add(jButton75);

        jButton76.setText("jButton1");
        jPanel2.add(jButton76);

        jButton77.setText("jButton1");
        jPanel2.add(jButton77);

        jButton78.setText("jButton1");
        jPanel2.add(jButton78);

        jButton79.setText("jButton1");
        jPanel2.add(jButton79);

        jButton80.setText("jButton1");
        jPanel2.add(jButton80);

        jButton81.setText("jButton1");
        jPanel2.add(jButton81);

        jButton82.setText("jButton1");
        jPanel2.add(jButton82);

        jButton83.setText("jButton1");
        jPanel2.add(jButton83);

        jButton84.setText("jButton1");
        jPanel2.add(jButton84);

        jButton85.setText("jButton1");
        jPanel2.add(jButton85);

        jButton86.setText("jButton1");
        jPanel2.add(jButton86);

        jButton87.setText("jButton1");
        jPanel2.add(jButton87);

        jButton88.setText("jButton1");
        jPanel2.add(jButton88);

        jButton89.setText("jButton1");
        jPanel2.add(jButton89);

        jButton90.setText("jButton1");
        jPanel2.add(jButton90);

        jButton91.setText("jButton1");
        jPanel2.add(jButton91);

        jButton92.setText("jButton1");
        jPanel2.add(jButton92);

        jButton93.setText("jButton1");
        jPanel2.add(jButton93);

        jButton94.setText("jButton1");
        jPanel2.add(jButton94);

        jButton95.setText("jButton1");
        jPanel2.add(jButton95);

        jButton96.setText("jButton1");
        jPanel2.add(jButton96);

        jButton97.setText("jButton1");
        jPanel2.add(jButton97);

        jButton98.setText("jButton1");
        jPanel2.add(jButton98);

        jButton99.setText("jButton1");
        jPanel2.add(jButton99);

        jButton100.setText("jButton1");
        jPanel2.add(jButton100);

        jPanel3.setBackground(new java.awt.Color(0, 153, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(500, 500));
        jPanel3.setLayout(new java.awt.GridLayout(10, 10));

        jButton101.setText("jButton101");
        jPanel3.add(jButton101);

        jButton102.setText("jButton101");
        jPanel3.add(jButton102);

        jButton103.setText("jButton101");
        jPanel3.add(jButton103);

        jButton104.setText("jButton101");
        jPanel3.add(jButton104);

        jButton105.setText("jButton101");
        jPanel3.add(jButton105);

        jButton106.setText("jButton101");
        jPanel3.add(jButton106);

        jButton107.setText("jButton101");
        jPanel3.add(jButton107);

        jButton108.setText("jButton101");
        jPanel3.add(jButton108);

        jButton109.setText("jButton101");
        jPanel3.add(jButton109);

        jButton110.setText("jButton101");
        jPanel3.add(jButton110);

        jButton111.setText("jButton101");
        jPanel3.add(jButton111);

        jButton112.setText("jButton101");
        jPanel3.add(jButton112);

        jButton113.setText("jButton101");
        jPanel3.add(jButton113);

        jButton114.setText("jButton101");
        jPanel3.add(jButton114);

        jButton115.setText("jButton101");
        jPanel3.add(jButton115);

        jButton116.setText("jButton101");
        jPanel3.add(jButton116);

        jButton117.setText("jButton101");
        jPanel3.add(jButton117);

        jButton118.setText("jButton101");
        jPanel3.add(jButton118);

        jButton119.setText("jButton101");
        jPanel3.add(jButton119);

        jButton120.setText("jButton101");
        jPanel3.add(jButton120);

        jButton121.setText("jButton101");
        jPanel3.add(jButton121);

        jButton122.setText("jButton101");
        jPanel3.add(jButton122);

        jButton123.setText("jButton101");
        jPanel3.add(jButton123);

        jButton124.setText("jButton101");
        jPanel3.add(jButton124);

        jButton125.setText("jButton101");
        jPanel3.add(jButton125);

        jButton126.setText("jButton101");
        jPanel3.add(jButton126);

        jButton127.setText("jButton101");
        jPanel3.add(jButton127);

        jButton128.setText("jButton101");
        jPanel3.add(jButton128);

        jButton129.setText("jButton101");
        jPanel3.add(jButton129);

        jButton130.setText("jButton101");
        jPanel3.add(jButton130);

        jButton131.setText("jButton101");
        jPanel3.add(jButton131);

        jButton132.setText("jButton101");
        jPanel3.add(jButton132);

        jButton133.setText("jButton101");
        jPanel3.add(jButton133);

        jButton134.setText("jButton101");
        jPanel3.add(jButton134);

        jButton135.setText("jButton101");
        jPanel3.add(jButton135);

        jButton136.setText("jButton101");
        jPanel3.add(jButton136);

        jButton137.setText("jButton101");
        jPanel3.add(jButton137);

        jButton138.setText("jButton101");
        jPanel3.add(jButton138);

        jButton139.setText("jButton101");
        jPanel3.add(jButton139);

        jButton140.setText("jButton101");
        jPanel3.add(jButton140);

        jButton141.setText("jButton101");
        jPanel3.add(jButton141);

        jButton142.setText("jButton101");
        jPanel3.add(jButton142);

        jButton143.setText("jButton101");
        jPanel3.add(jButton143);

        jButton144.setText("jButton101");
        jPanel3.add(jButton144);

        jButton145.setText("jButton101");
        jPanel3.add(jButton145);

        jButton146.setText("jButton101");
        jPanel3.add(jButton146);

        jButton147.setText("jButton101");
        jPanel3.add(jButton147);

        jButton148.setText("jButton101");
        jPanel3.add(jButton148);

        jButton149.setText("jButton101");
        jPanel3.add(jButton149);

        jButton150.setText("jButton101");
        jPanel3.add(jButton150);

        jButton151.setText("jButton101");
        jPanel3.add(jButton151);

        jButton152.setText("jButton101");
        jPanel3.add(jButton152);

        jButton153.setText("jButton101");
        jPanel3.add(jButton153);

        jButton154.setText("jButton101");
        jPanel3.add(jButton154);

        jButton155.setText("jButton101");
        jPanel3.add(jButton155);

        jButton156.setText("jButton101");
        jPanel3.add(jButton156);

        jButton157.setText("jButton101");
        jPanel3.add(jButton157);

        jButton158.setText("jButton101");
        jPanel3.add(jButton158);

        jButton159.setText("jButton101");
        jPanel3.add(jButton159);

        jButton160.setText("jButton101");
        jPanel3.add(jButton160);

        jButton161.setText("jButton101");
        jPanel3.add(jButton161);

        jButton162.setText("jButton101");
        jPanel3.add(jButton162);

        jButton163.setText("jButton101");
        jPanel3.add(jButton163);

        jButton164.setText("jButton101");
        jPanel3.add(jButton164);

        jButton165.setText("jButton101");
        jPanel3.add(jButton165);

        jButton166.setText("jButton101");
        jPanel3.add(jButton166);

        jButton167.setText("jButton101");
        jPanel3.add(jButton167);

        jButton168.setText("jButton101");
        jPanel3.add(jButton168);

        jButton169.setText("jButton101");
        jPanel3.add(jButton169);

        jButton170.setText("jButton101");
        jPanel3.add(jButton170);

        jButton171.setText("jButton101");
        jPanel3.add(jButton171);

        jButton172.setText("jButton101");
        jPanel3.add(jButton172);

        jButton173.setText("jButton101");
        jPanel3.add(jButton173);

        jButton174.setText("jButton101");
        jPanel3.add(jButton174);

        jButton175.setText("jButton101");
        jPanel3.add(jButton175);

        jButton176.setText("jButton101");
        jPanel3.add(jButton176);

        jButton177.setText("jButton101");
        jPanel3.add(jButton177);

        jButton178.setText("jButton101");
        jPanel3.add(jButton178);

        jButton179.setText("jButton101");
        jPanel3.add(jButton179);

        jButton180.setText("jButton101");
        jPanel3.add(jButton180);

        jButton181.setText("jButton101");
        jPanel3.add(jButton181);

        jButton182.setText("jButton101");
        jPanel3.add(jButton182);

        jButton183.setText("jButton101");
        jPanel3.add(jButton183);

        jButton184.setText("jButton101");
        jPanel3.add(jButton184);

        jButton185.setText("jButton101");
        jPanel3.add(jButton185);

        jButton186.setText("jButton101");
        jPanel3.add(jButton186);

        jButton187.setText("jButton101");
        jPanel3.add(jButton187);

        jButton188.setText("jButton101");
        jPanel3.add(jButton188);

        jButton189.setText("jButton101");
        jPanel3.add(jButton189);

        jButton190.setText("jButton101");
        jPanel3.add(jButton190);

        jButton191.setText("jButton101");
        jPanel3.add(jButton191);

        jButton192.setText("jButton101");
        jPanel3.add(jButton192);

        jButton193.setText("jButton101");
        jPanel3.add(jButton193);

        jButton194.setText("jButton101");
        jPanel3.add(jButton194);

        jButton195.setText("jButton101");
        jPanel3.add(jButton195);

        jButton196.setText("jButton101");
        jPanel3.add(jButton196);

        jButton197.setText("jButton101");
        jPanel3.add(jButton197);

        jButton198.setText("jButton101");
        jPanel3.add(jButton198);

        jButton199.setText("jButton101");
        jPanel3.add(jButton199);

        jButton200.setText("jButton101");
        jPanel3.add(jButton200);

        botonAtacar.setText("Atacar");
        botonAtacar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAtacarActionPerformed(evt);
            }
        });

        botonAbandonarBatalla.setText("Abandonar Batalla");
        botonAbandonarBatalla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonAbandonarBatallaActionPerformed(evt);
            }
        });

        jLabel1.setText("Temporizador aqui");

        jLabel2.setText("Tu");

        jLabel3.setText("Jugador oponente");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(botonAtacar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addGap(341, 341, 341)
                        .addComponent(botonAbandonarBatalla, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(62, 62, 62))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(260, 260, 260)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(307, 307, 307))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonAtacar, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonAbandonarBatalla, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonAbandonarBatallaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAbandonarBatallaActionPerformed
        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Estás seguro de que deseas abandonar la partida?",
                "Confirmar Abandono",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            agregarNotificacion("🏳️ " + nombreJugadorActual + " ha abandonado la partida");
            agregarNotificacion("🏆 " + nombreJugadorOponente + " es el ganador");

            JOptionPane.showMessageDialog(
                    this,
                    nombreJugadorOponente + " gana por abandono de " + nombreJugadorActual,
                    "Partida Finalizada",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Detener el temporizador
            controlador.pausarTemporizador();

            // Cerrar la aplicación 
            System.exit(0);
        }
    }//GEN-LAST:event_botonAbandonarBatallaActionPerformed

    private void botonAtacarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonAtacarActionPerformed
        JOptionPane.showMessageDialog(rootPane, "SaS");
    }//GEN-LAST:event_botonAtacarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonAbandonarBatalla;
    private javax.swing.JButton botonAtacar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton100;
    private javax.swing.JButton jButton101;
    private javax.swing.JButton jButton102;
    private javax.swing.JButton jButton103;
    private javax.swing.JButton jButton104;
    private javax.swing.JButton jButton105;
    private javax.swing.JButton jButton106;
    private javax.swing.JButton jButton107;
    private javax.swing.JButton jButton108;
    private javax.swing.JButton jButton109;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton110;
    private javax.swing.JButton jButton111;
    private javax.swing.JButton jButton112;
    private javax.swing.JButton jButton113;
    private javax.swing.JButton jButton114;
    private javax.swing.JButton jButton115;
    private javax.swing.JButton jButton116;
    private javax.swing.JButton jButton117;
    private javax.swing.JButton jButton118;
    private javax.swing.JButton jButton119;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton120;
    private javax.swing.JButton jButton121;
    private javax.swing.JButton jButton122;
    private javax.swing.JButton jButton123;
    private javax.swing.JButton jButton124;
    private javax.swing.JButton jButton125;
    private javax.swing.JButton jButton126;
    private javax.swing.JButton jButton127;
    private javax.swing.JButton jButton128;
    private javax.swing.JButton jButton129;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton130;
    private javax.swing.JButton jButton131;
    private javax.swing.JButton jButton132;
    private javax.swing.JButton jButton133;
    private javax.swing.JButton jButton134;
    private javax.swing.JButton jButton135;
    private javax.swing.JButton jButton136;
    private javax.swing.JButton jButton137;
    private javax.swing.JButton jButton138;
    private javax.swing.JButton jButton139;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton140;
    private javax.swing.JButton jButton141;
    private javax.swing.JButton jButton142;
    private javax.swing.JButton jButton143;
    private javax.swing.JButton jButton144;
    private javax.swing.JButton jButton145;
    private javax.swing.JButton jButton146;
    private javax.swing.JButton jButton147;
    private javax.swing.JButton jButton148;
    private javax.swing.JButton jButton149;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton150;
    private javax.swing.JButton jButton151;
    private javax.swing.JButton jButton152;
    private javax.swing.JButton jButton153;
    private javax.swing.JButton jButton154;
    private javax.swing.JButton jButton155;
    private javax.swing.JButton jButton156;
    private javax.swing.JButton jButton157;
    private javax.swing.JButton jButton158;
    private javax.swing.JButton jButton159;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton160;
    private javax.swing.JButton jButton161;
    private javax.swing.JButton jButton162;
    private javax.swing.JButton jButton163;
    private javax.swing.JButton jButton164;
    private javax.swing.JButton jButton165;
    private javax.swing.JButton jButton166;
    private javax.swing.JButton jButton167;
    private javax.swing.JButton jButton168;
    private javax.swing.JButton jButton169;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton170;
    private javax.swing.JButton jButton171;
    private javax.swing.JButton jButton172;
    private javax.swing.JButton jButton173;
    private javax.swing.JButton jButton174;
    private javax.swing.JButton jButton175;
    private javax.swing.JButton jButton176;
    private javax.swing.JButton jButton177;
    private javax.swing.JButton jButton178;
    private javax.swing.JButton jButton179;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton180;
    private javax.swing.JButton jButton181;
    private javax.swing.JButton jButton182;
    private javax.swing.JButton jButton183;
    private javax.swing.JButton jButton184;
    private javax.swing.JButton jButton185;
    private javax.swing.JButton jButton186;
    private javax.swing.JButton jButton187;
    private javax.swing.JButton jButton188;
    private javax.swing.JButton jButton189;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton190;
    private javax.swing.JButton jButton191;
    private javax.swing.JButton jButton192;
    private javax.swing.JButton jButton193;
    private javax.swing.JButton jButton194;
    private javax.swing.JButton jButton195;
    private javax.swing.JButton jButton196;
    private javax.swing.JButton jButton197;
    private javax.swing.JButton jButton198;
    private javax.swing.JButton jButton199;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton200;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton29;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton30;
    private javax.swing.JButton jButton31;
    private javax.swing.JButton jButton32;
    private javax.swing.JButton jButton33;
    private javax.swing.JButton jButton34;
    private javax.swing.JButton jButton35;
    private javax.swing.JButton jButton36;
    private javax.swing.JButton jButton37;
    private javax.swing.JButton jButton38;
    private javax.swing.JButton jButton39;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton40;
    private javax.swing.JButton jButton41;
    private javax.swing.JButton jButton42;
    private javax.swing.JButton jButton43;
    private javax.swing.JButton jButton44;
    private javax.swing.JButton jButton45;
    private javax.swing.JButton jButton46;
    private javax.swing.JButton jButton47;
    private javax.swing.JButton jButton48;
    private javax.swing.JButton jButton49;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton50;
    private javax.swing.JButton jButton51;
    private javax.swing.JButton jButton52;
    private javax.swing.JButton jButton53;
    private javax.swing.JButton jButton54;
    private javax.swing.JButton jButton55;
    private javax.swing.JButton jButton56;
    private javax.swing.JButton jButton57;
    private javax.swing.JButton jButton58;
    private javax.swing.JButton jButton59;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton60;
    private javax.swing.JButton jButton61;
    private javax.swing.JButton jButton62;
    private javax.swing.JButton jButton63;
    private javax.swing.JButton jButton64;
    private javax.swing.JButton jButton65;
    private javax.swing.JButton jButton66;
    private javax.swing.JButton jButton67;
    private javax.swing.JButton jButton68;
    private javax.swing.JButton jButton69;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton70;
    private javax.swing.JButton jButton71;
    private javax.swing.JButton jButton72;
    private javax.swing.JButton jButton73;
    private javax.swing.JButton jButton74;
    private javax.swing.JButton jButton75;
    private javax.swing.JButton jButton76;
    private javax.swing.JButton jButton77;
    private javax.swing.JButton jButton78;
    private javax.swing.JButton jButton79;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton80;
    private javax.swing.JButton jButton81;
    private javax.swing.JButton jButton82;
    private javax.swing.JButton jButton83;
    private javax.swing.JButton jButton84;
    private javax.swing.JButton jButton85;
    private javax.swing.JButton jButton86;
    private javax.swing.JButton jButton87;
    private javax.swing.JButton jButton88;
    private javax.swing.JButton jButton89;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButton90;
    private javax.swing.JButton jButton91;
    private javax.swing.JButton jButton92;
    private javax.swing.JButton jButton93;
    private javax.swing.JButton jButton94;
    private javax.swing.JButton jButton95;
    private javax.swing.JButton jButton96;
    private javax.swing.JButton jButton97;
    private javax.swing.JButton jButton98;
    private javax.swing.JButton jButton99;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

}