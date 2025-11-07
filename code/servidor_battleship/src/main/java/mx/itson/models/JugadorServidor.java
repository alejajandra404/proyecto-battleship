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
public class JugadorServidor implements IJugador{
    private final String nombre;
    private String color;
    private final ITableroNaves tableroNaves;
    private final ITableroDisparos tableroDisparos;

    public JugadorServidor(String nombre, ITableroNaves tableroNaves, ITableroDisparos tableroDisparos) {
        this.nombre = nombre;
        this.tableroNaves = tableroNaves;
        this.tableroDisparos = tableroDisparos;
    }
    
    /**
     * Devuelve el nombre del jugador
     * @return El nombre del jugador
     */
    public String getNombre() {return this.nombre;}

    /**
     * Devuelve el color asignado al jugador
     * @return El color del jugador
     */
    public String getColor() {return this.color;}
    
    /**
     * Obtiene el tablero de naves del jugador 
     * 
     * @return 
     */
    public ITableroNaves getTableroNaves() {return this.tableroNaves;}

    /**
     * Obtiene el tablero de disparos del jugador 
     * 
     * @return 
     */
    public ITableroDisparos getTableroDisparos() {return this.tableroDisparos;}
    
    @Override
    public void marcarDisparo(Disparo disparo) throws ModelException {this.tableroDisparos.añadirDisparo(disparo);}
    
    @Override
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws ModelException {return this.tableroNaves.recibirImpacto(coordenada);}
    
    @Override
    public boolean validarDisparo(Disparo disparo) throws ModelException {return this.tableroDisparos.validarDisparo(disparo);}

    @Override
    public boolean añadirNave(Nave nave) throws ModelException {return this.tableroNaves.añadirNave(nave);}

    @Override
    public boolean eliminarNave(Coordenada[] coordenadas) throws ModelException {return this.tableroNaves.eliminarNave(coordenadas);}
}