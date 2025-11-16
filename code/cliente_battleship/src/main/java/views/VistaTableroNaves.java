package views;

import java.awt.Color;
import javax.swing.border.LineBorder;
import static views.ConstantesVista.COLOR_NAVE;
import static views.ConstantesVista.TAMANO_TABLERO;

/**
 * Clase que representa el tablero de naves propios en la vista.
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class VistaTableroNaves extends VistaTablero {
    
    /**
     * Constructor.
     */
    public VistaTableroNaves() {
        super();
    }
    
    // Contendiente a ser modificado considerablemente por Leonardo (caso de uso 2 configurar naves)
    
    /**
     * Marca una nave en el tablero (contendiente a cambiar)
     * @param x Coordenada en x
     * @param y Coordenada en y
     */
    public void marcarNave(int x, int y) {
        if (x >= 0 && x < TAMANO_TABLERO && y >= 0 && y < TAMANO_TABLERO) {
            celdas[x][y].setBackground(COLOR_NAVE);
            celdas[x][y].setBorder(new LineBorder(Color.BLACK, 2));
        }
    }
    
}