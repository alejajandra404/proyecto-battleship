package mx.itson.factory;

import mx.itson.models.IJugador;
import mx.itson.models.IPartida;
import mx.itson.models.Partida;

/**
 * PartidaFactory.java
 *
 * Factory para la creación de instancias de IPartida.
 * Crea partidas con dos jugadores listos para iniciar el juego.
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
public class PartidaFactory {

    /**
     * Crea y retorna una nueva instancia de IPartida con los dos jugadores especificados.
     *
     * @param idPartida Identificador único de la partida
     * @param jugador1 Primer jugador de la partida
     * @param jugador2 Segundo jugador de la partida
     * @return Nueva instancia de Partida
     */
    public static IPartida crearPartida(String idPartida, IJugador jugador1, IJugador jugador2){
        return new Partida(idPartida, jugador1, jugador2);
    }
}