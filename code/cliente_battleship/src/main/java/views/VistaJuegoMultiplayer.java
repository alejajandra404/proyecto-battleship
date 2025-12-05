package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import patterns.observer.IObserver;
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
 * Implementa el patrón Observer para recibir notificaciones del modelo EstadoLocalJuego.
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
*/
public class VistaJuegoMultiplayer extends JPanel implements ControladorJuego.IVistaJuego, IObserver {

    // Lista que guarda las naves colocadas en la vista VistaColocacionNavesVisual
    public static List<NaveDTO> navesParaTransferir = new ArrayList<>();

    private final ControladorJuego controlador;

    // Datos del jugador local (primitivos, no DTOs)
    private final String nombreJugadorLocal;
    private final Color colorJugadorLocal;

    // Datos del oponente (primitivos, no DTOs)
    private final String nombreOponente;

    // Lista de naves propias (naves colocadas en el tablero propio)
    private List<NaveDTO> misNaves; 

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblTurno;
    private JLabel lblTiempo;
    private JTextArea txtLog;
    private JButton[][] botonesTableroPropio;
    private JButton[][] botonesTableroOponente;
    private JButton btnAbandonar;
    
    //Paneles contenedores de los tableros
    private PanelTableroConImagenes panelTableroPropio; 
    private JPanel panelTableroOponente;
    private JPanel panelMarcador;
    
    //Lista de componentes visuales para el marcador
    private List<PanelEstadoNave> listaPanelesEstado = new ArrayList<>();

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

    //Colores para el marcador
    private static final Color ESTADO_INTACTO = new Color(50, 205, 50);
    private static final Color ESTADO_AVERIADO = new Color(255, 215, 0);
    private static final Color ESTADO_HUNDIDO = new Color(220, 20, 60);
    
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
        // Extraer solo los datos necesarios (primitivos) de los DTOs
        this.nombreJugadorLocal = jugadorLocal.getNombre();
        this.colorJugadorLocal = jugadorLocal.getColor();
        this.nombreOponente = oponente.getNombre();

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
        Color c = colorJugadorLocal;
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
    
    // --- CLASE INTERNA: PANEL INDIVIDUAL PARA CADA NAVE EN EL MARCADOR ---
    private class PanelEstadoNave extends JPanel {
        private NaveDTO naveRef;
        private BufferedImage imagen;
        private Color colorEstado = ESTADO_INTACTO; // Empieza Verde

        public PanelEstadoNave(NaveDTO nave) {
            this.naveRef = nave;
            
            // Buscar imagen horizontal
            String claveTipo = "";
            switch(nave.getTipo()) {
                case PORTAAVIONES: claveTipo = "portaaviones"; break;
                case CRUCERO: claveTipo = "crucero"; break;
                case SUBMARINO: claveTipo = "submarino"; break;
                case BARCO: claveTipo = "barco"; break;
            }
            this.imagen = cacheImagenes.get(claveTipo + "_H");

            // Tamaño ajustado: Ancho según nave (escalado a 20px por celda para que quepa) + margen
            int ancho = (nave.getLongitudTotal() * 25) + 10; 
            setPreferredSize(new Dimension(ancho, 50));
            setOpaque(false);
        }

