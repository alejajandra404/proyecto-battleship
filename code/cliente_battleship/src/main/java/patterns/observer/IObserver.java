package patterns.observer;

/**
 * IObserver.java
 *
 * Interfaz del patrón Observer que define el contrato para los observadores.
 * Los observadores son notificados cuando ocurren cambios en el sujeto observado.
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
public interface IObserver {

    /**
     * Método llamado por el sujeto para notificar al observador sobre un cambio.
     * Recibe información sobre el evento que ocurrió.
     *
     * @param mensaje Mensaje descriptivo del evento
     * @param nombreJugadorTurno Nombre del jugador que tiene el turno actual
     */
    public void notificar(String mensaje, String nombreJugadorTurno);
}