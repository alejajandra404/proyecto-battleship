package dtos;

import enums.EstadoNave;
import enums.OrientacionNave;
import enums.TipoNave;
import java.io.Serializable;

/**
 * NaveDTO - Data Transfer Object para Nave
 *
 * Se usa para transferir información de naves sin exponer la lógica interna del
 * modelo
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class NaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private TipoNave tipo;
    private EstadoNave estado;
    private OrientacionNave orientacion;
    private CoordenadaDTO[] coordenadas;
    private int impactosRecibidos;
    private int longitudTotal;

    /**
     * Constructor vacío
     */
    public NaveDTO() {
    }

    /**
     * Constructor completo
     *
     * @param tipo
     * @param estado
     * @param orientacion
     * @param coordenadas
     * @param impactosRecibidos
     */
    public NaveDTO(TipoNave tipo, EstadoNave estado, OrientacionNave orientacion,
            CoordenadaDTO[] coordenadas, int impactosRecibidos) {
        this.tipo = tipo;
        this.estado = estado;
        this.orientacion = orientacion;
        this.coordenadas = coordenadas;
        this.impactosRecibidos = impactosRecibidos;
        this.longitudTotal = coordenadas.length;
    }

    // Getters y Setters
    public TipoNave getTipo() {
        return tipo;
    }

    public void setTipo(TipoNave tipo) {
        this.tipo = tipo;
    }

    public EstadoNave getEstado() {
        return estado;
    }

    public void setEstado(EstadoNave estado) {
        this.estado = estado;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(OrientacionNave orientacion) {
        this.orientacion = orientacion;
    }

    public CoordenadaDTO[] getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(CoordenadaDTO[] coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getImpactosRecibidos() {
        return impactosRecibidos;
    }

    public void setImpactosRecibidos(int impactosRecibidos) {
        this.impactosRecibidos = impactosRecibidos;
    }

    public int getLongitudTotal() {
        return longitudTotal;
    }

    public void setLongitudTotal(int longitudTotal) {
        this.longitudTotal = longitudTotal;
    }

    /**
     * Verifica si la nave está hundida
     *
     * @return
     */
    public boolean estaHundida() {
        return impactosRecibidos >= longitudTotal;
    }

    @Override
    public String toString() {
        return "NaveDTO{"
                + "tipo=" + tipo
                + ", estado=" + estado
                + ", impactos=" + impactosRecibidos + "/" + longitudTotal
                + '}';
    }

}
