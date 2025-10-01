package models;

import enums.EstadoCasilla;
import enums.ResultadoDisparo;
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
    public Disparo recibirDisparo(Coordenada coordenada) throws TableroException, CasillaException {
        
        EstadoCasilla estadoCasilla = this.tableroNaves.recibirImpacto(coordenada);
        // Resultado del disparo a añadir a una instancia de Disparo
        ResultadoDisparo resultadoDisparo;
        
        // Si el estado de la casilla impactada, es averiada o hundida, se actualiza el resultado del Diparo como IMPACTO
        if (estadoCasilla == EstadoCasilla.IMPACTADA_AVERIADA || estadoCasilla == EstadoCasilla.IMPACTADA_HUNDIDA)
            resultadoDisparo = ResultadoDisparo.IMPACTO;
        // De lo contrario, se actualiza el resultado como AGUA.
        else 
            resultadoDisparo = ResultadoDisparo.AGUA;
        // Se regresa una nueva instancia de un Disparo, con su resultado anterior.
        return new Disparo(coordenada, resultadoDisparo, this);
    }

    /**
     * Valida si un disparo en una coordenada es válido (por ejemplo, si no se ha
     * disparado antes en esa misma casilla)
     * @param disparo
     * @return true si el disparo es válido, false en caso contrario
     * @throws exceptions.TableroException
     */
    public boolean validarDisparo(Coordenada disparo) throws TableroException {return this.tableroDisparos.validarCoordenada(disparo);}
}