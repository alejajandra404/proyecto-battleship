package models;
import enums.EstadoNave;
import enums.OrientacionNave;
import enums.TipoNave;
/**
 * Nave.java
 *
 * Clase entidad que representa una nave dentro del tablero.
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
public class Nave {
    
    private TipoNave tipo;
    private EstadoNave estado;
    private OrientacionNave orientacion;
    private Coordenada[] coordenadas;
    private int impactosRecibidos;
    
    /**
     * Constructor para crear una nueva Nave
     * @param tipo El tipo de nave (ej. PORTAAVIONES, CRUCERO)
     * @param coordenadas El arreglo de coordenadas que ocupa la nave en el tablero
     * @param orientacion La orientación de la nave (VERTICAL u HORIZONTAL)
     */
    public Nave(TipoNave tipo, Coordenada[] coordenadas, OrientacionNave orientacion) {
        this.tipo = tipo;
        this.coordenadas = coordenadas;
        this.orientacion = orientacion;
        this.estado = EstadoNave.INTACTA; //Todas las naves empiezan intactas
        this.impactosRecibidos = 0;
    }

    /**
     * Procesa un disparo en una coordenada específica. La lógica para encontrar
     * y registrar el impacto está consolidada dentro de este método
     */
    public void recibirImpacto(Coordenada coordenadaImpacto) {
    }

    /**
     * Verifica y actualiza el estado de la nave (INTACTA, AVERIADA, HUNDIDA)
     * basado en el número de impactos recibidos
     * @return El nuevo estado de la nave
     */
    private EstadoNave verificarEstado() {
        if (impactosRecibidos == 0) {
            this.estado = EstadoNave.INTACTA;
        } else if (impactosRecibidos >= getLongitud()) {
            this.estado = EstadoNave.HUNDIDA;
        } else {
            this.estado = EstadoNave.AVERIADA;
        }
        return this.estado;
    }

    /**
     * Comprueba si la nave ha sido hundida
     * @return true si la nave está hundida, false en caso contrario
     */
    public boolean estaHundida() {
        return this.impactosRecibidos >= getLongitud();
    }

    /**
     * Obtiene la longitud de la nave, que es equivalente al número de coordenadas que ocupa
     * @return El tamaño de la nave
     */
    public int getLongitud() {
        return this.coordenadas.length;
    }

    // --- Getters --- //
    public TipoNave getTipo() {
        return tipo;
    }

    public OrientacionNave getOrientacion() {
        return orientacion;
    }
    
    public EstadoNave getEstado() {
        return estado;
    }
    
    public Coordenada[] getCoordenadas() {
        return coordenadas;
    }
}