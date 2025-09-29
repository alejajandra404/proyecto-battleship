package models;
import java.util.HashSet;
import java.util.Set;
/**
 * TableroDisparos.java
 *
 * Clase entidad que hereda de la clase padre Tablero
 * y representa el tablero de disparos de un jugador.
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
public class TableroDisparos extends Tablero {
    
    private Set<Disparo> disparos;
    
    public TableroDisparos(Jugador propietario) {
        super(propietario);
        this.disparos = new HashSet<>();
    }

    /**
     * Añade un nuevo disparo al registro del tablero
     * @param disparo El disparo a registrar
     */
    public void añadirDisparo(Disparo disparo) {
        this.disparos.add(disparo);
    }
    
    /**
     * Valida si ya se ha disparado a una coordenada previamente
     * @param coordenada La coordenada a verificar
     * @return true si la coordenada está libre, false si ya fue disparada
     */
    public boolean validarDisparo(Coordenada coordenada) {
        for (Disparo d : disparos) {
            if (d.obtenerCoordenada().equals(coordenada)) {
                return false;
            }
        }
        return true;
    }
}