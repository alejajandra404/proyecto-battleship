package mx.itson.servidor;

import mx.itson.utils.dtos.JugadorDTO;
import mx.itson.utils.dtos.SolicitudPartidaDTO;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestor centralizado de jugadores conectados al servidor
 * Thread-safe para manejo concurrente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class GestorJugadores {

    private final Map<String, JugadorDTO> jugadoresConectados;
    private final Map<String, ManejadorCliente> manejadores;
    private final Map<String, SolicitudPartidaDTO> solicitudesPendientes; // Key: ID del invitado

    /**
     * Constructor
     */
    public GestorJugadores() {
        this.jugadoresConectados = new ConcurrentHashMap<>();
        this.manejadores = new ConcurrentHashMap<>();
        this.solicitudesPendientes = new ConcurrentHashMap<>();
    }

    /**
     * Verifica si un nombre de jugador ya está en uso
     * @param nombre Nombre a verificar
     * @return true si el nombre está disponible, false si ya existe
     */
    public synchronized boolean nombreDisponible(String nombre) {
        return jugadoresConectados.values().stream()
                .noneMatch(j -> j.getNombre().equalsIgnoreCase(nombre));
    }

    /**
     * Registra un nuevo jugador en el sistema
     * @param jugador DTO del jugador
     * @param manejador Manejador asociado al cliente
     * @return true si se registró exitosamente, false si el nombre ya existe
     */
    public synchronized boolean registrarJugador(JugadorDTO jugador, ManejadorCliente manejador) {
        if (!nombreDisponible(jugador.getNombre())) {
            return false;
        }

        jugadoresConectados.put(jugador.getId(), jugador);
        manejadores.put(jugador.getId(), manejador);

        System.out.println("[GESTOR] Jugador registrado: " + jugador.getNombre() + " (ID: " + jugador.getId() + ")");
        return true;
    }

    /**
     * Obtiene la lista de jugadores disponibles (no en partida)
     * @return Lista de jugadores disponibles
     */
    public synchronized List<JugadorDTO> obtenerJugadoresDisponibles() {
        return new ArrayList<>(jugadoresConectados.values().stream()
                .filter(j -> !j.isEnPartida())
                .toList());
    }

    /**
     * Marca un jugador como en partida
     * @param id ID del jugador
     */
    public synchronized void marcarEnPartida(String id) {
        JugadorDTO jugador = jugadoresConectados.get(id);
        if (jugador != null) {
            jugador.setEnPartida(true);
            System.out.println("[GESTOR] Jugador " + jugador.getNombre() + " ahora está en partida");
        }
    }

    /**
     * Libera un jugador de la partida (disponible nuevamente)
     * @param id ID del jugador
     */
    public synchronized void liberarJugador(String id) {
        JugadorDTO jugador = jugadoresConectados.get(id);
        if (jugador != null) {
            jugador.setEnPartida(false);
            System.out.println("[GESTOR] Jugador " + jugador.getNombre() + " ahora está disponible");

            // Notificar a jugadores en espera
            notificarJugadorDisponible(jugador);
        }
    }

    /**
     * Marca un jugador como disponible (alias de liberarJugador)
     * @param id ID del jugador
     */
    public synchronized void marcarDisponible(String id) {
        liberarJugador(id);
    }

    /**
     * Elimina un jugador del sistema (cuando se desconecta)
     * @param id ID del jugador
     */
    public synchronized void eliminarJugador(String id) {
        JugadorDTO jugador = jugadoresConectados.remove(id);
        manejadores.remove(id);

        if (jugador != null) {
            System.out.println("[GESTOR] Jugador eliminado: " + jugador.getNombre());
        }
    }

    /**
     * Obtiene el número total de jugadores conectados
     * @return Número de jugadores
     */
    public synchronized int cantidadJugadores() {
        return jugadoresConectados.size();
    }

    /**
     * Notifica a todos los jugadores en espera que hay un nuevo jugador disponible
     * @param nuevoJugador Jugador que ahora está disponible
     */
    private void notificarJugadorDisponible(JugadorDTO nuevoJugador) {
        // Obtener jugadores que están esperando (conectados pero no en partida)
        List<JugadorDTO> jugadoresEsperando = obtenerJugadoresDisponibles();

        for (JugadorDTO jugador : jugadoresEsperando) {
            ManejadorCliente manejador = manejadores.get(jugador.getId());
            if (manejador != null) {
                manejador.notificarJugadorDisponible();
            }
        }
    }

    /**
     * Obtiene un jugador por su ID
     * @param id ID del jugador
     * @return JugadorDTO o null si no existe
     */
    public synchronized JugadorDTO obtenerJugador(String id) {
        return jugadoresConectados.get(id);
    }

    /**
     * Obtiene el manejador de un jugador por su ID
     * @param id ID del jugador
     * @return ManejadorCliente o null si no existe
     */
    public synchronized ManejadorCliente obtenerManejador(String id) {
        return manejadores.get(id);
    }

    /**
     * Registra una solicitud de partida pendiente
     * @param solicitud Solicitud de partida
     * @return true si se registró, false si el invitado ya tiene una solicitud pendiente
     */
    public synchronized boolean registrarSolicitudPartida(SolicitudPartidaDTO solicitud) {
        String idInvitado = solicitud.getIdInvitado();

        // Verificar que el invitado existe y está disponible
        JugadorDTO invitado = jugadoresConectados.get(idInvitado);
        if (invitado == null || invitado.isEnPartida()) {
            return false;
        }

        // Verificar que no tenga una solicitud pendiente
        if (solicitudesPendientes.containsKey(idInvitado)) {
            return false;
        }

        solicitudesPendientes.put(idInvitado, solicitud);
        System.out.println("[GESTOR] Solicitud de partida registrada: " +
                          solicitud.getNombreSolicitante() + " -> " + solicitud.getNombreInvitado());
        return true;
    }

    /**
     * Obtiene una solicitud pendiente para un jugador
     * @param idInvitado ID del jugador invitado
     * @return SolicitudPartidaDTO o null si no existe
     */
    public synchronized SolicitudPartidaDTO obtenerSolicitudPendiente(String idInvitado) {
        return solicitudesPendientes.get(idInvitado);
    }

    /**
     * Elimina una solicitud pendiente
     * @param idInvitado ID del jugador invitado
     * @return Solicitud eliminada o null si no existía
     */
    public synchronized SolicitudPartidaDTO eliminarSolicitudPendiente(String idInvitado) {
        SolicitudPartidaDTO solicitud = solicitudesPendientes.remove(idInvitado);
        if (solicitud != null) {
            System.out.println("[GESTOR] Solicitud de partida eliminada: " +
                              solicitud.getNombreSolicitante() + " -> " + solicitud.getNombreInvitado());
        }
        return solicitud;
    }

    /**
     * Verifica si un jugador tiene una solicitud pendiente
     * @param idJugador ID del jugador
     * @return true si tiene solicitud pendiente
     */
    public synchronized boolean tieneSolicitudPendiente(String idJugador) {
        return solicitudesPendientes.containsKey(idJugador);
    }
}
