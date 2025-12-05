package mx.itson.factory;

import mx.itson.exceptions.ModelException;
import mx.itson.models.IJugador;
import mx.itson.models.JugadorServidor;

/**
 * JugadorFactory.java
 *
 * Factory para la creación de instancias de IJugador.
 * Crea jugadores con sus tableros de naves y disparos inicializados.
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
public class JugadorFactory {

    /** Tamaño estándar del tablero de juego */
    private static final int TAMANO_TABLERO = 10;

    /**
     * Crea y retorna una nueva instancia de IJugador con sus tableros inicializados.
     *
     * @param idJugador Identificador único del jugador
     * @param nombre Nombre del jugador
     * @param color Color asignado al jugador
     * @return Nueva instancia de JugadorServidor
     * @throws ModelException Si ocurre un error al crear los tableros
     */
    public static IJugador crearJugador(String idJugador, String nombre, String color) throws ModelException{
        return new JugadorServidor(
                idJugador,
                nombre,
                color,
                TableroFactory.crearTableroNaves(TAMANO_TABLERO),
                TableroFactory.crearTableroDisparos(TAMANO_TABLERO));
    }
}