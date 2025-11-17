package mx.itson.utils.dtos;

import java.awt.Color;
import java.io.Serializable;

/**
 * DTO para transferir información del jugador entre cliente y servidor
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class JugadorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private Color color;
    private boolean enPartida;

    /**
     * Constructor por defecto
     */
    public JugadorDTO() {
    }

    /**
     * Constructor con parámetros
     * @param id ID único del jugador
     * @param nombre Nombre del jugador
     * @param color Color seleccionado por el jugador
     */
    public JugadorDTO(String id, String nombre, Color color) {
        this.id = id;
        this.nombre = nombre;
        this.color = color;
        this.enPartida = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isEnPartida() {
        return enPartida;
    }

    public void setEnPartida(boolean enPartida) {
        this.enPartida = enPartida;
    }

    @Override
    public String toString() {
        return "JugadorDTO{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", enPartida=" + enPartida +
                '}';
    }
}
