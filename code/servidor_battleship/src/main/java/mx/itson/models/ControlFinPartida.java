package mx.itson.models;

import mx.itson.utils.enums.EstadoPartida;

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
    
    // --- Métodos Mockeados falta agregar como verificar las naves hundidas---
    /**
     * Metodo que aplica la victoria a la partida por naves hundidas
     * @param ganador
     * @param partida 
     */
    public void aplicarVictoria(IJugador ganador, Partida partida) {
        System.out.println("CONTROL (Servidor): Aplicando victoria...");

        IJugador perdedor = (ganador.equals(partida.getJugador1())) 
                            ? partida.getJugador2() 
                            : partida.getJugador1();
        
        String razon = "Todas las naves del rival fueron hundidas...";

        partida.finalizarPartida(perdedor, razon);
    }
    
    public void desconexionJugador(IJugador jugador, Partida partida) {
        System.out.println("CONTROL (Servidor): Un jugador se ha desconectado.");

        if (partida.getEstado() == EstadoPartida.FINALIZADA) {
            System.out.println("CONTROL (Servidor): La partida ya había terminado. No se toman acciones.");
            return;
        }

        System.out.println("CONTROL (Servidor): La partida está en curso. Se aplicará como rendición.");
        
        aplicarRendicion(jugador, partida);
    }
}
