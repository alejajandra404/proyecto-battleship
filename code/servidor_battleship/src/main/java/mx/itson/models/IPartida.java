package mx.itson.models;

import mx.itson.exceptions.ModelException;

/**
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
public interface IPartida {
    /**
     * 
     * @throws ModelException 
     */
    public void iniciarPartida() throws ModelException;
    
    /**
     * 
     * @throws ModelException 
     */
    public void cambiarTurno() throws ModelException;
    
    /**
     * Valida si un disparo es legal (turno correcto y casilla no repetida)
     * @param disparo El disparo a validar
     * @param jugador El jugador que intenta disparar
     * @return true si el disparo es válido
     * @throws ModelException
     */
    public boolean validarDisparo(Disparo disparo, IJugador jugador) throws ModelException;
    
    /**
     * Procesa un disparo de un jugador a otro
     * @param coordenada La coordenada del disparo
     * @param jugadorAtacante El jugador que realiza el disparo
     * @return true si el disparo fue procesado, false si no fue válido
     * @throws ModelException
     */
    public boolean recibirDisparo(Coordenada coordenada, IJugador jugadorAtacante) throws ModelException;
    
    /**
     * Verifica si el jugador proporcionado es el que tiene el turno actual
     * @param jugador El jugador a verificar
     * @return true si es el turno de ese jugador
     * @throws ModelException
     */
    public boolean verificarJugadorTurno(IJugador jugador) throws ModelException;
    
    /**
     * 
     * @param naveNueva
     * @param jugador
     * @throws ModelException 
     */
    public void agregarNave(Nave naveNueva, IJugador jugador) throws ModelException;
    
    /**
     * 
     * @param coordenadas
     * @param jugador
     * @throws ModelException 
     */
    public void quitarNave(Coordenada[] coordenadas, IJugador jugador) throws ModelException;
}