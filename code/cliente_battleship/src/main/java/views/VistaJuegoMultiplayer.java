package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
/**
 * Vista principal del juego multijugador.
 * 
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
*/
public class VistaJuegoMultiplayer extends JPanel implements ControladorJuego.IVistaJuego {

    // Lista que guarda las naves colocadas en la vista VistaColocacionNavesVisual
    public static List<NaveDTO> navesParaTransferir = new ArrayList<>();

    private final ControladorJuego controlador;
    private final JugadorDTO jugadorLocal;
    private final JugadorDTO oponente;
    
    // Lista de naves propias (naves colocadas en el tablero propio)
    private List<NaveDTO> misNaves; 

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblTurno;
    private JTextArea txtLog;
    private JButton[][] botonesTableroPropio;
    private JButton[][] botonesTableroOponente;
    
    //Paneles contenedores de los tableros
    private PanelTableroConImagenes panelTableroPropio; 
    private JPanel panelTableroOponente;

    private static final int TAMANO_TABLERO = 10;
    private static final int TAMANO_CASILLA = 40;
    
    // Colores
    private static final Color COLOR_AGUA = new Color(100, 149, 237);
    private static final Color COLOR_FONDO_TABLERO = new Color(9, 117, 197);
    private static final Color COLOR_TITLE_BORDER = new Color(162, 212, 248);
    private static final Color COLOR_BORDE_TABLERO = new Color(134, 74, 52);
    private static final Color COLOR_IMPACTO = new Color(220, 20, 60, 200); 
    private static final Color COLOR_HUNDIDO = new Color(245, 132, 132);
    private static final Color COLOR_FALLO = new Color(255, 255, 255, 100);

    private boolean miTurno = false;
    
    // Imágenes
    private Map<String, BufferedImage> cacheImagenes = new HashMap<>();
    private ImageIcon iconoAgua;
    private ImageIcon iconoImpacto;
    private String sufijoColor;

    /**
     * Método constructor de la vista.
     * Inicializa componentes, carga recursos y recupera las naves colocadas en el tablero
     * @param jugadorLocal Datos del jugador local
     * @param oponente Datos del oponente
     * @param controlador Controlador de la lógica del juego
     */ 
    public VistaJuegoMultiplayer(JugadorDTO jugadorLocal, JugadorDTO oponente,
                                 ControladorJuego controlador) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.controlador = controlador;
        this.misNaves = new ArrayList<>(navesParaTransferir);

        determinarSufijoColor();
        cargarRecursos(); 
        
        controlador.setVistaJuego(this);

