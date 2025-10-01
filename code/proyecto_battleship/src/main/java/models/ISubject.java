package models;

/**
 *
 * @author PC WHITE WOLF
 */
public interface ISubject {
    public void agregarObserver(IObserver observador);
    public void quitarObserver(IObserver observador);
    public void notificarObservadores(String mensaje);
}
