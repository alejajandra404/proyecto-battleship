/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.EstadoPartida;
import java.io.Serializable;

/**
 * EstadoPartidaDTO - Data Transfer Object para el estado completo de la partida
 *
 * Se usa para sincronizar el estado completo entre jugadores
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class EstadoPartidaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreJugador1;
    private String nombreJugador2;
    private String nombreJugadorTurno;
    private EstadoPartida estadoPartida;
    private int tiempoRestante;
    private String ganador;

    /**
     * Constructor vacío
     */
    public EstadoPartidaDTO() {
    }

    /**
     * Constructor completo
     *
     * @param nombreJugador1
     * @param nombreJugador2
     * @param nombreJugadorTurno
     * @param estadoPartida
     * @param tiempoRestante
     */
    public EstadoPartidaDTO(String nombreJugador1, String nombreJugador2,
            String nombreJugadorTurno, EstadoPartida estadoPartida,
            int tiempoRestante) {
        this.nombreJugador1 = nombreJugador1;
        this.nombreJugador2 = nombreJugador2;
        this.nombreJugadorTurno = nombreJugadorTurno;
        this.estadoPartida = estadoPartida;
        this.tiempoRestante = tiempoRestante;
    }

    // Getters y Setters
    public String getNombreJugador1() {
        return nombreJugador1;
    }

    public void setNombreJugador1(String nombreJugador1) {
        this.nombreJugador1 = nombreJugador1;
    }

    public String getNombreJugador2() {
        return nombreJugador2;
    }

    public void setNombreJugador2(String nombreJugador2) {
        this.nombreJugador2 = nombreJugador2;
    }

    public String getNombreJugadorTurno() {
        return nombreJugadorTurno;
    }

    public void setNombreJugadorTurno(String nombreJugadorTurno) {
        this.nombreJugadorTurno = nombreJugadorTurno;
    }

    public EstadoPartida getEstadoPartida() {
        return estadoPartida;
    }

    public void setEstadoPartida(EstadoPartida estadoPartida) {
        this.estadoPartida = estadoPartida;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    @Override
    public String toString() {
        return "EstadoPartidaDTO{"
                + "jugadores=" + nombreJugador1 + " vs " + nombreJugador2
                + ", turno=" + nombreJugadorTurno
                + ", estado=" + estadoPartida
                + '}';
    }

}