        inicializarComponentes();
        log("¡Batalla iniciada!");
        log("Esperando asignación de turno...");
    }
    
    /**
     * Método que actualiza la lista de naves locales y repinta el tablero
     * @param naves Lista de naves a dibujar
     */
    public void setNavesLocales(List<NaveDTO> naves) {
        this.misNaves = naves;
        if (panelTableroPropio != null) panelTableroPropio.repaint();
    }

    /**
     * Método que ayuda a determinar el sufijo de 
     * archivo de imagen según el color del jugador (rojo/azul)
     */
    private void determinarSufijoColor() {
        Color c = jugadorLocal.getColor();
        if (c != null && c.getRed() > 200 && c.getBlue() < 100) {
            this.sufijoColor = "_rojo";
        } else {
            this.sufijoColor = "_azul";
        }
    }

    /**
     * Método que carga las imágenes de las naves y los iconos de eventos (agua/impacto) en memoria
     */
    private void cargarRecursos() {
        try {
            // Cargar Naves
            String[] tipos = {"portaaviones", "crucero", "submarino", "barco"};
            for (String tipo : tipos) {
                String nombreBase = tipo + sufijoColor + ".png";
                URL url = getClass().getResource("/" + nombreBase);
                if (url == null) url = getClass().getResource("/imgs/" + nombreBase);
                
                if (url != null) {
                    BufferedImage imgH = ImageIO.read(url);
                    cacheImagenes.put(tipo + "_H", imgH);
                    cacheImagenes.put(tipo + "_V", crearVersionVertical(imgH));
                }
            }

            // Cargar Iconos
            URL urlAgua = getClass().getResource("/agua.png");
            if (urlAgua == null) urlAgua = getClass().getResource("/imgs/agua.png");
            if (urlAgua != null) {
                Image img = ImageIO.read(urlAgua).getScaledInstance(TAMANO_CASILLA - 8, TAMANO_CASILLA - 8, Image.SCALE_SMOOTH);
                iconoAgua= new ImageIcon(img);
            }

            URL urlImpacto = getClass().getResource("/impacto.png");
            if (urlImpacto == null) urlImpacto = getClass().getResource("/imgs/impacto.png");
            if (urlImpacto != null) {
                Image img = ImageIO.read(urlImpacto).getScaledInstance(TAMANO_CASILLA - 5, TAMANO_CASILLA - 5, Image.SCALE_SMOOTH);
                iconoImpacto = new ImageIcon(img);
            }

        } catch (Exception e) { e.printStackTrace(); }
    }
    
    /**
     * Método que rota una imagen 90 grados para usarla en orientación vertical
     */
    private BufferedImage crearVersionVertical(BufferedImage original) {
        int w = original.getWidth();
        int h = original.getHeight();
        BufferedImage rotada = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotada.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.translate(h, 0);
        g2d.rotate(Math.toRadians(90));
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        return rotada;
    }

    /**
     * Panel personalizado que gestiona las "capas" es decir 
     * que imagen va encima de cual
     * 1. Fondo
     * 2. Casillas
     * 3. Naves
     */
    private class PanelTableroConImagenes extends JPanel {

        public PanelTableroConImagenes(LayoutManager layout) {
            super(layout);
        }

        @Override
        protected void paintChildren(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            
            // Pintar el fondo del tablero y el color de las casillas
            g2d.setColor(COLOR_FONDO_TABLERO); 
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(COLOR_AGUA);
            if (botonesTableroPropio != null) {
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        JButton btn = botonesTableroPropio[i][j];
                        if (btn != null) {
                            g2d.fillRect(btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight());
                        }
                    }
                }
            }

            // Pintar los botones (casillas)
            super.paintChildren(g);

            // Pintar las naves por enicma de los botones (casillas)
            if (misNaves != null && !misNaves.isEmpty()) {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                
                for (NaveDTO nave : misNaves) {
                    String claveTipo = "";
                    switch(nave.getTipo()) {
                        case PORTAAVIONES: claveTipo = "portaaviones"; break;
                        case CRUCERO: claveTipo = "crucero"; break;
                        case SUBMARINO: claveTipo = "submarino"; break;
                        case BARCO: claveTipo = "barco"; break;
                    }
                    
                    boolean esHorizontal = (nave.getOrientacion() == OrientacionNave.HORIZONTAL);
                    BufferedImage img = cacheImagenes.get(claveTipo + (esHorizontal ? "_H" : "_V"));

                    if (img != null && nave.getCoordenadas().length > 0) {
                        CoordenadaDTO origen = nave.getCoordenadas()[0]; 
                        int f = origen.getX();
                        int c = origen.getY();
                        
                        if (f >= 0 && f < TAMANO_TABLERO && c >= 0 && c < TAMANO_TABLERO && 
                            botonesTableroPropio != null && botonesTableroPropio[f][c] != null) {
                            
                            JButton btnRef = botonesTableroPropio[f][c];
                            int x = btnRef.getX();
                            int y = btnRef.getY();
                            int anchoCelda = btnRef.getWidth();
                            int altoCelda = btnRef.getHeight();
                            int padding = 0; 

                            if (esHorizontal) {
                                g2d.drawImage(img, x+padding, y+padding, (anchoCelda*nave.getLongitudTotal())-(padding*2), altoCelda-(padding*2), this);
                            } else {
                                g2d.drawImage(img, x+padding, y+padding, anchoCelda-(padding*2), (altoCelda*nave.getLongitudTotal())-(padding*2), this);
                            }
                        }
                    }
                }
            }
            
            // Repintar las imágenes de los impactos para que queden pintados encima de las naves
            if (botonesTableroPropio != null) {
                 for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        JButton btn = botonesTableroPropio[i][j];
                        if (btn != null && (btn.getIcon() != null || btn.isContentAreaFilled())) {
                             Graphics gBtn = g.create(btn.getX(), btn.getY(), btn.getWidth(), btn.getHeight());
                             btn.paint(gBtn);
                             gBtn.dispose();
                        }
                    }
                 }
            }
        }
    }

    /**
     * Método que inicializa todos los componentes visuales y layouts
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 248, 255));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 1, 5, 5));
        panelSuperior.setOpaque(false);
        lblTitulo = new JLabel("BATALLA NAVAL - " + jugadorLocal.getNombre() + " vs " + oponente.getNombre(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));
        lblTurno = new JLabel("Esperando turno...", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurno.setForeground(Color.DARK_GRAY);
        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblTurno);

        JPanel panelTableros = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTableros.setOpaque(false);

        // Tablero propio
        JPanel contenedorPropio = new JPanel(new BorderLayout());
        contenedorPropio.setOpaque(false);
        contenedorPropio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1), "Tu Tablero"),
            BorderFactory.createLineBorder(COLOR_BORDE_TABLERO, 2)));

        // Panel personalizado
        panelTableroPropio = new PanelTableroConImagenes(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 0, 0));
        panelTableroPropio.setOpaque(false); 
        botonesTableroPropio = llenarPanelTablero(panelTableroPropio, false);
        contenedorPropio.add(panelTableroPropio, BorderLayout.CENTER);

        // Tablero del oponente
        JPanel contenedorOponente = new JPanel(new BorderLayout());
        contenedorOponente.setOpaque(false);
        contenedorOponente.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1), "Enemigo - " + oponente.getNombre()),
            BorderFactory.createLineBorder(COLOR_BORDE_TABLERO, 2)));

        panelTableroOponente = new JPanel(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 0, 0));
        panelTableroOponente.setBackground(COLOR_FONDO_TABLERO);
        botonesTableroOponente = llenarPanelTablero(panelTableroOponente, true); 
        contenedorOponente.add(panelTableroOponente, BorderLayout.CENTER);

        panelTableros.add(contenedorPropio);
        panelTableros.add(contenedorOponente);

        txtLog = new JTextArea(8, 60);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro de Batalla"));

        add(panelSuperior, BorderLayout.NORTH);
        add(panelTableros, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
    }
    
    /**
     * Método que crea la matriz de botones para los tableros
     * @param panel Panel contenedor
     * @param esOponente
     * @return Matriz de botones creados
     */
    private JButton[][] llenarPanelTablero(JPanel panel, boolean esOponente) {
        JButton[][] botones = new JButton[TAMANO_TABLERO][TAMANO_TABLERO];
        
        panel.add(new JLabel("")); 
        for (int col = 0; col < TAMANO_TABLERO; col++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            panel.add(lbl);
        }

        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            JLabel lblF = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblF.setForeground(Color.WHITE);
            lblF.setFont(new Font("Arial", Font.BOLD, 15));
            panel.add(lblF);

            for (int col = 0; col < TAMANO_TABLERO; col++) {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(TAMANO_CASILLA, TAMANO_CASILLA));
                btn.setMargin(new Insets(0,0,0,0));
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1));

                if (esOponente) {
                    btn.setBackground(COLOR_AGUA);
                    btn.setOpaque(true);
                    final int x = fila;
                    final int y = col;
                    btn.addActionListener(e -> realizarDisparo(x, y));
                } else {
                    btn.setContentAreaFilled(false);
                    btn.setOpaque(false);
                    btn.setEnabled(false);
                }
                
                botones[fila][col] = btn;
                panel.add(btn);
            }
        }
        return botones;
    }

    private void realizarDisparo(int x, int y) {
        if (!miTurno) {
            mostrarError("¡No es tu turno!");
            return;
        }
        controlador.realizarDisparo(new CoordenadaDTO(x, y));
    }

    /**
     * Actualiza el estado visual de ambos tableros basándose en la información del servidor
     * Maneja iconos de impacto, agua y estados de hundido
     */
    @Override
    public void actualizarTableros(TableroDTO miTablero, TableroDTO tableroOponente) {
        SwingUtilities.invokeLater(() -> {
            // Tablero propio
            if (miTablero != null) {
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        EstadoCasilla estado = miTablero.getCasillas()[i][j];
                        JButton btn = botonesTableroPropio[i][j];
                        
                        btn.setIcon(null);
                        btn.setText("");
                        btn.setBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 2));

                        switch (estado) {
                            case VACIA:
                            case OCUPADA:
                                btn.setContentAreaFilled(false);
                                btn.setOpaque(false);
                                break;
                                
                            case IMPACTADA_VACIA:
                                if (iconoAgua != null) {
                                    btn.setIcon(iconoAgua);
                                    btn.setDisabledIcon(iconoAgua);
                                    btn.setBackground(COLOR_AGUA);
                                } else {
                                    btn.setContentAreaFilled(true);
                                    btn.setBackground(COLOR_FALLO);
                                    btn.setText("o");
                                }
                                break;

                            case IMPACTADA_AVERIADA:
                                if (iconoImpacto != null) {
                                    btn.setIcon(iconoImpacto);
                                    btn.setDisabledIcon(iconoImpacto);
                                    btn.setContentAreaFilled(false); 
                                    btn.setOpaque(false);
                                } else {
                                    btn.setContentAreaFilled(true);
                                    btn.setBackground(COLOR_IMPACTO);
                                    btn.setText("X");
                                }
                                break;

                            case IMPACTADA_HUNDIDA:
                                btn.setContentAreaFilled(true);
                                btn.setOpaque(true);
                                btn.setBackground(COLOR_HUNDIDO);
                                if (iconoImpacto != null) {
                                    btn.setIcon(iconoImpacto);
                                    btn.setDisabledIcon(iconoImpacto);
                                } else {
                                    btn.setText("☠");
                                }
                                break;
                        }
                    }
                }
                if (panelTableroPropio != null) panelTableroPropio.repaint();
            }

            // Tablero del oponente
            if (tableroOponente != null) {
                for (int i = 0; i < TAMANO_TABLERO; i++) {
                    for (int j = 0; j < TAMANO_TABLERO; j++) {
                        EstadoCasilla estado = tableroOponente.getCasillas()[i][j];
                        JButton btn = botonesTableroOponente[i][j];
                        
                        btn.setIcon(null);
                        btn.setText("");
                        btn.setBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 2));
                        btn.setContentAreaFilled(true);
                        btn.setOpaque(true);

                        switch (estado) {
                            case VACIA:
                            case OCUPADA:
                                btn.setBackground(COLOR_AGUA);
                                break;
                                
                            case IMPACTADA_VACIA:
                                btn.setEnabled(false);
                                if (iconoAgua != null) {
                                    btn.setIcon(iconoAgua);
                                    btn.setDisabledIcon(iconoAgua);
                                    btn.setBackground(COLOR_AGUA); 
                                } else {
                                    btn.setBackground(new Color(173, 216, 230));
                                    btn.setText("o");
                                }
                                break;
                                
                            case IMPACTADA_AVERIADA:
                                btn.setEnabled(false);
                                if (iconoImpacto != null) {
                                    btn.setIcon(iconoImpacto);
                                    btn.setDisabledIcon(iconoImpacto);
                                    btn.setBackground(COLOR_AGUA); 
                                } else {
                                    btn.setBackground(COLOR_IMPACTO);
                                    btn.setText("X");
                                }
                                break;
                                
                            case IMPACTADA_HUNDIDA:
                                btn.setEnabled(false);
                                btn.setBackground(COLOR_HUNDIDO);
                                if (iconoImpacto != null) {
                                    btn.setIcon(iconoImpacto);
                                    btn.setDisabledIcon(iconoImpacto);
                                } else {
                                    btn.setText("☠");
                                }
                                break;
                        }
                    }
                }
            }
        });
    }

    private void log(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + System.currentTimeMillis() % 100000 + "] " + mensaje + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }

    @Override
    public void mostrarResultadoDisparo(DisparoDTO disparo) {
        SwingUtilities.invokeLater(() -> {
            log(disparo.getNombreJugador() + " disparó a " +
                disparo.getCoordenada().toStringCoord() + ": " + disparo.getResultado());
        });
    }

    @Override
    public void actualizarTurno(boolean miTurno, JugadorDTO jugadorEnTurno) {
        this.miTurno = miTurno;
        SwingUtilities.invokeLater(() -> {
            if (miTurno) {
                lblTurno.setText("¡ES TU TURNO! - Dispara en el tablero enemigo");
                lblTurno.setForeground(new Color(0, 128, 0));
                bloquearOponente(false);
            } else {
                lblTurno.setText("Turno de " + jugadorEnTurno.getNombre());
                lblTurno.setForeground(Color.GRAY);
                bloquearOponente(true);
            }
        });
    }
    
    private void bloquearOponente(boolean bloquear) {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                if (botonesTableroOponente[i][j].getIcon() == null && 
                    botonesTableroOponente[i][j].getText().isEmpty()) {
                    botonesTableroOponente[i][j].setEnabled(!bloquear);
                }
            }
        }
    }

    @Override public void mostrarMensaje(String mensaje) { log("INFO: " + mensaje); }

    @Override
    public void mostrarError(String error) {
        SwingUtilities.invokeLater(() -> {
            log("ERROR: " + error);
            JOptionPane.showMessageDialog(this, error);
        });
    }

    @Override
    public void partidaFinalizada(boolean gane, JugadorDTO ganador) {
        JOptionPane.showMessageDialog(this, gane ? "¡GANASTE!" : "PERDISTE");
        FlujoVista.mostrarListaJugadores(
            controlador.getServicioConexion(),
            controlador.getJugadorLocal()
        );
    }
}