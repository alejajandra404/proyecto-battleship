package mx.itson.utils.enums;

/**
 * Estados posibles del controlador de juego en el cliente.
 * Controla el flujo de la partida desde la colocación hasta el final.
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public enum EstadoJuego {
    /** Esperando que el jugador coloque sus naves */
    ESPERANDO_COLOCACION,

    /** Esperando que el oponente termine de colocar sus naves */
    ESPERANDO_OPONENTE,

    /** Partida en curso, se pueden realizar disparos */
    EN_JUEGO,

    /** Partida finalizada, hay un ganador */
    FINALIZADO
}
