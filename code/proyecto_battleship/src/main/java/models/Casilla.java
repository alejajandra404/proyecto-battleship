package models;
import enums.EstadoCasilla;
import exceptions.CasillaException;

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
public class Casilla extends Coordenada{
    
    private EstadoCasilla estado;

    /**
     * Constructor para una nueva Casilla
     * Es obligatorio especificar su coordenada al crearla
     * @param coordenada La coordenada que esta casilla representa en el tablero
     */
    public Casilla(Coordenada coordenada) {
        super(coordenada.obtenerX(), coordenada.obtenerY());
        this.estado = EstadoCasilla.VACIA;
    }
    /**
     * 
     * @throws CasillaException 
     */
    public void ocuparCasilla() throws CasillaException{
        if(estado != EstadoCasilla.VACIA)
            throw new CasillaException("La casilla ya se encuentra ocupada o impactada.");
        else
            estado = EstadoCasilla.OCUPADA;
    }
    /**
     * 
     * @throws CasillaException 
     */
    public void marcarImpactoAgua() throws CasillaException{
        if(estado != EstadoCasilla.VACIA)
            throw new CasillaException("La casilla no está vacía.");
        else
            estado = EstadoCasilla.IMPACTADA_VACIA;
    }
    /**
     * 
     * @throws CasillaException 
     */
    public void marcarImpactoAveriada() throws CasillaException{
        if(estado != EstadoCasilla.OCUPADA)
            throw new CasillaException("La casilla ya ha recibido un impacto previo o está vacía.");
        else
            estado = EstadoCasilla.IMPACTADA_AVERIADA;
    }
    /**
     * 
     * @throws CasillaException 
     */
    public void marcarImpactoHundida() throws CasillaException{
        if(estado != EstadoCasilla.IMPACTADA_AVERIADA)
            throw new CasillaException("La casilla no corresponde con un barco averiado.");
        else
            estado = EstadoCasilla.IMPACTADA_HUNDIDA;
    }

    /**
     * Verifica si la casilla está ocupada por una nave
     * @return true si el estado es OCUPADA o IMPACTADA_OCUPADA
     */
    public boolean estaOcupada() {
        return this.estado != EstadoCasilla.VACIA && this.estado != EstadoCasilla.IMPACTADA_VACIA;
    }

    /**
     * Verifica si la casilla ya ha sido impactada
     * @return true si el estado es IMPACTADA_VACIA o IMPACTADA_OCUPADA
     */
    public boolean estaImpactada() {
        return 
                this.estado == EstadoCasilla.IMPACTADA_VACIA 
                    || 
                this.estado == EstadoCasilla.IMPACTADA_AVERIADA 
                    || 
                this.estado == EstadoCasilla.IMPACTADA_HUNDIDA;
    }

    /**
     * Devuelve el estado actual de la casilla
     * @return El enum EstadoCasilla que representa el estado actual
     */
    public EstadoCasilla obtenerEstado() {return this.estado;}
}