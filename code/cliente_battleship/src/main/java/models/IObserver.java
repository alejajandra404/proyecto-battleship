package models;

/**
 *
 * @author PC WHITE WOLF
 */
public interface IObserver {
    
    public void notificar(String mensaje, String nombreJugadorTurno);
}