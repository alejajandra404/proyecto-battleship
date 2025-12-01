package views;

import controllers.ControlDisparo;
import dtos.CoordenadaDTO;
import dtos.NaveDTO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import models.IObserver;
import static views.ConstantesVista.COLOR_AGUA;
import static views.ConstantesVista.COLOR_BORDE;
import static views.ConstantesVista.COLOR_FONDO;

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
public class VistaConfiguracionNaves extends javax.swing.JPanel implements IObserver {
        
    // Componentes visuales
    private VistaTableroNaves panelTableroPropio;
    private VistaTableroDisparos panelTableroDisparos;
    private JLabel lblNombreJugador;
    private JLabel lblNombreOponente;
    private VistaArrastreNaves panelNaves;
    private JTextArea txtNotificaciones;
    
    // Controlador (único punto de comunicación)
    private final ControlDisparo controlador;

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
    public VistaConfiguracionNaves(String nombreJugador, String nombreOponente, ControlDisparo controlador) {
        // Agregar argumentos a atributos
        this.nombreJugadorActual = nombreJugador;
        this.nombreJugadorOponente = nombreOponente;
        this.controlador = controlador;
        
        // Configurar el panel
        configurarVentana();
        
        // Cargar los recursos y elementos del panel
        inicializarComponentes();
        
        
        
        // Registrarse como observador
        controlador.getPartida().agregarObserver(this);

    }
    
    public void nuevaPartida(String nombreJugador, String nombreOponente, NaveDTO[] naves){
        // Nuevos jugadores
        this.nombreJugadorActual = nombreJugador;
        this.nombreJugadorOponente = nombreOponente;
        
        // Remueve todos los elementos del panel.
        removeAll();
        
        // Carga nuevamente los recursos del panel.
        inicializarComponentes();
    }
    
    private void configurarVentana() {
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_FONDO);
    }

    private void inicializarComponentes() {
        add(crearPanelSuperior(), BorderLayout.NORTH);
        add(crearPanelCentral(), BorderLayout.CENTER);
        add(crearPanelNaves(), BorderLayout.EAST);
        add(crearPanelInferior(), BorderLayout.SOUTH);
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

        panel.add(panelJugador, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelCentral() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 30, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        panel.add(crearPanelTablero("TABLERO DE DISPAROS"));
        panel.add(crearPanelTablero("MI TABLERO"));

        return panel;
    }

    private JPanel crearPanelTablero(String titulo) {
        JPanel panelContenedor = new JPanel(new BorderLayout(0, 10));
        panelContenedor.setBackground(COLOR_FONDO);

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_BORDE);

        JPanel tablero = crearTablero();
        tablero.setBorder(new LineBorder(COLOR_BORDE, 3, true));

        panelContenedor.add(lblTitulo, BorderLayout.NORTH);
        panelContenedor.add(tablero, BorderLayout.CENTER);

        return panelContenedor;
    }

    private JPanel crearTablero() {
        
        VistaTableroNaves tableroPropio = new VistaTableroNaves();
        panelTableroPropio = tableroPropio;
        return tableroPropio;
        
    }
    
    private JPanel crearPanelNaves() {
        VistaArrastreNaves panel = new VistaArrastreNaves();
        panelNaves = panel;
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

        JButton btnAbandonar = new JButton("Abandonar Partida");
        btnAbandonar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAbandonar.setBackground(new Color(220, 53, 69));
        btnAbandonar.setForeground(Color.WHITE);
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAbandonar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbandonar.addActionListener(e -> botonAbandonarBatallaActionPerformed(e));
        
        JButton btnListo = new JButton("Listo");
        btnListo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnListo.setBackground(Color.GREEN);
        btnListo.setForeground(Color.WHITE);
        btnListo.setFocusPainted(false);
        btnListo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnListo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnListo.setEnabled(false);

        panelBotones.add(btnAbandonar);
        panelBotones.add(btnListo);

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
     * @param naves Naves a mostrar.
     */
    public void mostrarNavesDesdeDTO(NaveDTO[] naves) {
        if (naves == null)
            return;

        for (NaveDTO nave : naves) {
            CoordenadaDTO[] coordenadas = nave.getCoordenadas();
            for (CoordenadaDTO coord : coordenadas) {
                panelTableroPropio.marcarNave(coord.getX(), coord.getY());
            }
        }
    }

    /**
     * Actualiza el marcador usando DTOs
     * 
     * @param navesOponente
     */
    public void actualizarMarcadorDesdeDTO(NaveDTO[] navesOponente) {
        // pendiente...y seguramente será removido de esta clase...
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
    
    @Override
    public void notificar(String mensaje, String nombreJugadorTurno) {
        SwingUtilities.invokeLater(() -> {
            
        });
    }
    /**
     * Devuelve el controlador.
     * @return Controlador.
     */
    public ControlDisparo getControlador() {return controlador;}
    /**
     * Devuelve el nombre del jugador actual.
     * @return Nombre del jugador actual.
     */
    public String getNombreJugadorActual() {return nombreJugadorActual;}
    /**
     * Dispara el mecanismo de abandono de la partida, al hacer click en el botón correspondiente.
     * @param evt Click.
     */
    private void botonAbandonarBatallaActionPerformed(java.awt.event.ActionEvent evt) {                                                      
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
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}