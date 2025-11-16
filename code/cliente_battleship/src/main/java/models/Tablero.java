package models;

import exceptions.TableroException;

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
    
    private final Jugador propietario;
    private final int tamanio;
    
    /**
     * Constructor para un Tablero
     * @param propietario El jugador dueño de este tablero
     * @param tamanio
     */
    public Tablero(Jugador propietario, int tamanio) throws TableroException {
        this.propietario = propietario;
        validarTamanio(tamanio);
        this.tamanio = tamanio;
    }

    /**
     * Valida si una coordenada está dentro de los límites del tablero.
     * @param coordenada La coordenada a validar.
     * @return true si la coordenada está dentro del tablero, false en caso contrario.
     */
    public boolean validarCoordenada(Coordenada coordenada){
        int x = coordenada.obtenerX();
        int y = coordenada.obtenerY();
        return (x < tamanio && x >= 0) && (y < tamanio && y >= 0);
    };

    public Jugador getPropietario() {return propietario;}

    public int getTamanio() {return tamanio;}
    
    private void validarTamanio(int tamanio) throws TableroException{
        if(tamanio < 0)
            throw new TableroException("Solo se aceptan tamaños positivos.");
    }
}