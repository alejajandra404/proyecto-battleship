package patterns.observer;

/**
 * ISubject.java
 *
 * Interfaz del patrón Observer que define el contrato para los sujetos observables.
 * Los sujetos mantienen una lista de observadores y los notifican cuando ocurren cambios.
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
public interface ISubject {

    /**
     * Agrega un observador a la lista de observadores.
     * El observador será notificado cuando ocurran cambios en el sujeto.
     *
     * @param observador El observador a agregar
     */
    public void agregarObserver(IObserver observador);

    /**
     * Quita un observador de la lista de observadores.
     * El observador dejará de recibir notificaciones.
     *
     * @param observador El observador a quitar
     */
    public void quitarObserver(IObserver observador);

    /**
     * Notifica a todos los observadores sobre un cambio en el sujeto.
     * Llama al método notificar de cada observador registrado.
     *
     * @param mensaje Mensaje descriptivo del cambio
     */
    public void notificarObservadores(String mensaje);
}