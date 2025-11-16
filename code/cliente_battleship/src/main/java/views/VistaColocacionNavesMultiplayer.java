package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para colocación de naves en modo multijugador
 * Permite al jugador colocar sus naves y enviarlas al servidor
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaColocacionNavesMultiplayer extends JPanel implements ControladorJuego.IVistaColocacionNaves {

    private final ControladorJuego controlador;
    private final JugadorDTO jugadorLocal;
    private final JugadorDTO oponente;

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblInstrucciones;
    private JTextArea txtLog;
    private JButton btnColocarNaves;
    private JButton btnListo;
    private JPanel panelNaves;

    // Naves a colocar
    private List<NaveDTO> navesColocadas;
    private static final int TAMANO_TABLERO = 10;

    /**
     * Constructor
     */
    public VistaColocacionNavesMultiplayer(JugadorDTO jugadorLocal, JugadorDTO oponente,
                                          ControladorJuego controlador) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.controlador = controlador;
        this.navesColocadas = new ArrayList<>();

        // Registrar esta vista en el controlador
        controlador.setVistaColocacion(this);

        inicializarComponentes();
        configurarLayout();
    }

    /**
     * Inicializa los componentes de la UI
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 248, 255));

        // Panel superior con título
        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 5, 5));
        panelSuperior.setOpaque(false);

        lblTitulo = new JLabel("COLOCACIÓN DE NAVES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 51, 102));

        JLabel lblJugadores = new JLabel(
            jugadorLocal.getNombre() + " vs " + oponente.getNombre(),
            SwingConstants.CENTER
        );
        lblJugadores.setFont(new Font("Arial", Font.PLAIN, 16));

        lblInstrucciones = new JLabel(
            "Coloca tus naves en el tablero. Necesitas: 1 Portaaviones (4), 1 Crucero (3), 2 Submarinos (2), 1 Barco (1)",
            SwingConstants.CENTER
        );
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 12));

        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblJugadores);
        panelSuperior.add(lblInstrucciones);

        // Panel central con formulario de naves
        panelNaves = new JPanel();
        panelNaves.setLayout(new BoxLayout(panelNaves, BoxLayout.Y_AXIS));
        panelNaves.setOpaque(false);

        agregarFormulariosNaves();

        JScrollPane scrollNaves = new JScrollPane(panelNaves);
        scrollNaves.setBorder(BorderFactory.createTitledBorder("Configurar Naves"));

        // Panel de log
        txtLog = new JTextArea(5, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro"));

        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);

        btnColocarNaves = new JButton("Colocar Naves Automáticamente");
        btnColocarNaves.setFont(new Font("Arial", Font.BOLD, 14));
        btnColocarNaves.setBackground(new Color(70, 130, 180));
        btnColocarNaves.setForeground(Color.WHITE);
        btnColocarNaves.setFocusPainted(false);
        btnColocarNaves.addActionListener(e -> colocarNavesAutomaticamente());

        btnListo = new JButton("¡Listo! Enviar al Servidor");
        btnListo.setFont(new Font("Arial", Font.BOLD, 14));
        btnListo.setBackground(new Color(34, 139, 34));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFocusPainted(false);
        btnListo.setEnabled(false);
        btnListo.addActionListener(e -> enviarNaves());

        panelBotones.add(btnColocarNaves);
        panelBotones.add(btnListo);

        // Agregar componentes
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollNaves, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        panelInferior.add(scrollLog, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        log("Bienvenido " + jugadorLocal.getNombre());
        log("Esperando que coloques tus naves...");
    }

    /**
     * Agrega formularios para configurar cada nave
     */
    private void agregarFormulariosNaves() {
        // Portaaviones
        panelNaves.add(crearPanelNave("Portaaviones", TipoNave.PORTAAVIONES, 4));
        // Crucero
        panelNaves.add(crearPanelNave("Crucero", TipoNave.CRUCERO, 3));
        // Submarinos (2)
        panelNaves.add(crearPanelNave("Submarino 1", TipoNave.SUBMARINO, 2));
        panelNaves.add(crearPanelNave("Submarino 2", TipoNave.SUBMARINO, 2));
        // Barco
        panelNaves.add(crearPanelNave("Barco", TipoNave.BARCO, 1));
    }

    /**
     * Crea un panel para configurar una nave
     */
    private JPanel crearPanelNave(String nombre, TipoNave tipo, int tamano) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JLabel lblNombre = new JLabel(nombre + " (" + tamano + " casillas):");
        lblNombre.setPreferredSize(new Dimension(180, 25));

        panel.add(lblNombre);
        panel.add(new JLabel("Coordenada inicial (ej: A1):"));
        JTextField txtCoordenada = new JTextField(5);
        panel.add(txtCoordenada);

        JComboBox<String> cmbOrientacion = new JComboBox<>(new String[]{"Horizontal", "Vertical"});
        panel.add(cmbOrientacion);

        // Guardar referencia para recuperar datos después
        panel.putClientProperty("nombre", nombre);
        panel.putClientProperty("tipo", tipo);
        panel.putClientProperty("tamano", tamano);
        panel.putClientProperty("txtCoordenada", txtCoordenada);
        panel.putClientProperty("cmbOrientacion", cmbOrientacion);

        return panel;
    }

    /**
     * Coloca naves automáticamente de forma aleatoria
     */
    private void colocarNavesAutomaticamente() {
        navesColocadas.clear();
        log("Colocando naves automáticamente...");

        // Crear naves con posiciones automáticas simples
        navesColocadas.add(crearNaveAutomatica(TipoNave.PORTAAVIONES, 4, 0, 0, true));
        navesColocadas.add(crearNaveAutomatica(TipoNave.CRUCERO, 3, 2, 0, true));
        navesColocadas.add(crearNaveAutomatica(TipoNave.SUBMARINO, 2, 4, 0, true));
        navesColocadas.add(crearNaveAutomatica(TipoNave.SUBMARINO, 2, 6, 0, true));
        navesColocadas.add(crearNaveAutomatica(TipoNave.BARCO, 1, 8, 0, true));

        log("Naves colocadas: " + navesColocadas.size());
        log("Presiona '¡Listo!' para enviar al servidor");

        btnListo.setEnabled(true);
        btnColocarNaves.setEnabled(false);
    }

    /**
     * Crea una nave con coordenadas automáticas
     */
    private NaveDTO crearNaveAutomatica(TipoNave tipo, int tamano, int fila, int columna, boolean horizontal) {
        NaveDTO nave = new NaveDTO();
        nave.setTipo(tipo);
        nave.setLongitudTotal(tamano);
        nave.setEstado(EstadoNave.INTACTA);
        nave.setImpactosRecibidos(0);
        nave.setOrientacion(horizontal ? OrientacionNave.HORIZONTAL : OrientacionNave.VERICAL);

        List<CoordenadaDTO> listaCoordenadas = new ArrayList<>();
        for (int i = 0; i < tamano; i++) {
            if (horizontal) {
                listaCoordenadas.add(new CoordenadaDTO(fila, columna + i));
            } else {
                listaCoordenadas.add(new CoordenadaDTO(fila + i, columna));
            }
        }

        // Convertir lista a array
        CoordenadaDTO[] coordenadas = listaCoordenadas.toArray(new CoordenadaDTO[0]);
        nave.setCoordenadas(coordenadas);

        return nave;
    }

    /**
     * Envía las naves al servidor
     */
    private void enviarNaves() {
        if (navesColocadas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Primero debes colocar las naves",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        log("Enviando naves al servidor...");
        controlador.enviarNavesColocadas(navesColocadas);

        btnListo.setEnabled(false);
        btnColocarNaves.setEnabled(false);
    }

    /**
     * Configura el layout
     */
    private void configurarLayout() {
        // Ya configurado en inicializarComponentes
    }

    /**
     * Agrega un mensaje al log
     */
    private void log(String mensaje) {
        txtLog.append("[" + System.currentTimeMillis() % 100000 + "] " + mensaje + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    // Implementación de IVistaColocacionNaves

    @Override
    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            log("INFO: " + mensaje);
        });
    }

    @Override
    public void mostrarError(String error) {
        SwingUtilities.invokeLater(() -> {
            log("ERROR: " + error);
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    @Override
    public void navesColocadas() {
        SwingUtilities.invokeLater(() -> {
            log("✓ Servidor confirmó recepción de naves");
        });
    }

    @Override
    public void esperandoOponente() {
        SwingUtilities.invokeLater(() -> {
            log("Esperando a que " + oponente.getNombre() + " coloque sus naves...");
            lblInstrucciones.setText("Esperando al oponente...");
        });
    }

    @Override
    public void iniciarJuego() {
        SwingUtilities.invokeLater(() -> {
            log("¡Ambos jugadores listos! Iniciando batalla...");
            // Cambiar a vista de juego
            FlujoVista.mostrarJuegoMultiplayer(jugadorLocal, oponente, controlador);
        });
    }
}
