package mx.itson.models;

import mx.itson.servidor.GestorJugadores;
import mx.itson.subsistema_gestor_partidas.GestorPartidas;
import mx.itson.utils.enums.EstadoPartida;

/**
 *
 * @author ErnestoLpz_252663
 */
public class ControlFinPartida {

    private final GestorJugadores gestorJugadores;
    private final GestorPartidas gestorPartidas;

    /**
     * Constructor obligatorio para inyectar los gestores.
     */
    public ControlFinPartida(GestorJugadores gestorJugadores, GestorPartidas gestorPartidas) {
        this.gestorJugadores = gestorJugadores;
        this.gestorPartidas = gestorPartidas;
    }

    /**
     * Método principal para el caso de uso "Abandonar Partida"
     */
    public void aplicarRendicion(IJugador jugadorQueAbandona, Partida partida) {

    }
    
    /**
     * Aplica la victoria por condiciones naturales (hundir naves)
     */
    public void aplicarVictoria(IJugador ganador, Partida partida) {

    }
    
    /**
     * Maneja desconexiones inesperadas
     */
    public void desconexionJugador(IJugador jugador, Partida partida) {
        System.out.println("CONTROL (Servidor): Jugador desconectado.");

        if (partida.getEstadoPartida()== EstadoPartida.FINALIZADA) {
            return;
        }

        System.out.println("CONTROL (Servidor): Aplicando rendición por desconexión.");
        aplicarRendicion(jugador, partida);
    }
}
