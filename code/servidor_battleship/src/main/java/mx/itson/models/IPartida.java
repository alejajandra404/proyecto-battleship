package mx.itson.models;

import java.util.List;
import java.util.function.Consumer;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.enums.EstadoPartida;

/**
 * IPartida.java
 *
 * Interfaz que define el contrato para una partida de Battleship.
 * Gestiona el estado del juego, los turnos, el temporizador y
 * las operaciones entre los dos jugadores.
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
public interface IPartida {

    /**
     * Obtiene el ID único de la partida.
     *
     * @return ID de la partida
     */
    public String getIdPartida();

    /**
     * Obtiene un jugador específico por su ID.
     *
     * @param idJugador ID del jugador a buscar
     * @return Instancia del jugador
     */
    public IJugador getJugador(String idJugador);

    /**
     * Obtiene el primer jugador de la partida.
     *
     * @return Jugador 1
     */
    public IJugador getJugador1();

    /**
     * Obtiene el segundo jugador de la partida.
     *
     * @return Jugador 2
     */
    public IJugador getJugador2();

    /**
     * Obtiene el ID del jugador que tiene el turno actual.
     *
     * @return ID del jugador en turno
     */
    public String getIdJugadorEnTurno();

    /**
     * Obtiene el ID del jugador ganador.
     *
     * @return ID del ganador, o null si no hay ganador aún
     */
    public String getIdGanador();

    /**
     * Verifica si ya hay un ganador en la partida.
     *
     * @return true si hay un ganador
     */
    public boolean hayGanador();

    /**
     * Verifica si ambos jugadores han colocado todas sus naves.
     *
     * @return true si ambos jugadores están listos
     */
    public boolean ambosJugadoresListos();

    /**
     * Obtiene el tiempo restante del turno actual en segundos.
     *
     * @return Tiempo restante en segundos
     */
    public int getTiempoRestante();

    /**
     * Obtiene el estado actual de la partida.
     *
     * @return Estado de la partida (EN_CURSO, FINALIZADA)
     */
    public EstadoPartida getEstadoPartida();

    /**
     * Verifica si un jugador específico tiene el turno actual.
     *
     * @param idJugador ID del jugador a verificar
     * @return true si el jugador tiene el turno
     */
    public boolean verificarJugadorTurno(String idJugador);

    /**
     * Valida si un disparo es válido para un jugador.
     *
     * @param idJugador ID del jugador que dispara
     * @param coordenada Coordenada del disparo
     * @return true si el disparo es válido
     * @throws ModelException Si ocurre un error durante la validación
     */
    public boolean validarDisparo(String idJugador, CoordenadaDTO coordenada) throws ModelException;

    /**
     * Procesa un disparo de un jugador hacia el oponente.
     * Actualiza el estado del juego y cambia el turno si es necesario.
     *
     * @param idJugadorDispara ID del jugador que dispara
     * @param coordenada Coordenada del disparo
     * @return DTO del disparo con el resultado
     * @throws ModelException Si ocurre un error al procesar el disparo
     */
    public DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) throws ModelException;

    /**
     * Coloca todas las naves de un jugador en su tablero.
     * Si ambos jugadores colocan sus naves, inicia el juego automáticamente.
     *
     * @param idJugador ID del jugador
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente
     * @throws ModelException Si ocurre un error al colocar las naves
     */
    public boolean colocarNaves(String idJugador, List<Nave> naves) throws ModelException;

    /**
     * Maneja la lógica cuando se agota el tiempo de un turno.
     * Cambia automáticamente el turno al oponente.
     */
    public void manejarTiempoAgotado();

    /**
     * Establece el callback que se ejecutará cuando se agote el tiempo.
     *
     * @param callback Consumer que recibe el ID del jugador que perdió el turno
     */
    public void establecerRespuestaTiempoAgotado(Consumer<String> callback);

    /**
     * Establece el callback para actualizaciones periódicas del tiempo restante.
     *
     * @param callback Consumer que recibe el tiempo restante en segundos
     */
    public void establecerCallbackActualizacionTiempo(Consumer<Integer> callback);

    /**
     * Inicia el temporizador del turno actual.
     * Reinicia el tiempo y comienza la cuenta regresiva.
     */
    public void iniciarTemporizador();

    /**
     * Detiene el temporizador del turno actual.
     */
    public void detenerTemporizador();

    /**
     * Libera todos los recursos asociados a la partida.
     * Detiene temporizadores y cierra executors.
     */
    public void liberarRecursos();
}