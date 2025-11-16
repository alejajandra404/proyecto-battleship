package views;

import controllers.ControladorJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vista visual con drag-and-drop para colocación de naves en modo multijugador
 * Permite al jugador arrastrar y soltar sus naves en el tablero
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

    // Componentes UI
    private JLabel lblTitulo;
    private JLabel lblInstrucciones;
    private JTextArea txtLog;
    private JButton btnListo;
    private JButton btnReiniciar;
    private JPanel panelTablero;
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
    private static final Color COLOR_NAVE = new Color(128, 128, 128);
    private static final Color COLOR_VALIDO = new Color(50, 205, 50, 100);
    private static final Color COLOR_INVALIDO = new Color(220, 20, 60, 100);
    private static final Color COLOR_HOVER = new Color(173, 216, 230);

    /**
     * Clase interna para representar una casilla del tablero
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
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        }

        public void setOcupada(boolean ocupada) {
            this.ocupada = ocupada;
            setBackground(ocupada ? COLOR_NAVE : COLOR_AGUA);
        }

        public void setHover(boolean hover, boolean valido) {
            this.hover = hover;
            if (hover) {
                setBackground(valido ? COLOR_VALIDO : COLOR_INVALIDO);
            } else {
                setBackground(ocupada ? COLOR_NAVE : COLOR_AGUA);
            }
        }

        public boolean isOcupada() {
            return ocupada;
        }

        public int getFila() {
            return fila;
        }

        public int getColumna() {
            return columna;
        }
    }

    /**
     * Clase interna para representar una nave visual arrastrable
     */
    private class NaveVisual extends JPanel {
        private TipoNave tipo;
        private int tamano;
        private boolean horizontal = true;
        private boolean colocada = false;
        private String nombre;

        public NaveVisual(String nombre, TipoNave tipo, int tamano) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.tamano = tamano;

            setLayout(new GridLayout(1, tamano, 2, 2));
            setBackground(new Color(240, 248, 255));
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));

            actualizarVisual();
            agregarListeners();
        }

        private void actualizarVisual() {
            removeAll();
            setLayout(horizontal ? new GridLayout(1, tamano, 2, 2) : new GridLayout(tamano, 1, 2, 2));

            for (int i = 0; i < tamano; i++) {
                JPanel segmento = new JPanel();
                segmento.setBackground(COLOR_NAVE);
                segmento.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                segmento.setPreferredSize(new Dimension(TAMANO_CASILLA - 10, TAMANO_CASILLA - 10));
                add(segmento);
            }

            revalidate();
            repaint();
        }

        private void agregarListeners() {
            // Mouse listener para iniciar drag
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!colocada) {
                        naveArrastrada = NaveVisual.this;
                        puntoInicial = e.getPoint();
                        posicionOriginal = getLocation();
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (naveArrastrada == NaveVisual.this) {
                        intentarColocarNave(e);
                        setCursor(Cursor.getDefaultCursor());
                        naveArrastrada = null;
                        puntoInicial = null;
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!colocada && SwingUtilities.isRightMouseButton(e)) {
                        rotar();
                    }
                }
            });

            // Mouse motion listener para drag
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (naveArrastrada == NaveVisual.this && puntoInicial != null) {
                        Point ubicacion = getLocation();
                        int x = ubicacion.x + e.getX() - puntoInicial.x;
                        int y = ubicacion.y + e.getY() - puntoInicial.y;
                        setLocation(x, y);

                        // Actualizar preview en el tablero
                        actualizarPreviewTablero(e);
                    }
                }
            });
        }

        public void rotar() {
            horizontal = !horizontal;
            actualizarVisual();
            log(nombre + " rotado a orientación " + (horizontal ? "horizontal" : "vertical"));
        }

        public boolean isHorizontal() {
            return horizontal;
        }

        public TipoNave getTipo() {
            return tipo;
        }

        public int getTamano() {
            return tamano;
        }

        public void setColocada(boolean colocada) {
            this.colocada = colocada;
            if (colocada) {
                setVisible(false);
            }
        }

        public void resetear() {
            colocada = false;
            setVisible(true);
            if (posicionOriginal != null) {
                setLocation(posicionOriginal);
            }
        }

        public String getNombre() {
            return nombre;
        }
    }

    /**
     * Clase interna para almacenar información de una nave colocada
     */
    private class NaveColocada {
        NaveVisual naveVisual;
        int fila;
        int columna;

        public NaveColocada(NaveVisual naveVisual, int fila, int columna) {
            this.naveVisual = naveVisual;
            this.fila = fila;
            this.columna = columna;
        }
    }

    /**
     * Constructor
     */
    public VistaColocacionNavesVisual(JugadorDTO jugadorLocal, JugadorDTO oponente,
                                     ControladorJuego controlador) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.controlador = controlador;
        this.navesColocadas = new ArrayList<>();

        // Registrar esta vista en el controlador
        controlador.setVistaColocacion(this);

        inicializarComponentes();
        log("Bienvenido " + jugadorLocal.getNombre());
        log("Arrastra las naves al tablero. Clic derecho para rotar.");
        log("REGLA: Las naves NO pueden estar contiguas (deben tener agua alrededor).");
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

        lblTitulo = new JLabel("COLOCACIÓN DE NAVES - Drag & Drop", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(0, 51, 102));

        JLabel lblJugadores = new JLabel(
            jugadorLocal.getNombre() + " vs " + oponente.getNombre(),
            SwingConstants.CENTER
        );
        lblJugadores.setFont(new Font("Arial", Font.PLAIN, 16));

        lblInstrucciones = new JLabel(
            "Arrastra las naves al tablero | Clic derecho para rotar | Las naves NO pueden estar contiguas (deben tener agua alrededor)",
            SwingConstants.CENTER
        );
        lblInstrucciones.setFont(new Font("Arial", Font.ITALIC, 11));

        panelSuperior.add(lblTitulo);
        panelSuperior.add(lblJugadores);
        panelSuperior.add(lblInstrucciones);

        // Panel central con tablero y naves
        JPanel panelCentral = new JPanel(new BorderLayout(20, 0));
        panelCentral.setOpaque(false);

        // Tablero
        panelTablero = crearPanelTablero();
        JPanel contenedorTablero = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedorTablero.setOpaque(false);
        contenedorTablero.add(panelTablero);

        // Panel de naves disponibles
        panelNaves = crearPanelNaves();

        panelCentral.add(contenedorTablero, BorderLayout.CENTER);
        panelCentral.add(panelNaves, BorderLayout.EAST);

        // Panel inferior con log y botones
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);

        // Log
        txtLog = new JTextArea(5, 60);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtLog.setBackground(Color.BLACK);
        txtLog.setForeground(Color.GREEN);
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(BorderFactory.createTitledBorder("Registro"));

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.setOpaque(false);

        btnReiniciar = new JButton("Reiniciar Naves");
        btnReiniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnReiniciar.setBackground(new Color(220, 20, 60));
        btnReiniciar.setForeground(Color.WHITE);
        btnReiniciar.setFocusPainted(false);
        btnReiniciar.addActionListener(e -> reiniciarNaves());

        btnListo = new JButton("¡Listo! Enviar al Servidor");
        btnListo.setFont(new Font("Arial", Font.BOLD, 14));
        btnListo.setBackground(new Color(34, 139, 34));
        btnListo.setForeground(Color.WHITE);
        btnListo.setFocusPainted(false);
        btnListo.setEnabled(false);
        btnListo.addActionListener(e -> enviarNaves());

        panelBotones.add(btnReiniciar);
        panelBotones.add(btnListo);

        panelInferior.add(scrollLog, BorderLayout.CENTER);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        // Agregar componentes
        add(panelSuperior, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }

    /**
     * Crea el panel del tablero con grid 10x10
     */
    private JPanel crearPanelTablero() {
        JPanel panel = new JPanel(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 1, 1));
        panel.setBackground(Color.DARK_GRAY);
        panel.setBorder(BorderFactory.createTitledBorder("Tu Tablero"));

        casillas = new CasillaTablero[TAMANO_TABLERO][TAMANO_TABLERO];

        // Esquina superior izquierda
        panel.add(new JLabel(""));

        // Encabezados de columnas (A-J)
        for (int col = 0; col < TAMANO_TABLERO; col++) {
            JLabel lbl = new JLabel(String.valueOf((char) ('A' + col)), SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(lbl);
        }

        // Filas con números y casillas
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            // Número de fila
            JLabel lblFila = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Arial", Font.BOLD, 12));
            panel.add(lblFila);

            // Casillas
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                casillas[fila][col] = new CasillaTablero(fila, col);
                panel.add(casillas[fila][col]);
            }
        }

        return panel;
    }

    /**
     * Crea el panel con las naves disponibles para arrastrar
     */
    private JPanel crearPanelNaves() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Naves Disponibles"));
        panel.setBackground(new Color(240, 248, 255));

        navesDisponibles = new ArrayList<>();

        // Crear naves
        navesDisponibles.add(new NaveVisual("Portaaviones", TipoNave.PORTAAVIONES, 4));
        navesDisponibles.add(new NaveVisual("Crucero", TipoNave.CRUCERO, 3));
        navesDisponibles.add(new NaveVisual("Submarino 1", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Submarino 2", TipoNave.SUBMARINO, 2));
        navesDisponibles.add(new NaveVisual("Barco", TipoNave.BARCO, 1));

        for (NaveVisual nave : navesDisponibles) {
            JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
            contenedor.setOpaque(false);
            contenedor.add(nave);
            panel.add(contenedor);
            panel.add(Box.createVerticalStrut(15));
        }

        return panel;
    }

    /**
     * Actualiza el preview del tablero mientras se arrastra una nave
     */
    private void actualizarPreviewTablero(MouseEvent e) {
        // Limpiar hover anterior
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                casillas[i][j].setHover(false, false);
            }
        }

        if (naveArrastrada == null) return;

        // Convertir coordenadas del mouse a coordenadas del tablero
        Point puntoMouse = SwingUtilities.convertPoint(naveArrastrada, e.getPoint(), panelTablero);
        Component comp = panelTablero.getComponentAt(puntoMouse);

        if (comp instanceof CasillaTablero) {
            CasillaTablero casilla = (CasillaTablero) comp;
            int fila = casilla.getFila();
            int columna = casilla.getColumna();

            boolean valido = validarPosicion(fila, columna, naveArrastrada);

            // Mostrar preview
            for (int i = 0; i < naveArrastrada.getTamano(); i++) {
                int f = naveArrastrada.isHorizontal() ? fila : fila + i;
                int c = naveArrastrada.isHorizontal() ? columna + i : columna;

                if (f >= 0 && f < TAMANO_TABLERO && c >= 0 && c < TAMANO_TABLERO) {
                    casillas[f][c].setHover(true, valido);
                }
            }
        }
    }

    /**
     * Intenta colocar una nave cuando se suelta el mouse
     */
    private void intentarColocarNave(MouseEvent e) {
        // Convertir coordenadas del mouse a coordenadas del tablero
        Point puntoMouse = SwingUtilities.convertPoint(naveArrastrada, e.getPoint(), panelTablero);
        Component comp = panelTablero.getComponentAt(puntoMouse);

        if (comp instanceof CasillaTablero) {
            CasillaTablero casilla = (CasillaTablero) comp;
            int fila = casilla.getFila();
            int columna = casilla.getColumna();

            if (validarPosicion(fila, columna, naveArrastrada)) {
                colocarNave(naveArrastrada, fila, columna);
                log(naveArrastrada.getNombre() + " colocado en " + coordenadaToString(fila, columna));
            } else {
                log("Posición inválida para " + naveArrastrada.getNombre());
                naveArrastrada.setLocation(posicionOriginal);
            }
        } else {
            // No se soltó sobre el tablero, regresar a posición original
            naveArrastrada.setLocation(posicionOriginal);
        }

        // Limpiar preview
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                casillas[i][j].setHover(false, false);
            }
        }
    }

    /**
     * Valida si una nave puede colocarse en la posición especificada
     * Verifica que no haya superposición ni naves adyacentes (incluye diagonales)
     */
    private boolean validarPosicion(int fila, int columna, NaveVisual nave) {
        // Verificar límites del tablero
        if (nave.isHorizontal()) {
            if (columna + nave.getTamano() > TAMANO_TABLERO) return false;
        } else {
            if (fila + nave.getTamano() > TAMANO_TABLERO) return false;
        }

        // Verificar que no haya superposición directa con otras naves
        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;

            if (casillas[f][c].isOcupada()) {
                return false;
            }
        }

        // Verificar que no haya naves adyacentes (incluye diagonales)
        // Direcciones: arriba, abajo, izquierda, derecha, y las 4 diagonales
        int[] offsetsFila = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] offsetsColumna = {-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;

            // Verificar todas las direcciones alrededor de esta casilla
            for (int d = 0; d < 8; d++) {
                int filaVecina = f + offsetsFila[d];
                int columnaVecina = c + offsetsColumna[d];

                // Verificar que esté dentro del tablero
                if (filaVecina >= 0 && filaVecina < TAMANO_TABLERO &&
                    columnaVecina >= 0 && columnaVecina < TAMANO_TABLERO) {

                    // Verificar que no esté ocupada por otra nave
                    if (casillas[filaVecina][columnaVecina].isOcupada()) {
                        // Verificar que no sea parte de la misma nave que estamos colocando
                        boolean esParteDeLaNaveActual = false;
                        for (int j = 0; j < nave.getTamano(); j++) {
                            int fNave = nave.isHorizontal() ? fila : fila + j;
                            int cNave = nave.isHorizontal() ? columna + j : columna;
                            if (filaVecina == fNave && columnaVecina == cNave) {
                                esParteDeLaNaveActual = true;
                                break;
                            }
                        }

                        // Si no es parte de la nave actual, hay una nave adyacente
                        if (!esParteDeLaNaveActual) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * Coloca una nave en el tablero
     */
    private void colocarNave(NaveVisual nave, int fila, int columna) {
        // Marcar casillas como ocupadas
        for (int i = 0; i < nave.getTamano(); i++) {
            int f = nave.isHorizontal() ? fila : fila + i;
            int c = nave.isHorizontal() ? columna + i : columna;
            casillas[f][c].setOcupada(true);
        }

        // Guardar nave colocada
        navesColocadas.add(new NaveColocada(nave, fila, columna));
        nave.setColocada(true);

        // Verificar si todas las naves están colocadas
        verificarNavesCompletas();
    }

    /**
     * Verifica si todas las naves han sido colocadas
     */
    private void verificarNavesCompletas() {
        if (navesColocadas.size() == navesDisponibles.size()) {
            btnListo.setEnabled(true);
            log("¡Todas las naves colocadas! Presiona 'Listo' para enviar al servidor.");
        } else {
            btnListo.setEnabled(false);
        }
    }

    /**
     * Reinicia todas las naves y el tablero
     */
    private void reiniciarNaves() {
        // Limpiar tablero
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                casillas[i][j].setOcupada(false);
            }
        }

        // Resetear naves
        for (NaveVisual nave : navesDisponibles) {
            nave.resetear();
        }

        navesColocadas.clear();
        btnListo.setEnabled(false);
        log("Naves reiniciadas. Comienza de nuevo.");
    }

    /**
     * Envía las naves colocadas al servidor
     */
    private void enviarNaves() {
        if (navesColocadas.size() != navesDisponibles.size()) {
            mostrarError("Debes colocar todas las naves antes de enviar");
            return;
        }

        List<NaveDTO> naves = new ArrayList<>();

        for (NaveColocada nc : navesColocadas) {
            NaveDTO nave = new NaveDTO();
            nave.setTipo(nc.naveVisual.getTipo());
            nave.setLongitudTotal(nc.naveVisual.getTamano());
            nave.setEstado(EstadoNave.INTACTA);
            nave.setImpactosRecibidos(0);
            nave.setOrientacion(nc.naveVisual.isHorizontal() ?
                OrientacionNave.HORIZONTAL : OrientacionNave.VERICAL);

            // Crear coordenadas
            List<CoordenadaDTO> listaCoordenadas = new ArrayList<>();
            for (int i = 0; i < nc.naveVisual.getTamano(); i++) {
                int fila = nc.naveVisual.isHorizontal() ? nc.fila : nc.fila + i;
                int columna = nc.naveVisual.isHorizontal() ? nc.columna + i : nc.columna;
                listaCoordenadas.add(new CoordenadaDTO(fila, columna));
            }

            CoordenadaDTO[] coordenadas = listaCoordenadas.toArray(new CoordenadaDTO[0]);
            nave.setCoordenadas(coordenadas);

            naves.add(nave);
        }

        log("Enviando " + naves.size() + " naves al servidor...");
        controlador.enviarNavesColocadas(naves);

        btnListo.setEnabled(false);
        btnReiniciar.setEnabled(false);
    }

    /**
     * Convierte coordenadas a string (ej: A1)
     */
    private String coordenadaToString(int fila, int columna) {
        return String.valueOf((char) ('A' + columna)) + (fila + 1);
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

    // Implementación de IVistaColocacionNaves

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

    @Override
    public void navesColocadas() {
        SwingUtilities.invokeLater(() -> {
            log("Naves enviadas correctamente al servidor");
        });
    }

    @Override
    public void esperandoOponente() {
        SwingUtilities.invokeLater(() -> {
            log("Esperando a que el oponente coloque sus naves...");
            lblInstrucciones.setText("Esperando al oponente...");
        });
    }

    @Override
    public void iniciarJuego() {
        SwingUtilities.invokeLater(() -> {
            log("¡Ambos jugadores listos! Iniciando batalla...");
            // El flujo cambiará automáticamente a la vista de juego
            FlujoVista.mostrarJuegoMultiplayer(jugadorLocal, oponente, controlador);
        });
    }
}