        // Método para verificar salud de la nave revisando el tablero
        public void actualizarEstado(TableroDTO tablero) {
            if (tablero == null) return;

            int casillasDañadas = 0;
            int casillasHundidas = 0;
            int longitud = naveRef.getLongitudTotal();

            // Revisamos cada coordenada de esta nave en el tablero actual
            for (CoordenadaDTO coord : naveRef.getCoordenadas()) {
                EstadoCasilla estado = tablero.getCasillas()[coord.getX()][coord.getY()];
                if (estado == EstadoCasilla.IMPACTADA_AVERIADA) casillasDañadas++;
                if (estado == EstadoCasilla.IMPACTADA_HUNDIDA) casillasHundidas++;
            }

            if (casillasHundidas > 0 || (casillasDañadas + casillasHundidas) == longitud) {
                colorEstado = ESTADO_HUNDIDO; // Rojo
            } else if (casillasDañadas > 0) {
                colorEstado = ESTADO_AVERIADO; // Amarillo
            } else {
                colorEstado = ESTADO_INTACTO; // Verde
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. Dibujar imagen de la nave (escalada un poco más pequeña para el marcador)
            if (imagen != null) {
                int anchoImg = naveRef.getLongitudTotal() * 25;
                int altoImg = 25;
                // Centrar horizontalmente
                int x = (getWidth() - anchoImg) / 2;
                g2d.drawImage(imagen, x, 5, anchoImg, altoImg, this);
            }

            // 2. Dibujar Círculo de Estado debajo
            int diametro = 12;
            int circuloX = (getWidth() - diametro) / 2;
            int circuloY = 35; // Debajo de la imagen

            g2d.setColor(colorEstado);
            g2d.fillOval(circuloX, circuloY, diametro, diametro);
            
            // Borde del círculo
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawOval(circuloX, circuloY, diametro, diametro);
        }
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
        lblTitulo = new JLabel("BATALLA NAVAL - " + nombreJugadorLocal + " vs " + nombreOponente, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));
        lblTurno = new JLabel("Esperando turno...", SwingConstants.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 16));
        lblTurno.setForeground(Color.DARK_GRAY);

        lblTiempo = new JLabel("⏱️ Tiempo: 30s", SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTiempo.setForeground(new Color(0, 150, 0));

        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblTurno);
        panelSuperior.add(lblTiempo);

        JPanel panelTableros = new JPanel(new GridLayout(1, 2, 20, 0));
        panelTableros.setOpaque(false);

