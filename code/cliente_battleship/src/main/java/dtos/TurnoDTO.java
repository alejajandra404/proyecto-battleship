/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.io.Serializable;

/**
 * TurnoDTO - Data Transfer Object para información del turno
 *
 * Se usa para sincronizar información del turno entre jugadores
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class TurnoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreJugadorTurno;
    private int tiempoRestante; // en segundos
    private boolean partidaEnCurso;
    private String mensaje;

    /**
     * Constructor vacío
     */
    public TurnoDTO() {
    }

    /**
     * Constructor con parámetros
     *
     * @param nombreJugadorTurno
     * @param tiempoRestante
     * @param partidaEnCurso
     * @param mensaje
     */
    public TurnoDTO(String nombreJugadorTurno, int tiempoRestante,
            boolean partidaEnCurso, String mensaje) {
        this.nombreJugadorTurno = nombreJugadorTurno;
        this.tiempoRestante = tiempoRestante;
        this.partidaEnCurso = partidaEnCurso;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public String getNombreJugadorTurno() {
        return nombreJugadorTurno;
    }

    public void setNombreJugadorTurno(String nombreJugadorTurno) {
        this.nombreJugadorTurno = nombreJugadorTurno;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public boolean isPartidaEnCurso() {
        return partidaEnCurso;
    }

    public void setPartidaEnCurso(boolean partidaEnCurso) {
        this.partidaEnCurso = partidaEnCurso;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "TurnoDTO{"
                + "jugadorTurno='" + nombreJugadorTurno + '\''
                + ", tiempo=" + tiempoRestante + "s"
                + '}';
    }

}
