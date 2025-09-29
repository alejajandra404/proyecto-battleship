package models;
import enums.EstadoCasilla;

/**
 * Casilla.java
 *
 * Clase entidad que representa una casilla dentro del tablero.
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
public class Casilla {
    private final Coordenada coordenada; 
    
    private EstadoCasilla estado;
    private Nave nave;

    /**
     * Constructor para una nueva Casilla
     * Es obligatorio especificar su coordenada al crearla
     * @param coordenada La coordenada que esta casilla representa en el tablero
     */
    public Casilla(Coordenada coordenada) {
        this.coordenada = coordenada;
        this.estado = EstadoCasilla.VACIA;
        this.nave = null;
    }

    /**
     * Marca la casilla como impactada y actualiza su estado
     */
    public void marcarImpacto() {
        if (this.estado == EstadoCasilla.OCUPADA) {
            this.estado = EstadoCasilla.IMPACTADA_OCUPADA;
            if (this.nave != null) {
                this.nave.recibirImpacto(coordenada);
            }
        } else if (this.estado == EstadoCasilla.VACIA) {
            this.estado = EstadoCasilla.IMPACTADA_VACIA;
        }
    }

    /**
     * Verifica si la casilla está ocupada por una nave
     * @return true si el estado es OCUPADA o IMPACTADA_OCUPADA
     */
    public boolean estaOcupada() {
        return this.estado == EstadoCasilla.OCUPADA || this.estado == EstadoCasilla.IMPACTADA_OCUPADA;
    }

    /**
     * Verifica si la casilla ya ha sido impactada
     * @return true si el estado es IMPACTADA_VACIA o IMPACTADA_OCUPADA
     */
    public boolean estaImpactada() {
        return this.estado == EstadoCasilla.IMPACTADA_VACIA || this.estado == EstadoCasilla.IMPACTADA_OCUPADA;
    }

    /**
     * Devuelve el estado actual de la casilla
     * @return El enum EstadoCasilla que representa el estado actual
     */
    public EstadoCasilla obtenerEstado() {
        return this.estado;
    }
}