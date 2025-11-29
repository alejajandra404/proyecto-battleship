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
public interface ITableroDisparos {
    /**
     * 
     * @param disparo
     * @throws ModelException 
     */
    public void añadirDisparo(Disparo disparo) throws ModelException;
    
    /**
     * 
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public boolean validarDisparo(Coordenada coordenada) throws ModelException;
    
    /**
     * 
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public boolean yaDisparado(Coordenada coordenada)  throws ModelException;
}