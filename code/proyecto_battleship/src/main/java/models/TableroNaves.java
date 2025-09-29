package models;
import enums.EstadoCasilla;
import java.util.Set;
/**
 * TableroNaves.java
 *
 * Clase entidad que hereda de la clase padre Tablero
 * y representa el tablero de naves de un jugador.
 * 
 * @author Leonardo Flores Leyva
 * ID: 00000252390
 * @author Yuri Germán García López
 * ID: 00000252583
 * @author Alejandra García Preciado
 * ID: 00000252444
 * @author Jesús Ernesto López Ibarra
 * ID: 00000252663
 * @author Daniel Miramontes Iribe
 * ID: 00000252801
 *
 */
public class TableroNaves extends Tablero {
    
    private Set<Nave> naves;
    private Set<Casilla> casillas;

    public TableroNaves(Jugador propietario) {
        super(propietario);
    }
    
    /**
     * Añade una nave al tablero si la posición es válida.
     * @param nave La nave a colocar.
     * @return true si la nave se pudo colocar, false en caso contrario.
     */
    public boolean añadirNave(Nave nave) {

        return false;

    }

    public EstadoCasilla recibirImpacto(Coordenada coordenada) {
        
        return null;
        
    }
}