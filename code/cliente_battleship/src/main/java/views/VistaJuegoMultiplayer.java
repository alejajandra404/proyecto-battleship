package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Vista principal del juego multijugador
 * Muestra ambos tableros y permite realizar disparos
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaJuegoMultiplayer extends JPanel implements ControladorJuego.IVistaJuego {

    private final ControladorJuego controlador;
    private final JugadorDTO jugadorLocal;
    private final JugadorDTO oponente;

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblTurno;
    private JTextArea txtLog;
    private JButton[][] botonesTableroPropio;
    private JButton[][] botonesTableroOponente;
    private JPanel panelTableroPropio;
    private JPanel panelTableroOponente;

    private static final int TAMANO_TABLERO = 10;
    private static final Color COLOR_AGUA = new Color(100, 149, 237);
    private static final Color COLOR_NAVE = new Color(128, 128, 128);
    private static final Color COLOR_IMPACTO = new Color(220, 20, 60);
    private static final Color COLOR_HUNDIDO = new Color(139, 0, 0);
    private static final Color COLOR_FALLO = new Color(173, 216, 230);

    private boolean miTurno = false;

    /**
     * Constructor
     */
    public VistaJuegoMultiplayer(JugadorDTO jugadorLocal, JugadorDTO oponente,
                                ControladorJuego controlador) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.controlador = controlador;

        // Registrar esta vista en el controlador
        controlador.setVistaJuego(this);

        inicializarComponentes();
        log("¡Batalla iniciada!");
        log("Esperando asignación de turno...");
    }

    /**
     * Inicializa los componentes de la UI
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 248, 255));

        // Panel superior con título y turno
        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSuperior.setOpaque(false);

        lblTitulo = new JLabel("BATALLA NAVAL - " + jugadorLocal.getNombre() + " vs " + oponente.getNombre(),
                              SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));

        lblTurno = new JLabel("Esperando turno...", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurno.setForeground(Color.DARK_GRAY);

        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblTurno);

        // Panel central con ambos tableros
        JPanel panelTableros = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTableros.setOpaque(false);

        // Tablero propio (izquierda)
        JPanel contenedorPropio = new JPanel(new BorderLayout());
        contenedorPropio.setBorder(BorderFactory.createTitledBorder("Tu Tablero - " + jugadorLocal.getNombre()));
        contenedorPropio.setOpaque(false);

        panelTableroPropio = crearPanelTablero(false);
        botonesTableroPropio = extraerBotones(panelTableroPropio);

        contenedorPropio.add(panelTableroPropio, BorderLayout.CENTER);

        // Tablero oponente (derecha)
        JPanel contenedorOponente = new JPanel(new BorderLayout());
        contenedorOponente.setBorder(BorderFactory.createTitledBorder("Tablero Enemigo - " + oponente.getNombre()));
        contenedorOponente.setOpaque(false);

        panelTableroOponente = crearPanelTablero(true);
        botonesTableroOponente = extraerBotones(panelTableroOponente);

        contenedorOponente.add(panelTableroOponente, BorderLayout.CENTER);

        panelTableros.add(contenedorPropio);
        panelTableros.add(contenedorOponente);

        // Panel inferior con log
        txtLog = new JTextArea(8, 60);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro de Batalla"));

        // Agregar componentes
        add(panelSuperior, BorderLayout.NORTH);
        add(panelTableros, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
    }

    /**
     * Crea un panel de tablero con grid de botones
     */
    private JPanel crearPanelTablero(boolean paraDisparar) {
        JPanel panel = new JPanel(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 1, 1));
        panel.setBackground(Color.DARK_GRAY);

        // Encabezados de columnas (A-J)
        panel.add(new JLabel("")); // Esquina superior izquierda
        for (int col = 0; col < TAMANO_TABLERO; col++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(lbl);
        }

        // Filas con números
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            // Número de fila
            JLabel lblFila = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(lblFila);

            // Casillas
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                JButton btn = new JButton();
                btn.setBackground(COLOR_AGUA);
                btn.setPreferredSize(new Dimension(40, 40));
                btn.setMargin(new Insets(0, 0, 0, 0));
                btn.setFocusPainted(false);

                if (paraDisparar) {
                    // Solo el tablero oponente tiene acciones de disparo
                    final int x = fila;
                    final int y = col;
                    btn.putClientProperty("x", x);
                    btn.putClientProperty("y", y);
                    btn.addActionListener(e -> realizarDisparo(x, y));
                } else {
                    btn.setEnabled(false);
                }

                panel.add(btn);
            }
        }

        return panel;
    }

    /**
     * Extrae la matriz de botones de un panel de tablero
     */
    private JButton[][] extraerBotones(JPanel panelTablero) {
        JButton[][] botones = new JButton[TAMANO_TABLERO][TAMANO_TABLERO];
        Component[] components = panelTablero.getComponents();

        // Los primeros TAMANO_TABLERO + 1 componentes son los encabezados
        int idx = TAMANO_TABLERO + 1;

        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            idx++; // Saltar el label de número de fila
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                botones[fila][col] = (JButton) components[idx++];
            }
        }

        return botones;
    }

    /**
     * Realiza un disparo en las coordenadas especificadas
     */
    private void realizarDisparo(int x, int y) {
        if (!miTurno) {
            mostrarError("¡No es tu turno!");
            return;
        }

        CoordenadaDTO coordenada = new CoordenadaDTO(x, y);
        log("Disparando a " + coordenada.toStringCoord());

        controlador.realizarDisparo(coordenada);
    }

    /**
     * Agrega un mensaje al log
     */
    private void log(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + System.currentTimeMillis() % 100000 + "] " + mensaje + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    // Implementación de IVistaJuego

    @Override
    public void actualizarTableros(TableroDTO miTablero, TableroDTO tableroOponente) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[VISTA_JUEGO] ========== ACTUALIZANDO TABLEROS ==========");
            System.out.println("[VISTA_JUEGO] Mi ID: " + jugadorLocal.getId());
            System.out.println("[VISTA_JUEGO] Mi tablero: " + miTablero);
            System.out.println("[VISTA_JUEGO] Otro tablero: " + tableroOponente);
            log("Actualizando tableros...");

            // Actualizar mi tablero
            if (miTablero != null) {
                System.out.println("[VISTA_JUEGO] Mi tablero - ID: " + miTablero.getIdJugador());
                System.out.println("[VISTA_JUEGO] ===== CONTENIDO COMPLETO MI TABLERO (10x10) =====");

                int contadorDisparos = 0;
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    StringBuilder fila = new StringBuilder("[VISTA_JUEGO] Fila " + i + ": ");
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        EstadoCasilla estado = miTablero.getCasillas()[i][j];

                        // Abreviaturas para visualización
                        String abrev = "";
                        switch (estado) {
                            case VACIA: abrev = "·"; break;
                            case OCUPADA: abrev = "■"; break;
                            case IMPACTADA_VACIA: abrev = "○"; contadorDisparos++; break;
                            case IMPACTADA_AVERIADA: abrev = "X"; contadorDisparos++; break;
                            case IMPACTADA_HUNDIDA: abrev = "✖"; contadorDisparos++; break;
                        }
                        fila.append(abrev).append(" ");

                        actualizarCasilla(botonesTableroPropio[i][j], estado, false);
                    }
                    System.out.println(fila.toString());
                }
                System.out.println("[VISTA_JUEGO] ===== FIN MI TABLERO - Total disparos: " + contadorDisparos + " =====");
            } else {
                System.err.println("[VISTA_JUEGO] ERROR: miTablero es null");
            }

            // Actualizar tablero oponente
            if (tableroOponente != null) {
                System.out.println("[VISTA_JUEGO] Tablero oponente - ID: " + tableroOponente.getIdJugador());
                System.out.println("[VISTA_JUEGO] ===== CONTENIDO COMPLETO TABLERO OPONENTE (10x10) =====");

                int contadorDisparos = 0;
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    StringBuilder fila = new StringBuilder("[VISTA_JUEGO] Fila " + i + ": ");
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        EstadoCasilla estado = tableroOponente.getCasillas()[i][j];

                        // Abreviaturas para visualización
                        String abrev = "";
                        switch (estado) {
                            case VACIA: abrev = "·"; break;
                            case OCUPADA: abrev = "■"; break;
                            case IMPACTADA_VACIA: abrev = "○"; contadorDisparos++; break;
                            case IMPACTADA_AVERIADA: abrev = "X"; contadorDisparos++; break;
                            case IMPACTADA_HUNDIDA: abrev = "✖"; contadorDisparos++; break;
                        }
                        fila.append(abrev).append(" ");

                        actualizarCasilla(botonesTableroOponente[i][j], estado, true);
                    }
                    System.out.println(fila.toString());
                }
                System.out.println("[VISTA_JUEGO] ===== FIN TABLERO OPONENTE - Total disparos: " + contadorDisparos + " =====");
            } else {
                System.err.println("[VISTA_JUEGO] ERROR: tableroOponente es null");
            }

            // Forzar repaint
            panelTableroPropio.repaint();
            panelTableroOponente.repaint();
            System.out.println("[VISTA_JUEGO] ========== TABLEROS ACTUALIZADOS ==========");
        });
    }

    /**
     * Actualiza el color y estado de una casilla
     */
    private void actualizarCasilla(JButton boton, EstadoCasilla estado, boolean esOponente) {
        Color colorAnterior = boton.getBackground();
        String textoAnterior = boton.getText();

        switch (estado) {
            case VACIA:
                boton.setBackground(COLOR_AGUA);
                boton.setText("");
                break;

            case OCUPADA:
                // En mi tablero muestra naves, en el del oponente no
                if (!esOponente) {
                    boton.setBackground(COLOR_NAVE);
                    boton.setText("■");
                } else {
                    boton.setBackground(COLOR_AGUA);
                    boton.setText("");
                }
                break;

            case IMPACTADA_VACIA:
                boton.setBackground(COLOR_FALLO);
                boton.setText("○");
                boton.setEnabled(false);
                if (!textoAnterior.equals("○")) {
                    System.out.println("[VISTA_JUEGO] Actualizando casilla a FALLO (○) - Oponente: " + esOponente);
                }
                break;

            case IMPACTADA_AVERIADA:
                boton.setBackground(COLOR_IMPACTO);
                boton.setText("X");
                boton.setEnabled(false);
                if (!textoAnterior.equals("X")) {
                    System.out.println("[VISTA_JUEGO] Actualizando casilla a IMPACTO (X) - Oponente: " + esOponente);
                }
                break;

            case IMPACTADA_HUNDIDA:
                boton.setBackground(COLOR_HUNDIDO);
                boton.setText("✖");
                boton.setEnabled(false);
                if (!textoAnterior.equals("✖")) {
                    System.out.println("[VISTA_JUEGO] Actualizando casilla a HUNDIDO (✖) - Oponente: " + esOponente);
                }
                break;
        }
    }

    @Override
    public void mostrarResultadoDisparo(DisparoDTO disparo) {
        SwingUtilities.invokeLater(() -> {
            String mensaje = disparo.getNombreJugador() + " disparó a " +
                           disparo.getCoordenada().toStringCoord() + ": " +
                           disparo.getResultado();

            if (disparo.getMensaje() != null) {
                mensaje += " - " + disparo.getMensaje();
            }

            log(mensaje);
        });
    }

    @Override
    public void actualizarTurno(boolean miTurno, JugadorDTO jugadorEnTurno) {
        SwingUtilities.invokeLater(() -> {
            this.miTurno = miTurno;

            if (miTurno) {
                lblTurno.setText("¡ES TU TURNO! - Dispara en el tablero enemigo");
                lblTurno.setForeground(new Color(0, 128, 0));
                log(">>> ES TU TURNO <<<");

                // Habilitar tablero oponente solo en casillas no disparadas
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        String texto = botonesTableroOponente[i][j].getText();
                        // Solo habilitar si no tiene marcas de disparo (○, X, ✖)
                        if (texto.isEmpty()) {
                            botonesTableroOponente[i][j].setEnabled(true);
                        }
                    }
                }
            } else {
                lblTurno.setText("Turno de " + jugadorEnTurno.getNombre() + " - Esperando...");
                lblTurno.setForeground(Color.RED);
                log("Esperando turno del oponente...");

                // Deshabilitar tablero oponente
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        botonesTableroOponente[i][j].setEnabled(false);
                    }
                }
            }
        });
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        log("INFO: " + mensaje);
    }

    @Override
    public void mostrarError(String error) {
        SwingUtilities.invokeLater(() -> {
            log("ERROR: " + error);
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
        });
    }

    //PARTE DE FINALIZAR PARTIDA
    @Override
    public void partidaFinalizada(boolean gane, JugadorDTO ganador, EstadisticaDTO misEstadisticas) {
        SwingUtilities.invokeLater(() -> {
            String titulo = gane ? "¡VICTORIA!" : "DERROTA";
            String mensaje = gane ?
                "¡Felicidades! Has ganado la partida contra " + oponente.getNombre() :
                "Has perdido contra " + ganador.getNombre() + ". ¡Mejor suerte la próxima vez!";

            log("========================================");
            log(titulo);
            log(mensaje);
            log("========================================");

            // Deshabilitar todos los botones
            for (int i = 0; i < TAMANO_TABLERO; i++) {
                for (int j = 0; j < TAMANO_TABLERO; j++) {
                    botonesTableroOponente[i][j].setEnabled(false);
                }
            }

            lblTurno.setText("PARTIDA FINALIZADA - " + titulo);
            lblTurno.setForeground(gane ? new Color(0, 128, 0) : Color.RED);

            // Mostrar mensaje de fin de partida
            JOptionPane.showMessageDialog(null, 
            mensaje,
            titulo,
            gane ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);

            log("Partida finalizada. Mostrando Estadísticas...");

            //PANTALLA DE ESTADÍSTICAS
            FlujoVista.mostrarEstadisticas(controlador, misEstadisticas);
        });
    }
}
