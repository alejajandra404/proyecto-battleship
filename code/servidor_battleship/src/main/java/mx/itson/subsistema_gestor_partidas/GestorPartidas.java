package mx.itson.subsistema_gestor_partidas;

import mx.itson.utils.dtos.JugadorDTO;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import mx.itson.exceptions.GestorPartidasException;
import mx.itson.exceptions.ModelException;
import mx.itson.factory.JugadorFactory;
import mx.itson.factory.PartidaFactory;
import mx.itson.models.IPartida;
import mx.itson.models.Partida;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.NaveDTO;
import mx.itson.utils.dtos.PartidaDTO;

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
public class GestorPartidas implements IGestorPartidas{

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
    @Override
    public synchronized PartidaDTO crearPartida(JugadorDTO jugador1, JugadorDTO jugador2) {
        
        try {
            String idPartida = UUID.randomUUID().toString();
            
            IPartida partidaNueva = PartidaFactory.crearPartida(
                    idPartida,
                    JugadorFactory.crearJugador(jugador1.getId(), jugador1.getNombre(), jugador1.getColor().toString()),
                    JugadorFactory.crearJugador(jugador2.getId(), jugador2.getNombre(), jugador2.getColor().toString())
            );
            
            Partida partida = new Partida(jugador1, jugador2);
            
            partidas.put(partida.getIdPartida(), partida);
            jugadorAPartida.put(jugador1.getId(), partida.getIdPartida());
            jugadorAPartida.put(jugador2.getId(), partida.getIdPartida());
            
            System.out.println("[GESTOR_PARTIDAS] Partida creada: " + partida.getIdPartida());
            return partida;
        } catch (ModelException ex) {
            System.getLogger(GestorPartidas.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    /**
     * Obtiene la partida de un jugador
     *
     * @param idJugador ID del jugador
     * @return La partida del jugador o null si no está en ninguna
     */
    public synchronized PartidaDTO obtenerPartidaDeJugador(String idJugador) {
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
    public synchronized PartidaDTO obtenerPartida(String idPartida) {
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
    public synchronized List<PartidaDTO> obtenerTodasPartidas() {
        return new ArrayList<>(partidas.values());
    }

    @Override
    public void colocarNaves(String idPartida, String idJugador, List<NaveDTO> naves) throws GestorPartidasException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void iniciarTemporizador(String idPartida) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public DisparoDTO procesarDisparo(String idPartida, String idJugador, CoordenadaDTO coordenada) throws GestorPartidasException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
