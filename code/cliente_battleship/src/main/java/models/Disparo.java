package models;
import enums.ResultadoDisparo;
import java.time.LocalDateTime;
/**
 * Disparo.java
 *
 * Clase entidad que representa el disparo que
 * puede realizar un jugador durante la partida.
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
public class Disparo {
     
    private final Coordenada coordenada;
    private final ResultadoDisparo resultado;
    private final LocalDateTime timestamp;
    private final Jugador jugador;

    /**
     * Constructor para crear un nuevo objeto Disparo
     * @param coordenada La coordenada donde se realizó el disparo
     * @param resultado El resultado del disparo (IMPACTO o AGUA)
     * @param jugador El jugador que realizó el disparo
     */
    public Disparo(Coordenada coordenada, ResultadoDisparo resultado, Jugador jugador) {
        this.coordenada = coordenada;
        this.resultado = resultado;
        this.jugador = jugador;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor sin resultado 
     * 
     * @param coordenada
     */
    public Disparo(Coordenada coordenada) {
        this.coordenada = coordenada;
        this.timestamp = java.time.LocalDateTime.now();
        this.jugador = null; // Se establece después
        this.resultado = null; // Se establece después
    }
    
    // Getters
    public Coordenada getCoordenada() {
        return coordenada;
    }

    public ResultadoDisparo getResultado() {
        return resultado;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Jugador getJugador() {
        return jugador;
    }
    
    /**
     * Procesa y devuelve una representación en texto del disparo.
     * @return Un String describiendo la jugada.
     */
    public String procesarDisparo() {
        return "El jugador " + jugador.getNombre()+ " disparó en " + coordenada.toString() + " con resultado: " + resultado;
    }

    /**
     * Devuelve el resultado del disparo
     * @return El enum ResultadoDisparo
     */
    public ResultadoDisparo obtenerResultado() {
        return this.resultado;
    }

    /**
     * Verifica si el disparo fue un impacto a una nave
     * @return true si el resultado fue IMPACTO
     */
    public boolean esImpacto() {
        return this.resultado == ResultadoDisparo.IMPACTO;
    }

    /**
     * Devuelve la coordenada del disparo
     * @return El objeto Coordenada del disparo
     */
    public Coordenada obtenerCoordenada() {
        return this.coordenada;
    }
}
