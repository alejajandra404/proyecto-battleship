package views;

import dtos.CoordenadaDTO;
import dtos.DisparoDTO;
import dtos.TurnoDTO;
import enums.ResultadoDisparo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import static views.ConstantesVista.*;

/**
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaTablero extends javax.swing.JPanel {
    
    private final VistaJuego contenedor;
    
    /**
     * Creates new form VistaTablero
     * @param contenedor
     * @param esTableroDisparos
     */
    public VistaTablero(VistaJuego contenedor, boolean esTableroDisparos) {
        this.contenedor = contenedor;
        crearTablero(esTableroDisparos);
    }
    
    private void crearTablero(boolean esTableroDisparos){
        setLayout(new GridLayout(TAMANO_TABLERO + 1, TAMANO_TABLERO + 1, 2, 2));
        setBackground(COLOR_BORDE);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton[][] botones = new JButton[TAMANO_TABLERO][TAMANO_TABLERO];

        JLabel lblVacio = new JLabel("");
        lblVacio.setOpaque(true);
        lblVacio.setBackground(COLOR_AGUA_OSCURO);
        add(lblVacio);

        for (int i = 0; i < TAMANO_TABLERO; i++) {
            JLabel lblCol = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            lblCol.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblCol.setForeground(Color.WHITE);
            lblCol.setOpaque(true);
            lblCol.setBackground(COLOR_AGUA_OSCURO);
            add(lblCol);
        }

        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            JLabel lblFila = new JLabel(String.valueOf(fila + 1), SwingConstants.CENTER);
            lblFila.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblFila.setForeground(Color.WHITE);
            lblFila.setOpaque(true);
            lblFila.setBackground(COLOR_AGUA_OSCURO);
            add(lblFila);

            for (int col = 0; col < TAMANO_TABLERO; col++) {
                JButton btnCelda = crearBotonCelda(fila, col, esTableroDisparos);
                botones[fila][col] = btnCelda;
                add(btnCelda);
            }
        }

        if (esTableroDisparos) {
            contenedor.setBotonesTableroDisparos(botones);
        } else {
            contenedor.setBotonesTableroPropio(botones);
        }
    }
    
    private JButton crearBotonCelda(int fila, int col, boolean esTableroDisparos) {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(TAMANO_CELDA, TAMANO_CELDA));
        btn.setBackground(COLOR_VACIO);
        btn.setOpaque(true);
        btn.setBorder(new LineBorder(COLOR_AGUA, 1));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));

        if (esTableroDisparos) {
            final int filaFinal = fila;
            final int colFinal = col;

            btn.addActionListener(e -> realizarDisparo(filaFinal, colFinal));

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn.isEnabled() && btn.getBackground().equals(COLOR_VACIO)) {
                        btn.setBackground(COLOR_AGUA);
                        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn.isEnabled() && btn.getBackground().equals(COLOR_AGUA)) {
                        btn.setBackground(COLOR_VACIO);
                    }
                }
            });
        } else {
            btn.setEnabled(false);
        }

        return btn;
    }
    
    /**
     * Realiza un disparo en las coordenadas especificadas
     *
     * BAJO ACOPLAMIENTO: Este método solo usa DTOs, NO objetos del modelo
     */
    private void realizarDisparo(int fila, int col) {
        try {
            // Crear DTO para el disparo (NO se crea objeto Coordenada del modelo)
            CoordenadaDTO coordDTO = new CoordenadaDTO(fila, col);
            DisparoDTO disparoDTO = new DisparoDTO(coordDTO, contenedor.getNombreJugadorActual());

            // Llamar al controlador con DTO
            DisparoDTO resultado = contenedor.getControlador().procesarDisparo(disparoDTO);

            // Procesar resultado usando el DTO
            if (resultado.getResultado() != null) {
                boolean esImpacto = resultado.getResultado() == ResultadoDisparo.IMPACTO; // Duda en si debería de haber un DTO para ResultadoDisparo

                // Actualizar visual
                contenedor.marcarResultadoEnTableroDisparos(fila, col, esImpacto);

                // Mostrar mensaje del DTO
                String coordStr = coordDTO.toStringCoord();
                if (esImpacto) {
                    contenedor.agregarNotificacion("🎯 ¡IMPACTO en " + coordStr + "!");
                    contenedor.mostrarDialogo("¡IMPACTO!", resultado.getMensaje(),
                            JOptionPane.INFORMATION_MESSAGE);
                    // El turno se mantiene automáticamente
                } else {
                    contenedor.agregarNotificacion("💧 Agua en " + coordStr);
                    contenedor.mostrarDialogo("Agua", resultado.getMensaje(),
                            JOptionPane.INFORMATION_MESSAGE);

                    // Actualizar turno después del diálogo
                    TurnoDTO nuevoTurno = contenedor.getControlador().obtenerTurnoActual();
                    contenedor.actualizarTurnoDesdeDTO(nuevoTurno);
                }

            } else {
                // Error en el disparo
                contenedor.agregarNotificacion("❌ " + resultado.getMensaje());
                contenedor.mostrarDialogo("Error", resultado.getMensaje(),
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            contenedor.agregarNotificacion("❌ Error: " + ex.getMessage());
            contenedor.mostrarDialogo("Error", "Error al procesar disparo: " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
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
