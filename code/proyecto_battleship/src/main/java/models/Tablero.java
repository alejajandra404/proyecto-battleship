package models;
/**
 * Tablero.java
 *
 * Clase entidad padre que representa un tablero.
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
public class Tablero {
    
    private Jugador propietario;
    private int tamanio;
    
    /**
     * Constructor para un Tablero
     * @param propietario El jugador dueño de este tablero
     */
    public Tablero(Jugador propietario) {
        this.propietario = propietario;
        this.tamanio = 10;
    }

    /**
     * Valida si una coordenada está dentro de los límites del tablero.
     * @param coordenada La coordenada a validar.
     * @return true si la coordenada está dentro del tablero, false en caso contrario.
     */
    public boolean validarCoordenada(Coordenada coordenada) {
        return coordenada.getX()>= 0 && coordenada.getX() < this.tamanio &&
               coordenada.getY()>= 0 && coordenada.getY() < this.tamanio;
    }

    public Jugador getPropietario() {
        return propietario;
    }

    public int getTamanio() {
        return tamanio;
    }
}