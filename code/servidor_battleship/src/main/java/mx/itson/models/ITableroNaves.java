package mx.itson.models;

import java.util.List;
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
    
    /**
     * 
     * @param coordenada
     * @return 
     */
    public Nave encontrarNaveEnCoordenada(Coordenada coordenada);
    
    /**
     * 
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public EstadoCasilla recibirImpacto(Coordenada coordenada) throws ModelException;
    
    /**
     * 
     * @return 
     */
    public boolean navesColocadas();
    
    /**
     * 
     * @return 
     */
    public int getNavesHundidas();
    
    /**
     * 
     * @return 
     */
    public List<Nave> getNaves();
    
    /**
     * 
     * @return 
     */
    public Casilla[][] getCasillas();
    
    /**
     * 
     * @return 
     */
    public int getTotalNaves();
    
    /**
     * 
     * @return 
     */
    public boolean todasNavesHundidas();
}