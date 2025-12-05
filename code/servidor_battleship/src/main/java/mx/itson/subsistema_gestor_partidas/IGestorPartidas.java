package mx.itson.subsistema_gestor_partidas;

import java.util.List;
import java.util.function.Consumer;
import mx.itson.exceptions.GestorPartidasException;
import mx.itson.models.IPartida;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.JugadorDTO;
import mx.itson.utils.dtos.NaveDTO;
import mx.itson.utils.dtos.PartidaDTO;

/**
 * IGestorPartidas.java
 *
 * Interfaz del subsistema gestor de partidas.
 * Define las operaciones para administrar partidas activas en el servidor,
 * incluyendo creación, eliminación, gestión de turnos y procesamiento de disparos.
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
public interface IGestorPartidas {

    /**
     * Crea una nueva partida entre dos jugadores.
     *
     * @param jugador1 Primer jugador de la partida
     * @param jugador2 Segundo jugador de la partida
     * @return DTO de la partida creada
     * @throws GestorPartidasException Si ocurre un error al crear la partida
     */
    public PartidaDTO crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) throws GestorPartidasException;

    /**
     * Obtiene la partida activa de un jugador específico.
     *
     * @param idJugador ID del jugador
     * @return DTO de la partida del jugador, o null si no está en ninguna partida
     * @throws GestorPartidasException Si ocurre un error al obtener la partida
     */
    public PartidaDTO obtenerPartidaDeJugador(String idJugador) throws GestorPartidasException;

    /**
     * Obtiene una partida por su ID.
     *
     * @param idPartida ID de la partida
     * @return DTO de la partida, o null si no existe
     * @throws GestorPartidasException Si ocurre un error al obtener la partida
     */
    public PartidaDTO obtenerPartida(String idPartida) throws GestorPartidasException;

    /**
     * Obtiene la instancia del modelo IPartida sin convertir a DTO.
     *
     * @param idPartida ID de la partida
     * @return Instancia de IPartida del modelo, o null si no existe
     */
    public IPartida obtenerPartidaModelo(String idPartida);

    /**
     * Verifica si un jugador tiene una partida activa.
     *
     * @param idJugador ID del jugador
     * @return true si el jugador está en una partida activa
     */
    public boolean verificarJugadorPartidaActiva(String idJugador);

    /**
     * Elimina una partida del gestor y libera sus recursos.
     *
     * @param idPartida ID de la partida a eliminar
     * @throws GestorPartidasException Si ocurre un error al eliminar la partida
     */
    public void eliminarPartida(String idPartida) throws GestorPartidasException;

    /**
     * Coloca las naves de un jugador en su tablero.
     *
     * @param idPartida ID de la partida
     * @param idJugador ID del jugador que coloca las naves
     * @param naves Lista de naves a colocar
     * @return DTO de la partida actualizada
     * @throws GestorPartidasException Si ocurre un error al colocar las naves
     */
    public PartidaDTO colocarNaves(String idPartida, String idJugador, List<NaveDTO> naves) throws GestorPartidasException;

    /**
     * Establece el callback que se ejecutará cuando se agote el tiempo de un turno.
     *
     * @param idPartida ID de la partida
     * @param callback Consumer que recibe el ID del jugador que perdió su turno
     */
    public void establecerRespuestaTiempoAgotado(String idPartida, Consumer<String> callback);

    /**
     * Establece el callback para actualizaciones periódicas del tiempo restante.
     *
     * @param idPartida ID de la partida
     * @param callback Consumer que recibe el tiempo restante en segundos
     */
    public void establecerCallbackActualizacionTiempo(String idPartida, Consumer<Integer> callback);

    /**
     * Inicia el temporizador del turno actual de una partida.
     *
     * @param idPartida ID de la partida
     */
    public void iniciarTemporizador(String idPartida);

    /**
     * Libera los recursos asociados a una partida (temporizadores, executors, etc.).
     *
     * @param idPartida ID de la partida
     */
    public void liberarRecursos(String idPartida);

    /**
     * Procesa un disparo de un jugador hacia el oponente.
     *
     * @param idPartida ID de la partida
     * @param idJugador ID del jugador que dispara
     * @param coordenada Coordenada del disparo
     * @return DTO del disparo con el resultado (AGUA, IMPACTO_AVERIADA, IMPACTO_HUNDIDA)
     * @throws GestorPartidasException Si ocurre un error al procesar el disparo
     */
    public DisparoDTO procesarDisparo(String idPartida, String idJugador, CoordenadaDTO coordenada) throws GestorPartidasException;

    /**
     * Verifica si un jugador está en una partida activa.
     *
     * @param idJugador ID del jugador
     * @return true si el jugador está en una partida
     */
    public boolean jugadorEnPartida(String idJugador);

    /**
     * Obtiene el número total de partidas activas en el gestor.
     *
     * @return Cantidad de partidas activas
     */
    public int cantidadPartidas();

    /**
     * Obtiene todas las partidas activas del gestor.
     *
     * @return Lista de DTOs de todas las partidas activas
     * @throws GestorPartidasException Si ocurre un error al obtener las partidas
     */
    public List<PartidaDTO> obtenerTodasPartidas() throws GestorPartidasException;

}