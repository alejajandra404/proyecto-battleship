package mx.itson.utils.dtos;

import java.awt.Color;
import java.io.Serializable;

/**
 * DTO para la solicitud de registro de un jugador
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class RegistroJugadorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private Color color;

    /**
     * Constructor por defecto
     */
    public RegistroJugadorDTO() {
    }

    /**
     * Constructor con parámetros
     * @param nombre Nombre del jugador
     * @param color Color seleccionado
     */
    public RegistroJugadorDTO(String nombre, Color color) {
        this.nombre = nombre;
        this.color = color;
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

    @Override
    public String toString() {
        return "RegistroJugadorDTO{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
