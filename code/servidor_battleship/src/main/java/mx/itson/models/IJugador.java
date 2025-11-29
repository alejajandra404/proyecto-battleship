package mx.itson.models;

import java.awt.Color;
import java.util.List;
import mx.itson.utils.enums.EstadoCasilla;
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
public interface IJugador {
    /**
     * 
     * @return 
     */
    public String getId();
    
    /**
     * 
     * @return 
     */
    public String getNombre();
    
    /**
     * 
     * @return 
     */
    public Color getColor();
    
    /**
     * 
     * @return 
     */
    public ITableroDisparos getTableroDisparos();
    
    /**
     * 
     * @return 
     */
    public ITableroNaves getTableroNaves();
    
    /**
     * 
     * @return 
     */
    public boolean isEnPartida();
    
    /**
     * Marca un disparo realizado por este jugador en su tablero de disparos
     * @param disparo El objeto Disparo que contiene el resultado y la coordenada
     * @throws ModelException
     */
    public void marcarDisparo(Disparo disparo) throws ModelException;
    
    /**
     * Procesa un disparo recibido de un oponente en una coordenada específica
     * Delega la acción a su tablero de naves
     * @param coordenada La coordenada donde el oponente ha disparado
     * @return Un objeto Disparo con el resultado (AGUA, TOCADO, HUNDIDO)
     * @throws ModelException
     */
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException;
    
    /**
     * Valida si un disparo en una coordenada es válido (por ejemplo, si no se ha
     * disparado antes en esa misma casilla)
     * @param coordenada
     * @return true si el disparo es válido, false en caso contrario
     * @throws ModelException
     */
    public boolean validarDisparo(Coordenada coordenada) throws ModelException;
    
    /**
     * 
     * @param nave
     * @return
     * @throws ModelException 
     */
    public boolean añadirNave(Nave nave) throws ModelException;
    
    /**
     * 
     * @param nave
     * @return
     * @throws ModelException 
     */
    public boolean eliminarNave(Nave nave) throws ModelException;
    
    /**
     * 
     * @param naves
     * @return
     * @throws ModelException 
     */
    public boolean colocarNaves(List<Nave> naves) throws ModelException;
}