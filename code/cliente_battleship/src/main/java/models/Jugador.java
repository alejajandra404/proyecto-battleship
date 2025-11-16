package models;

import enums.EstadoCasilla;
import exceptions.CasillaException;
import exceptions.TableroException;

/**
 * Jugador.java
 *
 * Clase entidad que representa un jugador dentro de la partida.
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
 *
 */
public class Jugador {
    
    private final String nombre;
    private String color;
    private final TableroNaves tableroNaves;
    private final TableroDisparos tableroDisparos;

    /**
     * Constructor para crear un nuevo Jugador
     * @param nombre El nombre del jugador
     * @throws exceptions.TableroException
     */
    public Jugador(String nombre) throws TableroException {
        this.nombre = nombre;
        this.tableroNaves = new TableroNaves(this, 10);
        this.tableroDisparos = new TableroDisparos(this, 10);
    }

    public Jugador(String nombre, TableroNaves tableroNaves, TableroDisparos tableroDisparos) {
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
    public TableroNaves getTableroNaves() {
        return this.tableroNaves;
    }

    /**
     * Obtiene el tablero de disparos del jugador 
     * 
     * @return 
     */
    public TableroDisparos getTableroDisparos() {
        return this.tableroDisparos;
    }

    /**
     * Marca un disparo realizado por este jugador en su tablero de disparos
     * @param disparo El objeto Disparo que contiene el resultado y la coordenada
     * @throws exceptions.TableroException
     */
    public void marcarDisparo(Disparo disparo) throws TableroException {this.tableroDisparos.añadirDisparo(disparo);}

    /**
     * Procesa un disparo recibido de un oponente en una coordenada específica
     * Delega la acción a su tablero de naves
     * @param coordenada La coordenada donde el oponente ha disparado
     * @return Un objeto Disparo con el resultado (AGUA, TOCADO, HUNDIDO)
     * @throws exceptions.TableroException
     * @throws exceptions.CasillaException
     */
    public EstadoCasilla recibirDisparo(Coordenada coordenada) throws TableroException, CasillaException {return this.tableroNaves.recibirImpacto(coordenada);}

    /**
     * Valida si un disparo en una coordenada es válido (por ejemplo, si no se ha
     * disparado antes en esa misma casilla)
     * @param disparo
     * @return true si el disparo es válido, false en caso contrario
     * @throws exceptions.TableroException
     */
    public boolean validarDisparo(Coordenada disparo) throws TableroException {return this.tableroDisparos.validarCoordenada(disparo);}
}