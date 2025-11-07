package mx.itson.models;

import java.time.LocalDateTime;
import mx.itson.utils.enums.ResultadoDisparo;

/**
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
 */
public class Disparo {
     
    private final Coordenada coordenada;
    private final ResultadoDisparo resultado;
    private final LocalDateTime fechaHora;
    private final IJugador jugador;

    /**
     * Constructor para crear un nuevo objeto Disparo
     * @param coordenada La coordenada donde se realizó el disparo
     * @param resultado El resultado del disparo (IMPACTO o AGUA)
     * @param jugador El jugador que realizó el disparo
     */
    public Disparo(Coordenada coordenada, ResultadoDisparo resultado, IJugador jugador) {
        this.coordenada = coordenada;
        this.resultado = resultado;
        this.jugador = jugador;
        this.fechaHora = LocalDateTime.now();
    }
    
    /**
     * Constructor sin resultado 
     * 
     * @param coordenada
     */
    public Disparo(Coordenada coordenada) {
        this.coordenada = coordenada;
        this.fechaHora = java.time.LocalDateTime.now();
        this.jugador = null; // Se establece después
        this.resultado = null; // Se establece después
    }
    
    // Getters
    
    /**
     * 
     * @return 
     */
    public LocalDateTime getFechaHora() {return fechaHora;}

    /**
     * 
     * @return 
     */
    public IJugador getJugador() {return jugador;}

    /**
     * Devuelve el resultado del disparo
     * @return El enum ResultadoDisparo
     */
    public ResultadoDisparo obtenerResultado() {return this.resultado;}

    /**
     * Devuelve la coordenada del disparo
     * @return El objeto Coordenada del disparo
     */
    public Coordenada obtenerCoordenada() {return this.coordenada;}
    
    /**
     * Verifica si el disparo fue un impacto a una nave
     * @return true si el resultado fue IMPACTO
     */
    public boolean esImpacto() {
        return this.resultado == ResultadoDisparo.IMPACTO_AVERIADA || this.resultado == ResultadoDisparo.IMPACTO_HUNDIDA;
    }
}