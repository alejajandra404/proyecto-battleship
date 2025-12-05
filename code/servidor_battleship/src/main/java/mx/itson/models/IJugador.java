package mx.itson.models;

import java.awt.Color;
import java.util.List;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.dtos.EstadisticaDTO;

/**
 * IJugador.java
 *
 * Interfaz que define el contrato para un jugador en el servidor.
 * Gestiona los tableros de naves y disparos del jugador, así como
 * las operaciones relacionadas con el juego.
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
public interface IJugador {

    /**
     * Obtiene el ID único del jugador.
     *
     * @return ID del jugador
     */
    public String getId();

    /**
     * Obtiene el nombre del jugador.
     *
     * @return Nombre del jugador
     */
    public String getNombre();

    /**
     * Obtiene el color asignado al jugador.
     *
     * @return Color del jugador
     */
    public Color getColor();

    /**
     * Obtiene el tablero de disparos del jugador.
     * Este tablero registra los disparos realizados hacia el oponente.
     *
     * @return Tablero de disparos
     */
    public ITableroDisparos getTableroDisparos();

    /**
     * Obtiene el tablero de naves del jugador.
     * Este tablero contiene las naves del jugador y los impactos recibidos.
     *
     * @return Tablero de naves
     */
    public ITableroNaves getTableroNaves();

    /**
     * Verifica si el jugador está actualmente en una partida.
     *
     * @return true si el jugador está en partida
     */
    public boolean isEnPartida();

    /**
     * Marca un disparo realizado por este jugador en su tablero de disparos.
     *
     * @param disparo El objeto Disparo que contiene el resultado y la coordenada
     * @throws ModelException Si ocurre un error al marcar el disparo
     */
    public void marcarDisparo(Disparo disparo) throws ModelException;

    /**
     * Procesa un disparo recibido de un oponente en una coordenada específica.
     * Delega la acción a su tablero de naves.
     *
     * @param coordenada La coordenada donde el oponente ha disparado
     * @return Estado de la casilla después del impacto (IMPACTADA_VACIA, IMPACTADA_AVERIADA, IMPACTADA_HUNDIDA)
     * @throws ModelException Si ocurre un error al procesar el disparo
     */
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException;

    /**
     * Valida si un disparo en una coordenada es válido.
     * Verifica que no se haya disparado antes en esa misma casilla.
     *
     * @param coordenada Coordenada a validar
     * @return true si el disparo es válido, false en caso contrario
     * @throws ModelException Si ocurre un error durante la validación
     */
    public boolean validarDisparo(Coordenada coordenada) throws ModelException;

    /**
     * Añade una nave al tablero del jugador.
     *
     * @param nave Nave a añadir
     * @return true si se añadió exitosamente
     * @throws ModelException Si ocurre un error al añadir la nave
     */
    public boolean añadirNave(Nave nave) throws ModelException;

    /**
     * Elimina una nave del tablero del jugador.
     *
     * @param nave Nave a eliminar
     * @return true si se eliminó exitosamente
     * @throws ModelException Si ocurre un error al eliminar la nave
     */
    public boolean eliminarNave(Nave nave) throws ModelException;

    /**
     * Coloca todas las naves del jugador en el tablero.
     *
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente todas las naves
     * @throws ModelException Si ocurre un error al colocar las naves
     */
    public boolean colocarNaves(List<Nave> naves) throws ModelException;

    /**
     * Obtiene el jugador del modelo por su ID.
     *
     * @param idBusqueda ID del jugador a buscar
     * @return Instancia de IJugador, o null si no se encuentra
     */
    public IJugador getJugadorModeloPorId(String idBusqueda);

    /**
     * Genera las estadísticas finales del jugador al terminar la partida.
     *
     * @param esGanador true si este jugador ganó la partida
     * @param barcosHundidos Cantidad de barcos que este jugador hundió
     * @return DTO con las estadísticas del jugador
     */
    public EstadisticaDTO generarEstadisticas(boolean esGanador, int barcosHundidos);
}