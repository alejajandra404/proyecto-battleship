package mx.itson.subsistema_gestor_partidas;

import java.util.List;
import mx.itson.exceptions.GestorPartidasException;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.JugadorDTO;
import mx.itson.utils.dtos.NaveDTO;
import mx.itson.utils.dtos.PartidaDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public interface IGestorPartidas {
    
    public void crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) throws GestorPartidasException;
    
    public PartidaDTO obtenerPartidaDeJugador(String idJugador);
    
    public PartidaDTO obtenerPartida(String idJugador);
    
    public void eliminarPartida(String idPartida) throws GestorPartidasException;
    
    public void colocarNaves(String idPartida, String idJugador, List<NaveDTO> naves) throws GestorPartidasException;
    
    public void iniciarTemporizador(String idPartida);
    
    public DisparoDTO procesarDisparo(String idPartida, String idJugador, CoordenadaDTO coordenada) throws GestorPartidasException;
    
    public boolean jugadorEnPartida(String idJugador);
    
    public int cantidadPartidas();
    
    public List<PartidaDTO> obtenerTodasPartidas();
    
}