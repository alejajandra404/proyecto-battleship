package mx.itson.models;

import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;

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
public interface ITableroNaves {
    /**
     * 
     * @param nave
     * @return
     * @throws ModelException 
     */
    public boolean añadirNave(Nave nave) throws ModelException;
    
    /**
     * 
     * @param coordenadas
     * @return
     * @throws ModelException 
     */
    public boolean eliminarNave(Coordenada[] coordenadas) throws ModelException;
    
    /**
     * 
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public EstadoCasilla recibirImpacto(Coordenada coordenada) throws ModelException;
}