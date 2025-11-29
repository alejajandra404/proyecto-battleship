package mx.itson.factory;

import mx.itson.exceptions.ModelException;
import mx.itson.models.IJugador;
import mx.itson.models.JugadorServidor;

/**
 *
 * @author PC WHITE WOLF
 */
public class JugadorFactory {
    
    private static final int TAMANO_TABLERO = 10;
    private static final int TOTAL_NAVES = 11;
    
    public static IJugador crearJugador(String idJugador, String nombre, String color) throws ModelException{
        return new JugadorServidor(
                idJugador, 
                nombre, 
                color, 
                TableroFactory.crearTableroNaves(TAMANO_TABLERO, TOTAL_NAVES), 
                TableroFactory.crearTableroDisparos(TAMANO_TABLERO));
    }
}