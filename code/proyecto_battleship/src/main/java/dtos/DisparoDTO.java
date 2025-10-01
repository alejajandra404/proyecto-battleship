/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import enums.ResultadoDisparo;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DisparoDTO - Data Transfer Object para Disparo
 *
 * Se usa para transferir información de disparos en la red y en la vista.
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class DisparoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CoordenadaDTO coordenada;
    private ResultadoDisparo resultado;
    private LocalDateTime timestamp;
    private String nombreJugador;
    private String mensaje; // Para notificaciones

    /**
     * Constructor vacío (necesario para serialización)
     */
    public DisparoDTO() {
    }

    /**
     * Constructor con parámetros básicos
     *
     * @param coordenada
     * @param nombreJugador
     */
    public DisparoDTO(CoordenadaDTO coordenada, String nombreJugador) {
        this.coordenada = coordenada;
        this.nombreJugador = nombreJugador;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor completo
     *
     * @param coordenada
     * @param resultado
     * @param nombreJugador
     * @param mensaje
     */
    public DisparoDTO(CoordenadaDTO coordenada, ResultadoDisparo resultado,
            String nombreJugador, String mensaje) {
        this.coordenada = coordenada;
        this.resultado = resultado;
        this.nombreJugador = nombreJugador;
        this.timestamp = LocalDateTime.now();
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public CoordenadaDTO getCoordenada() {
        return coordenada;
    }

    public void setCoordenada(CoordenadaDTO coordenada) {
        this.coordenada = coordenada;
    }

    public ResultadoDisparo getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoDisparo resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "DisparoDTO{"
                + "coordenada=" + coordenada
                + ", resultado=" + resultado
                + ", nombreJugador='" + nombreJugador + '\''
                + ", timestamp=" + timestamp
                + '}';
    }

}
