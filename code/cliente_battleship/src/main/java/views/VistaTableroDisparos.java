package views;

import dtos.CoordenadaDTO;
import dtos.DisparoDTO;
import dtos.TurnoDTO;
import enums.ResultadoDisparo;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import static views.ConstantesVista.COLOR_AGUA;
import static views.ConstantesVista.COLOR_DISPARO_AGUA;
import static views.ConstantesVista.COLOR_IMPACTO;
import static views.ConstantesVista.COLOR_VACIO;
import static views.ConstantesVista.TAMANO_TABLERO;

/**
 * Clase que representa el tablero de disparos en la vista.
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaTableroDisparos extends VistaTablero {
    // Contenedor del tablero.
    private final VistaDisparos contenedor;
    /**
     * Constructor que recibe el contenedor.
     * @param contenedor Contenedor del tablero.
     */
    public VistaTableroDisparos(VistaDisparos contenedor) {
        super();
        this.contenedor = contenedor;
        añadirFuncionesDisparo();
    }
    /**
     * Añade el listener para disparar la acción del disparo
     * a cada celda del tablero.
     */
    private void añadirFuncionesDisparo(){
        for (int fila = 0; fila < TAMANO_TABLERO; fila++) {
            for (int col = 0; col < TAMANO_TABLERO; col++) {
                añadirFuncionDisparo(fila, col);
            }
        }
    }
    /**
     * Habilita o desabilita el tablero de disparos.
     * Se espera que el contenedor sea el encargado de utilizar
     * este método, dependiendo del turno del jugador.
     * @param habilitar Si es VERDADERO, se habilita el talero; si es FALSO, se deshabilita.
     */
    public void habilitarTableroDisparos(boolean habilitar) {
        for (int i = 0; i < TAMANO_TABLERO; i++) {
            for (int j = 0; j < TAMANO_TABLERO; j++) {
                JButton btn = this.celdas[i][j];
                if (btn.getBackground().equals(COLOR_VACIO) || btn.getBackground().equals(COLOR_AGUA)) {
                    btn.setEnabled(habilitar);
                }
            }
        }
    }
    /**
     * Añade el mecanismo de acción de un disparo a una celda.
     * Agrega el listener para ejecutar el mecanismo y enviar la acción
     * al controlador.
     * @param fila Fila de la celda.
     * @param col Columna de la celda.
     */
    private void añadirFuncionDisparo(int fila, int col){
        // Se obtiene el botón.
        JButton btn = this.celdas[fila][col];
        // Se convierten los valores de la fila y la columna a constantes.
        final int filaFinal = fila;
        final int colFinal = col;
        // Se agrega el listener de realizar disparo a la celda.
        btn.addActionListener(e -> realizarDisparo(filaFinal, colFinal));
        // Se agregan mouse listeners para estética de selección.
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
    }
    
    /**
     * Realiza un disparo en las coordenadas especificadas
     * BAJO ACOPLAMIENTO: Este método solo usa DTOs, NO objetos del modelo.
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
                marcarResultado(fila, col, esImpacto);

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
     * Marca el resultado del disparo en el tablero.
     * @param x Coordenada en X
     * @param y Coordenada en Y
     * @param esImpacto Es posible que se cambie esto debido al tipo de impacto
     */
    private void marcarResultado(int x, int y, boolean esImpacto) {
        JButton btn = this.celdas[x][y];
        if (esImpacto) {
            btn.setBackground(COLOR_IMPACTO);
            btn.setText("X");
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(COLOR_DISPARO_AGUA);
            btn.setText("•");
            btn.setForeground(Color.WHITE);
        }
        btn.setEnabled(false);
    }
}