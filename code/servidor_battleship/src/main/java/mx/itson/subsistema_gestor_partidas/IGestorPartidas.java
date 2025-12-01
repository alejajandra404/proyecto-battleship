package mx.itson.subsistema_gestor_partidas;

import java.util.List;
import java.util.function.Consumer;
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
    
    public PartidaDTO crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) throws GestorPartidasException;
    
    public PartidaDTO obtenerPartidaDeJugador(String idJugador) throws GestorPartidasException;
    
    public PartidaDTO obtenerPartida(String idJugador) throws GestorPartidasException;
    
    public boolean verificarJugadorPartidaActiva(String idJugador);
    
    public void eliminarPartida(String idPartida) throws GestorPartidasException;
    
    public PartidaDTO colocarNaves(String idPartida, String idJugador, List<NaveDTO> naves) throws GestorPartidasException;
    
    public void establecerRespuestaTiempoAgotado(String idPartida, Consumer<String> callback);

    public void establecerCallbackActualizacionTiempo(String idPartida, Consumer<Integer> callback);

    public void iniciarTemporizador(String idPartida);
    
    public void liberarRecursos(String idPartida);
    
    public DisparoDTO procesarDisparo(String idPartida, String idJugador, CoordenadaDTO coordenada) throws GestorPartidasException;
    
    public boolean jugadorEnPartida(String idJugador);
    
    public int cantidadPartidas();
    
    public List<PartidaDTO> obtenerTodasPartidas() throws GestorPartidasException;
    
}