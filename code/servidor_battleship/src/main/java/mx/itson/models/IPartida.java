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
    public String getIdPartida();
    
    /**
     * 
     * @return 
     */
    public IJugador getJugador1();
    
    /**
     * 
     * @return 
     */
    public IJugador getJugador2();
    
    /**
     * 
     * @return 
     */
    public String getIdJugadorEnTurno();
    
    /**
     * 
     * @return 
     */
    public String getIdGanador();
    
    /**
     * 
     * @return 
     */
    public boolean ambosJugadoresListos();
    
    /**
     * 
     * @return 
     */
    public int getTiempoRestante();
    
    /**
     * 
     * @return 
     */
//    public String getIdGanador();
    
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
     * @param idJugador
     * @param naves
     * @return
     * @throws ModelException 
     */
    public boolean colocarNaves(String idJugador, List<Nave> naves) throws ModelException;
    
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
     */
    public void manejarTiempoAgotado();
    
    /**
     * 
     * @param callback 
     */
    public void establecerRespuestaTiempoAgotado(Consumer<String> callback);
    
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