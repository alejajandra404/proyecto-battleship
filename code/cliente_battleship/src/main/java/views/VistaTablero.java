package views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import static views.ConstantesVista.*;

/**
 * Clase que representa un tablero de la vista.
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public abstract class VistaTablero extends javax.swing.JPanel {
    // Arreglo de celdas. Es heredado por cada tablero hijo.
    protected JButton[][] celdas;
    /**
     * Constructor
     */
    public VistaTablero() {
        setLayout(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 2, 2));
        setBackground(COLOR_BORDE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        crearTablero();
    }
    /**
     * Construye el maquetado del tablero.
     */
    private void crearTablero(){
        // Arreglo de botones que posteriormente se agregan como las celdas del tablero
        JButton[][] botones = new JButton[TAMANO_TABLERO][TAMANO_TABLERO];
        // Título vacío
        JLabel lblVacio = new JLabel("");
        lblVacio.setOpaque(true);
        lblVacio.setBackground(COLOR_AGUA_OSCURO);
        add(lblVacio);
        // Añade las letras a cada columna
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            JLabel lblCol = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lblCol.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblCol.setForeground(Color.WHITE);
            lblCol.setOpaque(true);
            lblCol.setBackground(COLOR_AGUA_OSCURO);
            add(lblCol);
        }
        // Añade los números de cada fila
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            JLabel lblFila = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFila.setForeground(Color.WHITE);
            lblFila.setOpaque(true);
            lblFila.setBackground(COLOR_AGUA_OSCURO);
            add(lblFila);
            // Añade las casillas por cada fila (cada casilla es un botón)
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                JButton btnCelda = crearBotonCelda();
                botones[fila][col] = btnCelda;
                add(btnCelda);
            }
        }
        this.celdas = botones;
    }
    /**
     * Crea cada celda del tablero como un botón.
     * @return Botón que representa una celda.
     */
    private JButton crearBotonCelda() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(TAMANO_CELDA, TAMANO_CELDA));
        btn.setBackground(COLOR_VACIO);
        btn.setOpaque(true);
        btn.setBorder(new LineBorder(COLOR_AGUA, 1));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setEnabled(false);
        
        return btn;
    }
    
}