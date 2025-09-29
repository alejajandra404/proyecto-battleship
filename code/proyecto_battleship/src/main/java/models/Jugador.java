package models;

import enums.EstadoCasilla;
import enums.ResultadoDisparo;

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
    
    private String nombre;
    private String color;
    private TableroNaves tableroNaves;
    private TableroDisparos tableroDisparos;

    /**
     * Constructor para crear un nuevo Jugador
     * @param nombre El nombre del jugador
     */
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.tableroNaves = new TableroNaves(this);
        this.tableroDisparos = new TableroDisparos(this);
    }

    /**
     * Devuelve el nombre del jugador
     * @return El nombre del jugador
     */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Devuelve el color asignado al jugador
     * @return El color del jugador
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Marca un disparo realizado por este jugador en su tablero de disparos
     * @param disparo El objeto Disparo que contiene el resultado y la coordenada
     */
    public void marcarDisparo(Disparo disparo) {
        this.tableroDisparos.añadirDisparo(disparo);
    }

    /**
     * Procesa un disparo recibido de un oponente en una coordenada específica
     * Delega la acción a su tablero de naves
     * @param coordenada La coordenada donde el oponente ha disparado
     * @return Un objeto Disparo con el resultado (AGUA, TOCADO, HUNDIDO)
     */
    public Disparo recibirDisparo(Coordenada coordenada) {
        EstadoCasilla estadoCasilla = this.tableroNaves.recibirImpacto(coordenada);
        
        ResultadoDisparo resultadoDisparo;
        if (estadoCasilla == EstadoCasilla.IMPACTADA_OCUPADA) {
            resultadoDisparo = ResultadoDisparo.IMPACTO;
        } else {
            resultadoDisparo = ResultadoDisparo.AGUA;
        }
        
        return new Disparo(coordenada, resultadoDisparo, this);
    }

    /**
     * Valida si un disparo en una coordenada es válido (por ejemplo, si no se ha
     * disparado antes en esa misma casilla)
     * @param coordenada La coordenada a validar
     * @return true si el disparo es válido, false en caso contrario
     */
    public boolean validarDisparo(Coordenada coordenada) {
        return this.tableroDisparos.validarDisparo(coordenada);
    }
}