package mx.itson.subsistema_inicio_fin_partida;

import java.util.List;
import mx.itson.models.IPartida;
import mx.itson.utils.dtos.JugadorDTO;

/**
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
 */
public interface IInicioFinPartida {
    public IPartida crearPartida(JugadorDTO jugador1, JugadorDTO jugador2);
    public IPartida obtenerPartida(String idPartida);
    public void eliminarPartida(String idPartida);
    public IPartida obtenerPartidaDeJugador (String idJugador);
    public boolean jugadorEnPartida(String idJugador);
    public int cantidadPartidas();
    public List<IPartida> obtenerTodasPartidas();
}