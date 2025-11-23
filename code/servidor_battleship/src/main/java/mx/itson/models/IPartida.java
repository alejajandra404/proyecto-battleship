package mx.itson.models;

import java.util.List;
import java.util.function.Consumer;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.NaveDTO;

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
     * @return 
     */
    public String obtenerIdPartida();
    
    /**
     * 
     * @return 
     */
    public boolean ambosJugadoresListos();
    
    /**
     * 
     * @return 
     */
    public int obtenerTiempoRestante();
    
    /**
     * 
     * @return 
     */
    public String obtenerIdJugadorEnTurno();
    
    /**
     * 
     * @return 
     */
    public String obtenerIdGanador();
    
    /**
     * 
     * @param idJugadorDispara
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) throws ModelException;
    
    /**
     * 
     * @param naves
     * @return
     * @throws ModelException 
     */
    public boolean colocarNaves(String idJugador, List<NaveDTO> naves) throws ModelException;
    
    /**
     * 
     * @param naveNueva
     * @param idJugador
     * @return 
     * @throws ModelException 
     */
    public boolean agregarNave(Nave naveNueva, String idJugador) throws ModelException;
    
    /**
     * 
     * @param coordenadas
     * @param idJugador
     * @return 
     * @throws ModelException 
     */
    public boolean quitarNave(Coordenada[] coordenadas, String idJugador) throws ModelException;
    
    /**
     * 
     * @param callback 
     */
    public void establecerCallbackTimeout(Consumer<String> callback);
    
    /**
     * 
     */
    public void iniciarTemporizador();
    
    /**
     * 
     */
    public void detenerTemporizador();
    
    /**
     * 
     */
    public void limpiarRecursos();
}