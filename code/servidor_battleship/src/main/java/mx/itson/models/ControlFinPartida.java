package mx.itson.models;

/**
 *
 * @author Usuario
 */
public class ControlFinPartida {

    /**
     * Método principal para el caso de uso "Abandonar Partida"
     * Es llamado por el Servidor cuando recibe un mensaje de rendición
     */
    public void aplicarRendicion(IJugador jugadorQueAbandona, Partida partida) {
        System.out.println("CONTROL (Servidor): Un jugador ha solicitado rendirse.");
        
        String razon = "Rendición por ya no querer jugar";
        
        //Llama a la entidad Partida para actualizar su estado interno
        partida.finalizarPartida(jugadorQueAbandona, razon);
    }
    
    // --- Métodos Mockeados del diagrama ---
    
    public void aplicarVictoria(IJugador ganador, Partida partida) {
        //Se usaría para un fin de partida por disparos
    }
    
    public void desconexionJugador(IJugador jugador, Partida partida) {
        //Para cuando un jugador se desconecta
    }
}
