package views;

import controllers.ControladorJuego;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.*;
import java.awt.*;
import mx.itson.utils.dtos.EstadisticaDTO;

/**
 *
 * @author Usuario
 */
public class VistaEstadisticas extends JPanel {
    private JLabel lblTitulo;
    private JLabel lblNombre;
    private JLabel lblResultado;
    private JLabel lblDisparosTotales;
    private JLabel lblAciertos;
    private JLabel lblPrecision;
    private JLabel lblBarcosHundidos;
    private JButton btnVolver;
    private final ControladorJuego controlador;

    public VistaEstadisticas(ControladorJuego controlador, EstadisticaDTO misEstadisticas) {
        this.controlador = controlador;
        initComponents();
        cargarDatos(misEstadisticas);
    }

    private void initComponents() {
        // Configuramos el layout principal de esta vista
        this.setLayout(new BorderLayout());
        
        // Panel central con Grid para organizar los textos verticalmente
        // Agregamos espaciado vertical (vgap) de 10px
        JPanel panelContenido = new JPanel(new GridLayout(8, 1, 0, 10)); 

        // --- DEFINICIÓN DE FUENTES (TAMAÑOS) ---
        Font fuenteTitulo = new Font("Arial", Font.BOLD, 40);      // Muy grande
        Font fuenteResultado = new Font("Arial", Font.BOLD, 32);   // Grande y negrita
        Font fuenteDatos = new Font("Arial", Font.PLAIN, 24);      // Mediana para datos
        Font fuenteBoton = new Font("Arial", Font.BOLD, 20);       // Para el botón

        // --- INICIALIZACIÓN Y ESTILO DE COMPONENTES ---

        // 1. Título
        lblTitulo = new JLabel("Resumen del Juego", SwingConstants.CENTER);
        lblTitulo.setFont(fuenteTitulo);

        // 2. Jugador
        lblNombre = new JLabel("Jugador: ", SwingConstants.CENTER);
        lblNombre.setFont(fuenteDatos);

        // 3. Resultado (Victoria/Derrota)
        lblResultado = new JLabel("Resultado: ", SwingConstants.CENTER);
        lblResultado.setFont(fuenteResultado);

        // 4. Datos numéricos
        lblDisparosTotales = new JLabel("Disparos Totales: ", SwingConstants.CENTER);
        lblDisparosTotales.setFont(fuenteDatos);

        lblAciertos = new JLabel("Aciertos: ", SwingConstants.CENTER);
        lblAciertos.setFont(fuenteDatos);

        lblPrecision = new JLabel("Precisión: ", SwingConstants.CENTER);
        lblPrecision.setFont(fuenteDatos);

        lblBarcosHundidos = new JLabel("Barcos Hundidos: ", SwingConstants.CENTER);
        lblBarcosHundidos.setFont(fuenteDatos);

        // 5. Botón
        btnVolver = new JButton("Volver al Menú");
        btnVolver.setFont(fuenteBoton);
        btnVolver.addActionListener(e -> {
            System.out.println("Regresando al menú...");
            FlujoVista.mostrarListaJugadores(controlador.getServicioConexion(),
                controlador.getJugadorLocal());
        });

        // --- AGREGAR AL PANEL ---
        panelContenido.add(lblTitulo);
        panelContenido.add(lblNombre);
        panelContenido.add(lblResultado);
        panelContenido.add(lblDisparosTotales);
        panelContenido.add(lblAciertos);
        panelContenido.add(lblPrecision);
        panelContenido.add(lblBarcosHundidos);
        panelContenido.add(btnVolver);

        // Agregamos el panel de contenido al centro de la vista
        add(panelContenido, BorderLayout.CENTER);
    }

    /**
     * @param stats El objeto con los datos del servidor.
     */
    public void cargarDatos(EstadisticaDTO stats) {
        lblNombre.setText("Jugador: " + stats.getNombreJugador());
        
        if (stats.isEsGanador()) {
            lblResultado.setText("Resultado: ¡VICTORIA!");
            lblResultado.setForeground(new Color(0, 150, 0)); // Verde oscuro (se lee mejor)
        } else {
            lblResultado.setText("Resultado: DERROTA");
            lblResultado.setForeground(Color.RED);
        }

        lblDisparosTotales.setText("Disparos Totales: " + stats.getTotalDisparos());
        lblAciertos.setText("Aciertos Totales: " + stats.getAciertos());
        lblBarcosHundidos.setText("Barcos Hundidos: " + stats.getBarcosHundidos());
        
        // Formateo simple para que se vea limpio
        lblPrecision.setText("Precisión: " + stats.getPorcentajePrecision() + "%");
    }
}