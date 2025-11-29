package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
/**
 * Vista visual con drag-and-drop para colocación de naves en modo multijugador.
 * Combina la estética original (casillas de colores sólidos) con el motor de 
 * renderizado de imágenes para las naves.
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaColocacionNavesVisual extends JPanel implements ControladorJuego.IVistaColocacionNaves {

    private final ControladorJuego controlador;
    private final JugadorDTO jugadorLocal;
    private final JugadorDTO oponente;

    // Recursos gráficos
    private String sufijoColor;

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblInstrucciones;
    private JTextArea txtLog;
    private JButton btnListo;
    private JButton btnReiniciar;
    
    // Usamos el panel personalizado para pintar imágenes de naves
    private PanelTableroConImagenes panelTablero; 
    private JPanel panelNaves;

    // Grid del tablero
    private CasillaTablero[][] casillas;
    private static final int TAMANO_TABLERO = 10;
    private static final int TAMANO_CASILLA = 40;

    // Naves
    private List<NaveVisual> navesDisponibles;
    private List<NaveColocada> navesColocadas;

    // Drag and drop
    private NaveVisual naveArrastrada = null;
    private Point puntoInicial = null;
    private Point posicionOriginal = null;

    // Colores
    private static final Color COLOR_AGUA = new Color(100, 149, 237);
    private static final Color COLOR_VALIDO = new Color(50, 205, 50, 100);
    private static final Color COLOR_INVALIDO = new Color(220, 20, 60, 100);
    private static final Color COLOR_FONDO_TABLERO = new Color(9, 117, 197);
    private static final Color COLOR_TITLE_BORDER = new Color(162, 212, 248);
    private static final Color COLOR_BORDE_TABLERO = new Color(134, 74, 52);

    /**
     * Pinta las naves por encima de la cuadrícula
     */
    private class PanelTableroConImagenes extends JPanel {
        
        // Variables para ajustar la posición de las naves en el tablero
        private final int AJUSTE_X = 2;   
        private final int AJUSTE_Y = 18;  

        public PanelTableroConImagenes(LayoutManager layout) {
            super(layout);
        }

        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

            for (NaveColocada nc : navesColocadas) {
                BufferedImage img = nc.naveVisual.getImagenActual();
                if (img != null) {
                    int x = ((nc.columna + 1) * TAMANO_CASILLA) + AJUSTE_X;
                    int y = ((nc.fila + 1) * TAMANO_CASILLA) + AJUSTE_Y;
                    
                    int ancho = nc.naveVisual.isHorizontal() ? nc.naveVisual.getTamano() * TAMANO_CASILLA : TAMANO_CASILLA;
                    int alto = nc.naveVisual.isHorizontal() ? TAMANO_CASILLA : nc.naveVisual.getTamano() * TAMANO_CASILLA;

                    if (!nc.naveVisual.isHorizontal()) {
                        java.awt.geom.AffineTransform backup = g2d.getTransform();
                        g2d.translate(x + ancho / 2.0, y + alto / 2.0);
                        g2d.rotate(Math.toRadians(90));
                        g2d.drawImage(img, -alto / 2, -ancho / 2, alto, ancho, null);
                        g2d.setTransform(backup);
                    } else {
                        g2d.drawImage(img, x, y, ancho, alto, null);
                    }
                }
            }
        }
    }

    /**
     * CasillaTablero: Usa colores sólidos de fondo y bordes
     */
    private class CasillaTablero extends JPanel {
        private int fila;
        private int columna;
        private boolean ocupada = false;
        private boolean hover = false;

        public CasillaTablero(int fila, int columna) {
            this.fila = fila;
            this.columna = columna;
            setPreferredSize(new Dimension(TAMANO_CASILLA, TAMANO_CASILLA));
            setBackground(COLOR_AGUA);
            setBorder(BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 2));
        }
        
        public void setOcupada(boolean ocupada) {
            this.ocupada = ocupada;
            if (!hover) {
                setBackground(COLOR_AGUA);
            }
        }

        public void setHover(boolean hover, boolean valido) {
            this.hover = hover;
            if (hover) {
                setBackground(valido ? COLOR_VALIDO : COLOR_INVALIDO);
            } else {
                setBackground(COLOR_AGUA);
            }
        }

        public boolean isOcupada() { return ocupada; }
        public int getFila() { return fila; }
        public int getColumna() { return columna; }
    }

    /**
     * NaveVisual: Carga imágenes (igual que antes)
     */
    private class NaveVisual extends JPanel {
        private TipoNave tipo;
        private int tamano;
        private boolean horizontal = true;
        private boolean colocada = false;
        private String nombre;
        private BufferedImage imagenNave;
        private JComponent contenedorPadreOriginal;
        private JRootPane contenedorRaiz;
        
        private int mouseX;
        private int mouseY;
        
        public NaveVisual(String nombre, TipoNave tipo, int tamano) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.tamano = tamano;
            cargarImagenNave();
            
            setOpaque(false);
            setBackground(null);
            actualizarDimensiones();
            agregarListeners();
//            setBorder(new LineBorder(Color.BLACK, 2));
        }

        @Override
        public void addNotify() {
            super.addNotify();
            mouseX = getLocation().x;
            mouseY = getLocation().y;
        }
        
        private void cargarImagenNave() {
            String nombreArchivo = "";
            switch (tipo) {
                case PORTAAVIONES: nombreArchivo = "portaaviones" + sufijoColor + ".png"; break;
                case CRUCERO:      nombreArchivo = "crucero" + sufijoColor + ".png"; break;
                case SUBMARINO:    nombreArchivo = "submarino" + sufijoColor + ".png"; break;
                case BARCO:        nombreArchivo = "barco" + sufijoColor + ".png"; break; 
                default:           nombreArchivo = "barco" + sufijoColor + ".png"; break;
            }

            try {
                URL url = getClass().getResource("/" + nombreArchivo);
                if(url == null) url = getClass().getResource("imgs/" + nombreArchivo);
                
                if (url != null) {
                    imagenNave = ImageIO.read(url);
                } else {
                    System.err.println("No se encontró imagen: " + nombreArchivo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void actualizarDimensiones() {
            int w = tamano * TAMANO_CASILLA;
            int h = TAMANO_CASILLA;
            if (!horizontal) {
                w = TAMANO_CASILLA;
                h = tamano * TAMANO_CASILLA;
            }
            setPreferredSize(new Dimension(w, h));
            setSize(new Dimension(w, h));
            revalidate();
            repaint();
        }
        
        public BufferedImage getImagenActual() { return imagenNave; }

        @Override
        protected void paintComponent(Graphics g) {
            if (imagenNave != null) {
                Graphics2D g2d = (Graphics2D) g;

                // Mejor calidad visual al rotar
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (horizontal) {
                    // --- CASO 1: HORIZONTAL (Original) ---
                    // Se dibuja tal cual, llenando todo el componente
                    g2d.drawImage(imagenNave, 0, 0, getWidth(), getHeight(), this);

                } else {
                    // --- CASO 2: VERTICAL (Rotado 90 grados) ---
                    // Guardamos la transformación original para no afectar otros dibujos
                    AffineTransform backup = g2d.getTransform();

                    // 1. Movemos el punto de origen (0,0) al CENTRO EXACTO del componente actual
                    g2d.translate(getWidth() / 2.0, getHeight() / 2.0);

                    // 2. Rotamos 90 grados alrededor de ese centro
                    g2d.rotate(Math.toRadians(90));

                    // 3. Dibujamos la imagen "Centrada" en el nuevo origen.
                    // IMPORTANTE: Como hemos rotado 90 grados:
                    // - La longitud de la imagen será igual al ALTO (Height) del componente.
                    // - El grosor de la imagen será igual al ANCHO (Width) del componente.
                    // Por eso invertimos getHeight() y getWidth() en los parámetros de drawImage.
                    int w = getWidth();
                    int h = getHeight();

                    // Coordenadas: (-h/2, -w/2) para que el centro de la imagen quede en el origen (0,0)
                    g2d.drawImage(imagenNave, -h / 2, -w / 2, h, w, this);

                    // Restauramos la configuración original
                    g2d.setTransform(backup);
                }
            } else {
                // Fallback visual
                g.setColor(Color.GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.BLACK);
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
        }

        private void agregarListeners() {
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && !colocada) {
                        naveArrastrada = NaveVisual.this;
                        puntoInicial = e.getPoint();
                        posicionOriginal = getLocation();
                        SwingUtilities.getWindowAncestor(NaveVisual.this).setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        
                        // 1.- Guarda el contenedor padre
                        contenedorPadreOriginal = (JComponent) getParent();

                        // 2.- Obtiene el contenedor raíz
                        contenedorRaiz = naveArrastrada.getRootPane();

                        // 3.- Obtiene el punto relativo al contenedor padre
                        Point ubicacionActual = SwingUtilities.convertPoint(contenedorPadreOriginal, naveArrastrada.getLocation(), contenedorRaiz);

                        // 4.- Quita el componente de su padre original y lo repinta
                        contenedorPadreOriginal.remove(naveArrastrada);
                        contenedorPadreOriginal.repaint();

                        // 5.- Agrega el componente al contenedor raíz
                        if (contenedorRaiz != null) {
                            contenedorRaiz.getLayeredPane().add(naveArrastrada, JLayeredPane.DRAG_LAYER);
                        }

                        // 5. Establece la nueva ubicación y hace que el nuevo padre lo repinte
                        naveArrastrada.setLocation(ubicacionActual);
                        naveArrastrada.repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) && naveArrastrada == NaveVisual.this) {
                        intentarColocarNave(e);
                        SwingUtilities.getWindowAncestor(NaveVisual.this).setCursor(Cursor.getDefaultCursor());
                        naveArrastrada = null;
                        puntoInicial = null;
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // Agregamos 'e' como argumento a rotar
                    if (naveArrastrada == NaveVisual.this && !colocada && SwingUtilities.isRightMouseButton(e)) {
                        rotar(e);
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (naveArrastrada == NaveVisual.this && puntoInicial != null) {
                        // 1. Obtenemos la posición actual de la esquina del componente
                        Point ubicacion = getLocation();
                        
                        mouseX = e.getX();
                        mouseY = e.getY();
                        
                        // 2. Calculamos la posición del mouse respecto al PADRE (Contenedor).
                        // Sumamos la posición del componente (ubicacion.x) + la posición local del mouse (e.getX())
                        int mouseX_EnParent = ubicacion.x + e.getX();
                        int mouseY_EnParent = ubicacion.y + e.getY();

                        // 3. Restamos la mitad del ancho/alto para que el CENTRO quede en el mouse
                        int nuevoX = mouseX_EnParent - (getWidth() / 2);
                        int nuevoY = mouseY_EnParent - (getHeight() / 2);

                        // 4. Aplicamos la nueva ubicación
                        setLocation(nuevoX, nuevoY);

                        // Refrescar la interfaz
                        actualizarPreviewTablero(e);
                        if (getParent() != null)
                            getParent().repaint();
                    }
                }
            });
        }

        public void rotar(MouseEvent e) {
            // 1. CALCULAR EL PIVOTE (Posición absoluta del mouse en el contenedor padre)
            // Usamos e.getX() y e.getY() porque son las coordenadas frescas del clic actual.
            Point ubicacionActual = getLocation();
            int pivoteX = ubicacionActual.x + e.getX();
            int pivoteY = ubicacionActual.y + e.getY();

            // 2. CAMBIAR ORIENTACIÓN Y DIMENSIONES
            horizontal = !horizontal;

            // Intercambiamos ancho y alto
            int w = getWidth();
            int h = getHeight();

            setSize(h, w);
            setPreferredSize(new Dimension(h, w));

            // 3. REUBICAR CENTRADO EN EL PIVOTE
            // Ahora que el componente tiene nuevas dimensiones, calculamos dónde debe ir 
            // su esquina superior izquierda para que su centro coincida con el pivote.
            int nuevoX = pivoteX - (getWidth() / 2);
            int nuevoY = pivoteY - (getHeight() / 2);

            setLocation(nuevoX, nuevoY);

            // 4. ACTUALIZAR VARIABLES INTERNAS (CRUCIAL)
            // Como hemos forzado que el mouse esté en el centro exacto del componente,
            // debemos actualizar las variables mouseX y mouseY para que el próximo 
            // arrastre ('drag') sea suave y no dé saltos.
            this.mouseX = getWidth() / 2;
            this.mouseY = getHeight() / 2;

            // 5. Refrescar visuales
            revalidate();
            repaint();
            if (getParent() != null) {
                getParent().repaint();
            }

            // (Opcional) Log
            log(nombre + " rotado a " + (horizontal ? "horizontal" : "vertical"));
        }
        
        public boolean isHorizontal() { return horizontal; }
        public TipoNave getTipo() { return tipo; }
        public int getTamano() { return tamano; }
        public String getNombre() { return nombre; }
        public void setMouseX(int mouseX) {this.mouseX = mouseX;}
        public void setMouseY(int mouseY) {this.mouseY = mouseY;}
        
        public void setColocada(boolean colocada) {
            this.colocada = colocada;
            setVisible(!colocada);
        }

        public void resetear() {
            colocada = false;
            setVisible(true);
            horizontal = true;
            actualizarDimensiones();
            if(contenedorPadreOriginal != null && !colocada){
                // 1.- Se obtiene el contenedor actual, y se castea como LayeredPane
                JLayeredPane currentParent = (JLayeredPane) getParent();
                
                NaveVisual actual = NaveVisual.this;
                
                // 2.- Se obtiene la ubicación relativa al contenedor original
                Point currentLoc = SwingUtilities.convertPoint(currentParent, actual.getLocation(), contenedorPadreOriginal);

                // 3.- Se quita el componente del contenedor actual y se repinta
                currentParent.remove(actual);
                currentParent.repaint();

                // 4.- Se agrega el componente a su contenedor original
                contenedorPadreOriginal.add(actual);
                contenedorPadreOriginal.revalidate();

                // 5.- Se posiciona el componente en su ubicación original y se repinta
                actual.setLocation(currentLoc);
                actual.revalidate();
                actual.repaint();

                // 6.- Se resetea el contenedor padre original
                contenedorPadreOriginal = null;
            }
        }
    }

    private class NaveColocada {
        NaveVisual naveVisual;
        int fila, columna;
        public NaveColocada(NaveVisual naveVisual, int fila, int columna) {
            this.naveVisual = naveVisual;
            this.fila = fila;
            this.columna = columna;
        }
    }

    /**
     * Constructor
     */
    public VistaColocacionNavesVisual(JugadorDTO jugadorLocal, JugadorDTO oponente, ControladorJuego controlador) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.controlador = controlador;
        this.navesColocadas = new ArrayList<>();

        determinarSufijoColor();
        
        controlador.setVistaColocacion(this);

        inicializarComponentes();
        log("Bienvenido " + jugadorLocal.getNombre());
        log("Arrastra las naves al tablero. Clic derecho para rotar.");
    }
    
    private void determinarSufijoColor() {
        Color c = jugadorLocal.getColor();
        if (c != null && c.getRed() > 200 && c.getBlue() < 100) {
            this.sufijoColor = "_rojo";
        } else {
            this.sufijoColor = "_azul";
        }
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 248, 255));

        // Panel superior
        JPanel panelSuperior = new JPanel(new GridLayout(3, 1, 5, 5));
        panelSuperior.setOpaque(false);

        lblTitulo = new JLabel("COLOCACIÓN DE NAVES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(new Color(0, 51, 102));

        JLabel lblJugadores = new JLabel(jugadorLocal.getNombre() + " vs " + oponente.getNombre(), SwingConstants.CENTER);
        lblJugadores.setFont(new Font("Arial", Font.PLAIN, 16));

        lblInstrucciones = new JLabel("Arrastra las naves | Clic derecho rotar | Separadas por agua", SwingConstants.CENTER);
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 11));

        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblJugadores);
        panelSuperior.add(lblInstrucciones);

        // Panel central
        JPanel panelCentral = new JPanel(new BorderLayout(20, 0));
        panelCentral.setOpaque(false);

        // Tablero
        panelTablero = crearPanelTablero();
        JPanel contenedorTablero = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedorTablero.setOpaque(false);
        contenedorTablero.add(panelTablero);

        panelNaves = crearPanelNaves();

        panelCentral.add(contenedorTablero, BorderLayout.CENTER);
        panelCentral.add(panelNaves, BorderLayout.EAST);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);

        txtLog = new JTextArea(5, 60);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro"));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelBotones.setOpaque(false);

        btnReiniciar = new JButton("Reiniciar Naves");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReiniciar.setBackground(new Color(220, 20, 60));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.addActionListener(e -> reiniciarNaves());

        btnListo = new JButton("¡Listo! Enviar al Servidor");
        btnListo.setFont(new Font("Arial", Font.BOLD, 14));
        btnListo.setBackground(new Color(34, 139, 34));
        btnListo.setForeground(Color.WHITE);
        btnListo.setEnabled(false);
        btnListo.addActionListener(e -> enviarNaves());

        panelBotones.add(btnReiniciar);
        panelBotones.add(btnListo);

        panelInferior.add(scrollLog, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    private PanelTableroConImagenes crearPanelTablero() {
        PanelTableroConImagenes panel = new PanelTableroConImagenes(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 0, 0));
        panel.setBackground(COLOR_FONDO_TABLERO);
        
        Border bordeAzulTablero = BorderFactory.createLineBorder(COLOR_TITLE_BORDER, 1);
        Border bordeExterior = BorderFactory.createLineBorder(COLOR_BORDE_TABLERO, 2);
        TitledBorder bordeTitulo = BorderFactory.createTitledBorder(bordeAzulTablero, "Tu Tablero");
        bordeTitulo.setTitleColor(Color.WHITE);
        
        panel.setBorder(BorderFactory.createCompoundBorder(bordeExterior, bordeTitulo));

        casillas = new CasillaTablero[TAMANO_TABLERO][TAMANO_TABLERO];

        // Headers
        panel.add(new JLabel(""));
        for (int col = 0; col < TAMANO_TABLERO; col++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 15));
            lbl.setForeground(Color.WHITE);
            panel.add(lbl);
        }

        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            JLabel lblFila = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 15));
            lblFila.setForeground(Color.WHITE);
            panel.add(lblFila);
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                casillas[fila][col] = new CasillaTablero(fila, col);
                panel.add(casillas[fila][col]);
            }
        }
        return panel;
    }

    private JPanel crearPanelNaves() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Naves Disponibles"));
        panel.setBackground(new Color(240, 248, 255));

        navesDisponibles = new ArrayList<>();
        navesDisponibles.add(new NaveVisual("Portaaviones 1", TipoNave.PORTAAVIONES, 4));
        navesDisponibles.add(new NaveVisual("Portaaviones 2", TipoNave.PORTAAVIONES, 4));
        navesDisponibles.add(new NaveVisual("Crucero 1", TipoNave.CRUCERO, 3));
        navesDisponibles.add(new NaveVisual("Crucero 2", TipoNave.CRUCERO, 3));
        navesDisponibles.add(new NaveVisual("Submarino 1", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Submarino 2", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Submarino 3", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Submarino 4", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Barco 1", TipoNave.BARCO, 1));
        navesDisponibles.add(new NaveVisual("Barco 2", TipoNave.BARCO, 1));
        navesDisponibles.add(new NaveVisual("Barco 3", TipoNave.BARCO, 1));

        for (NaveVisual nave : navesDisponibles) {
            JPanel c = new JPanel(new FlowLayout(FlowLayout.CENTER));
            c.setOpaque(false);
            c.add(nave);
            panel.add(c);
//            panel.add(Box.createVerticalStrut(10));
        }
        return panel;
    }

    private void actualizarPreviewTablero(MouseEvent e) {
        // Limpiar hovers
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                casillas[i][j].setHover(false, false);
            }
        }
        if (naveArrastrada == null) return;

        Point p = SwingUtilities.convertPoint(naveArrastrada, e.getPoint(), panelTablero);
        Component c = panelTablero.getComponentAt(p);

        if (c instanceof CasillaTablero) {
            CasillaTablero casilla = (CasillaTablero) c;
            int fila = casilla.getFila();
            int columna = casilla.getColumna();
            boolean valido = validarPosicion(fila, columna, naveArrastrada);

            for (int i = 0; i < naveArrastrada.getTamano(); i++) {
                int f = naveArrastrada.isHorizontal() ? fila : fila + i;
                int col = naveArrastrada.isHorizontal() ? columna + i : columna;
                if (f < TAMANO_TABLERO && col < TAMANO_TABLERO) {
                    casillas[f][col].setHover(true, valido);
                }
            }
        }
    }

    private void intentarColocarNave(MouseEvent e) {
        Point p = SwingUtilities.convertPoint(naveArrastrada, e.getPoint(), panelTablero);
        Component c = panelTablero.getComponentAt(p);

        if (c instanceof CasillaTablero) {
            CasillaTablero casilla = (CasillaTablero) c;
            int fila = casilla.getFila();
            int columna = casilla.getColumna();

            if (validarPosicion(fila, columna, naveArrastrada)) {
                colocarNave(naveArrastrada, fila, columna);
                log(naveArrastrada.getNombre() + " colocado en " + coordenadaToString(fila, columna));
            } else {
                naveArrastrada.resetear();
                log("Posición inválida (regla de adyacencia)");
            }
        } else {
            naveArrastrada.resetear();
        }
        
        for(int i=0; i<TAMANO_TABLERO; i++) for(int j=0; j<TAMANO_TABLERO; j++) casillas[i][j].setHover(false, false);
        panelTablero.repaint(); // Forzar el repintado de imágenes
    }
    
    private boolean validarPosicion(int fila, int columna, NaveVisual nave) {
        if (nave.isHorizontal()) {
            if (columna + nave.getTamano() > TAMANO_TABLERO) return false;
        } else {
            if (fila + nave.getTamano() > TAMANO_TABLERO) return false;
        }

        // Revisar casillas ocupadas
        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;
            if (casillas[f][c].isOcupada()) return false;
        }

        // Revisar adyacentes
        int[] offsetsFila = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] offsetsColumna = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;

            for (int d = 0; d < 8; d++) {
                int filaVecina = f + offsetsFila[d];
                int columnaVecina = c + offsetsColumna[d];

                if (filaVecina >= 0 && filaVecina < TAMANO_TABLERO && columnaVecina >= 0 && columnaVecina < TAMANO_TABLERO) {
                    if (casillas[filaVecina][columnaVecina].isOcupada()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void colocarNave(NaveVisual nave, int fila, int columna) {
        // Marcar lógicamente las casillas
        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;
            casillas[f][c].setOcupada(true);
        }
        navesColocadas.add(new NaveColocada(nave, fila, columna));
        nave.setColocada(true);
        verificarNavesCompletas();
        panelTablero.repaint();
    }

    private void verificarNavesCompletas() {
        if (navesColocadas.size() == navesDisponibles.size()) {
            btnListo.setEnabled(true);
            log("¡Flota lista! Presiona enviar.");
        } else {
            btnListo.setEnabled(false);
        }
    }

    private void reiniciarNaves() {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                casillas[i][j].setOcupada(false);
                casillas[i][j].setHover(false, false); // Resetear color
            }
        }
        for (NaveVisual nave : navesDisponibles) {
            nave.resetear();
        }
        navesColocadas.clear();
        btnListo.setEnabled(false);
        panelTablero.repaint();
        log("Reiniciado.");
    }

    private void enviarNaves() {
        List<NaveDTO> naves = new ArrayList<>();
        for (NaveColocada nc : navesColocadas) {
            NaveDTO nave = new NaveDTO();
            nave.setTipo(nc.naveVisual.getTipo());
            nave.setLongitudTotal(nc.naveVisual.getTamano());
            nave.setEstado(EstadoNave.INTACTA);
            nave.setOrientacion(nc.naveVisual.isHorizontal() ? OrientacionNave.HORIZONTAL : OrientacionNave.VERICAL);
            
            List<CoordenadaDTO> coords = new ArrayList<>();
            for(int i=0; i<nc.naveVisual.getTamano(); i++) {
                coords.add(new CoordenadaDTO(
                    nc.naveVisual.isHorizontal()?nc.fila:nc.fila+i, 
                    nc.naveVisual.isHorizontal()?nc.columna+i:nc.columna
                ));
            }
            nave.setCoordenadas(coords.toArray(new CoordenadaDTO[0]));
            naves.add(nave);
        }
        VistaJuegoMultiplayer.navesParaTransferir = naves;
        controlador.enviarNavesColocadas(naves);
        btnListo.setEnabled(false);
        btnReiniciar.setEnabled(false);
    }
    
    private String coordenadaToString(int fila, int columna) { return String.valueOf((char) ('A' + columna)) + (fila + 1); }

    private void log(String m) { SwingUtilities.invokeLater(() -> txtLog.append(m + "\n")); }

    // Interfaz IVistaColocacionNaves
    @Override public void mostrarMensaje(String m) { log(m); }
    
    @Override public void mostrarError(String e) { JOptionPane.showMessageDialog(this, e); }
    
    @Override public void navesColocadas() { log("Enviado al servidor."); }
    
    @Override public void esperandoOponente() { 
        log("Esperando oponente..."); lblInstrucciones.setText("Esperando oponente..."); }
    
    @Override public void iniciarJuego() { FlujoVista.mostrarJuegoMultiplayer(jugadorLocal, oponente, controlador); }
}