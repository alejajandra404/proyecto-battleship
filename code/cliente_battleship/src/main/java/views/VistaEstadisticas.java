package views;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import mx.itson.utils.dtos.EstadisticaDTO;

/**
 *
 * @author Usuario
 */
public class VistaEstadisticas extends JPanel{
    private JLabel lblTitulo;
    private JLabel lblNombre;
    private JLabel lblResultado;
    private JLabel lblDisparosTotales;
    private JLabel lblAciertos;
    private JLabel lblPrecision;
    private JLabel lblBarcosHundidos;

    public VistaEstadisticas() {
        initComponents();
    }

    private void initComponents() {
        setSize(1080, 720);

        JPanel panel = new JPanel(new GridLayout(7, 1)); 

        // Inicializamos los labels
        lblTitulo = new JLabel("Resumen del Juego", JLabel.CENTER);
        lblNombre = new JLabel("Jugador: ");
        lblResultado = new JLabel("Resultado: ");
        lblDisparosTotales = new JLabel("Disparos Totales: ");
        lblAciertos = new JLabel("Aciertos: ");
        lblPrecision = new JLabel("Precisión: ");
        lblBarcosHundidos = new JLabel("Barcos Hundidos: ");

        panel.add(lblTitulo);
        panel.add(lblNombre);
        panel.add(lblResultado);
        panel.add(lblDisparosTotales);
        panel.add(lblAciertos);
        panel.add(lblPrecision);
        panel.add(lblBarcosHundidos);

        add(panel);
    }

    /**
     * @param stats El objeto con los datos del servidor.
     */
    public void cargarDatos(EstadisticaDTO stats) {
        lblNombre.setText("Jugador: " + stats.getNombreJugador());
        
        if (stats.isEsGanador()) {
            lblResultado.setText("Resultado: ¡VICTORIA!");
            lblResultado.setForeground(java.awt.Color.GREEN);
        } else {
            lblResultado.setText("Resultado: DERROTA");
            lblResultado.setForeground(java.awt.Color.RED);
        }

        lblDisparosTotales.setText("Disparos Totales: " + stats.getTotalDisparos());
        lblAciertos.setText("Aciertos: " + stats.getAciertos());
        
        // Formatear la precisión a 2 decimales
        String precisionStr = String.format("%.2f", stats.getPorcentajePrecision());
        lblPrecision.setText("Precisión: " + precisionStr + "%");
        
        lblBarcosHundidos.setText("Barcos Hundidos: " + stats.getBarcosHundidos());
    }
}