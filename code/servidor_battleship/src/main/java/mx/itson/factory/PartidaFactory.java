package mx.itson.factory;

import mx.itson.models.IJugador;
import mx.itson.models.IPartida;
import mx.itson.models.Partida;

/**
 *
 * @author PC WHITE WOLF
 */
public class PartidaFactory {
    
    public static IPartida crearPartida(String idPartida, IJugador jugador1, IJugador jugador2){
        return new Partida(idPartida, jugador1, jugador2);
    }
}