        // Tablero propio
        JPanel contenedorPropio = new JPanel(new BorderLayout());
        contenedorPropio.setOpaque(false);
        contenedorPropio.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1), "Tu Tablero - " + nombreJugadorLocal),
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
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1), "Enemigo - " + nombreOponente),
            BorderFactory.createLineBorder(COLOR_BORDE_TABLERO, 2)));

        panelTableroOponente = new JPanel(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 0, 0));
        panelTableroOponente.setBackground(COLOR_FONDO_TABLERO);
        botonesTableroOponente = llenarPanelTablero(panelTableroOponente, true); 
        contenedorOponente.add(panelTableroOponente, BorderLayout.CENTER);

        panelTableros.add(contenedorPropio);
        panelTableros.add(contenedorOponente);

        // Panel del marcador
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 10));
        panelDerecho.setOpaque(false);
        panelDerecho.setPreferredSize(new Dimension(200, 0));
        panelMarcador = crearPanelMarcador();
        panelDerecho.add(panelMarcador, BorderLayout.CENTER);

        // Botón Abandonar (Estilo Rojo)
        btnAbandonar = new JButton("Abandonar Partida");
        btnAbandonar.setFont(new Font("Arial", Font.BOLD, 14));
        btnAbandonar.setBackground(new Color(220, 20, 60));
        btnAbandonar.setForeground(Color.WHITE);
        btnAbandonar.setFocusPainted(false);
        btnAbandonar.setPreferredSize(new Dimension(0, 40));
        btnAbandonar.addActionListener(e -> abandonarPartida());
        
        panelDerecho.add(btnAbandonar, BorderLayout.SOUTH);
        
        txtLog = new JTextArea(8, 60);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro de Batalla"));

        add(panelSuperior, BorderLayout.NORTH);
        add(panelTableros, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
        add(scrollLog, BorderLayout.SOUTH);
    }
    
    /**
     * Método que crea el panel lateral con el estado de las naves
     */
    private JPanel crearPanelMarcador() {
        JPanel panel = new JPanel();
        // Usar un GridLayout de 2 columnas para acomodar las imágenes de las naves y sus estados
        panel.setLayout(new GridLayout(0, 2, 5, 5)); 
        panel.setBorder(BorderFactory.createTitledBorder("Marcador de tu Flota"));
        panel.setBackground(new Color(240, 248, 255));
        
        listaPanelesEstado.clear();

        if (misNaves != null) {
            // Separé naves por su tipo
            List<NaveDTO> portaavioness = new ArrayList<>();
            List<NaveDTO> cruceros = new ArrayList<>();
            List<NaveDTO> submarinos = new ArrayList<>();
            List<NaveDTO> barcos = new ArrayList<>();

            for (NaveDTO nave : misNaves) {
                switch(nave.getTipo()) {
                    case PORTAAVIONES: portaavioness.add(nave); break;
                    case CRUCERO:      cruceros.add(nave); break;
                    case SUBMARINO:    submarinos.add(nave); break;
                    case BARCO:        barcos.add(nave); break;
                }
            }

            // Agregar en orden cada nave
            
            // Fila 1: Portaaviones - Crucero
            agregarAlMarcador(panel, obtenerNave(portaavioness, 0));
            agregarAlMarcador(panel, obtenerNave(cruceros, 0));
            
            // Fila 2: Portaaviones - Crucero
            agregarAlMarcador(panel, obtenerNave(portaavioness, 1));
            agregarAlMarcador(panel, obtenerNave(cruceros, 1));
            
            // Fila 3: Submarino - Submarino 
            agregarAlMarcador(panel, obtenerNave(submarinos, 0));
            agregarAlMarcador(panel, obtenerNave(submarinos, 1));
            
            // Fila 4: Submarino - Submarino
            agregarAlMarcador(panel, obtenerNave(submarinos, 2));
            agregarAlMarcador(panel, obtenerNave(submarinos, 3));
            
            // Fila 5: Barco - Barco
            agregarAlMarcador(panel, obtenerNave(barcos, 0));
            agregarAlMarcador(panel, obtenerNave(barcos, 1));
            
            // Fila 6: Barco 3 - Nada jaja
            agregarAlMarcador(panel, obtenerNave(barcos, 2));
        }
        return panel;
    }

    // Método auxiliar que obtiene las naves de la lista de navesDTO
    private NaveDTO obtenerNave(List<NaveDTO> lista, int indice) {
        if (indice < lista.size()) {
            return lista.get(indice);
        }
        return null;
    }

    // Método auxiliar para crear el panel del marcador y agregarlo a la vista
    private void agregarAlMarcador(JPanel panelContenedor, NaveDTO nave) {
        if (nave != null) {
            PanelEstadoNave pEstado = new PanelEstadoNave(nave);
            listaPanelesEstado.add(pEstado);
            panelContenedor.add(pEstado);
        } else {
            // Si no hay naves se agrega un espacio vacío transparente para que no haya problemas con el acomodo del grid
            JPanel hueco = new JPanel();
            hueco.setOpaque(false);
            panelContenedor.add(hueco);
        }
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
                
                //Actualizar el marcador
                for (PanelEstadoNave pEstado : listaPanelesEstado) {
                    pEstado.actualizarEstado(miTablero);
                }
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
            // Resetear el label del tiempo cuando cambia el turno
            actualizarTiempoTurno(30);
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

    @Override
    public void actualizarTiempoTurno(int tiempoRestante) {
        SwingUtilities.invokeLater(() -> {
            lblTiempo.setText("⏱️ Tiempo: " + tiempoRestante + "s");

            // Cambiar color según el tiempo restante
            if (tiempoRestante <= 10) {
                lblTiempo.setForeground(Color.RED);
            } else if (tiempoRestante <= 20) {
                lblTiempo.setForeground(new Color(255, 165, 0)); // Naranja
            } else {
                lblTiempo.setForeground(new Color(0, 150, 0)); // Verde
            }
        });
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
    public void partidaFinalizada(boolean gane, JugadorDTO ganador, EstadisticaDTO misEstadisticas) {
        SwingUtilities.invokeLater(() -> {
            EstadisticaDTO statsFinales = misEstadisticas;

            String titulo = gane ? "¡VICTORIA!" : "DERROTA";
            String mensaje = gane ?
                    "¡Felicidades! Has ganado la partida contra " + nombreOponente :
                    "Has perdido contra " + ganador.getNombre() + ". ¡Mejor suerte la próxima vez!";

            log("========================================");
            log(titulo);
            log(mensaje);
            log("========================================");

            // Deshabilitar botones
            for (int i = 0; i < TAMANO_TABLERO; i++) {
                for (int j = 0; j < TAMANO_TABLERO; j++) {
                    botonesTableroOponente[i][j].setEnabled(false);
                }
            }

            lblTurno.setText("PARTIDA FINALIZADA - " + titulo);
            lblTurno.setForeground(gane ? new Color(0, 128, 0) : Color.RED);
            
            JOptionPane.showMessageDialog(null, 
                mensaje,
                titulo, 
                gane ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
            log("Partida finalizada. Intentando mostrar Estadísticas...");

            statsFinales = new EstadisticaDTO(nombreJugadorLocal, gane,
                        statsFinales.getTotalDisparos(), statsFinales.getAciertos(),
                        statsFinales.getBarcosHundidos());

            //DIAGNÓSTICO *sonidos de ambulancia*
            System.out.println("[DEBUG VISTA] Preparando cambio de pantalla...");

            if (controlador == null) {
                System.err.println("[ERROR CRÍTICO] El controlador es NULL.");
            }

            if (statsFinales == null) {
                System.err.println("[ERROR CRÍTICO] Las estadísticas llegaron NULL.");
                System.err.println("[DEBUG] Nombre esperado (Local): " + nombreJugadorLocal);

            } else {
                System.out.println("[DEBUG] Stats válidas. Disparos: " + statsFinales.getTotalDisparos());
            }

            try {
                FlujoVista.mostrarEstadisticas(controlador, statsFinales);
                System.out.println("[DEBUG] Cambio de vista solicitado exitosamente.");
            } catch (Exception e) {
                System.err.println("[ERROR FATAL] Explotó al crear VistaEstadisticas:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al abrir estadísticas: " + e.getMessage());
            }
        });
    }

    @Override
    public void partidaAbandonada(String nombreAbandonador) {
        SwingUtilities.invokeLater(() -> {
            String titulo = "Partida Abandonada";
            String mensaje = nombreAbandonador + " ha abandonado la partida.";

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

            lblTurno.setText("PARTIDA ABANDONADA");
            lblTurno.setForeground(Color.ORANGE);

            JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                JOptionPane.WARNING_MESSAGE
            );

            log("Volviendo a la lista de jugadores...");
            FlujoVista.mostrarListaJugadores(controlador.getServicioConexion(),
                controlador.getJugadorLocal());
        });
    }

    /**
     * Método para abandonar partida / rendirse
     */
    private void abandonarPartida(){
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Estás seguro de que deseas abandonar la partida?",
            "Abandonar Partida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            log("Jugador ha decidido abandonar la partida");

            // Notificar al servidor que se abandona la partida
            controlador.abandonarPartida();

            // Deshabilitar todos los botones
            for (int i = 0; i < TAMANO_TABLERO; i++) {
                for (int j = 0; j < TAMANO_TABLERO; j++) {
                    botonesTableroOponente[i][j].setEnabled(false);
                }
            }

            lblTurno.setText("PARTIDA ABANDONADA");
            log("Volviendo a la lista de jugadores...");

            // Volver a la lista de jugadores
            FlujoVista.mostrarListaJugadores(controlador.getServicioConexion(),
                controlador.getJugadorLocal());
        }
    }

    // ========== IMPLEMENTACIÓN DE IOBSERVER (PATRÓN OBSERVER) ==========

    /**
     * Método del patrón Observer que recibe notificaciones del modelo EstadoLocalJuego.
     * Este método es llamado automáticamente cuando el estado del juego cambia.
     *
     * @param evento El tipo de evento que ocurrió (ej: "TURNO_CAMBIADO", "TIEMPO_ACTUALIZADO")
     * @param datos Datos adicionales relacionados con el evento
     */
    @Override
    public void notificar(String evento, String datos) {
        SwingUtilities.invokeLater(() -> {
            switch (evento) {
                case "TURNO_CAMBIADO":
                    log("Notificación Observer: Turno cambió - " + datos);
                    // La vista ya recibe actualizarTurno() del controlador
                    // Esta notificación es adicional para logging o efectos visuales
                    break;

                case "TIEMPO_ACTUALIZADO":
                    // El tiempo se actualiza automáticamente por el controlador
                    // Aquí podríamos agregar efectos visuales adicionales
                    break;

                case "TABLEROS_ACTUALIZADOS":
                    log("Notificación Observer: Tableros actualizados");
                    break;

                case "DISPARO_REALIZADO":
                    log("Notificación Observer: " + datos);
                    break;

                case "PARTIDA_FINALIZADA":
                    log("Notificación Observer: Partida finalizada - " + datos);
                    break;

                default:
                    log("Notificación Observer desconocida: " + evento);
                    break;
            }
        });
    }
}