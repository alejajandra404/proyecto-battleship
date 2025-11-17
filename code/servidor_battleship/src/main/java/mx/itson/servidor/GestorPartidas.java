package mx.itson.servidor;

import mx.itson.utils.dtos.JugadorDTO;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor centralizado de partidas activas en el servidor
 * Thread-safe para manejo concurrente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class GestorPartidas {

    private final Map<String, Partida> partidas; // Key: ID de partida
    private final Map<String, String> jugadorAPartida; // Key: ID de jugador, Value: ID de partida

    /**
     * Constructor
     */
    public GestorPartidas() {
        this.partidas = new ConcurrentHashMap<>();
        this.jugadorAPartida = new ConcurrentHashMap<>();
    }

    /**
     * Crea una nueva partida entre dos jugadores
     *
     * @param jugador1
     * @param jugador2
     * @return La partida creada
     */
    public synchronized Partida crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) {
        Partida partida = new Partida(jugador1, jugador2);

        partidas.put(partida.getIdPartida(), partida);
        jugadorAPartida.put(jugador1.getId(), partida.getIdPartida());
        jugadorAPartida.put(jugador2.getId(), partida.getIdPartida());

        System.out.println("[GESTOR_PARTIDAS] Partida creada: " + partida.getIdPartida());
        return partida;
    }

    /**
     * Obtiene la partida de un jugador
     *
     * @param idJugador ID del jugador
     * @return La partida del jugador o null si no está en ninguna
     */
    public synchronized Partida obtenerPartidaDeJugador(String idJugador) {
        String idPartida = jugadorAPartida.get(idJugador);
        if (idPartida == null) {
            return null;
        }
        return partidas.get(idPartida);
    }

    /**
     * Obtiene una partida por su ID
     *
     * @param idPartida ID de la partida
     * @return La partida o null si no existe
     */
    public synchronized Partida obtenerPartida(String idPartida) {
        return partidas.get(idPartida);
    }

    /**
     * Elimina una partida del gestor
     *
     * @param idPartida ID de la partida
     */
    public synchronized void eliminarPartida(String idPartida) {
        Partida partida = partidas.remove(idPartida);
        if (partida != null) {
            jugadorAPartida.remove(partida.getJugador1().getId());
            jugadorAPartida.remove(partida.getJugador2().getId());
            System.out.println("[GESTOR_PARTIDAS] Partida eliminada: " + idPartida);
        }
    }

    /**
     * Verifica si un jugador está en una partida activa
     *
     * @param idJugador ID del jugador
     * @return true si está en una partida
     */
    public synchronized boolean jugadorEnPartida(String idJugador) {
        return jugadorAPartida.containsKey(idJugador);
    }

    /**
     * Obtiene el número de partidas activas
     *
     * @return Número de partidas
     */
    public synchronized int cantidadPartidas() {
        return partidas.size();
    }

    /**
     * Obtiene todas las partidas activas
     *
     * @return Lista de partidas
     */
    public synchronized List<Partida> obtenerTodasPartidas() {
        return new ArrayList<>(partidas.values());
    }
}
