package models;
import enums.EstadoPartida;
/**
 * Partida.java
 *
 * Clase entidad que representa una paritda.
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
public class Partida {
    private Jugador jugadorTurno;
    private EstadoPartida estado;
    private Jugador jugador1;
    private Jugador jugador2;

    public static final int TIEMPO_TURNO = 30; // Tiempo por turno en segundos
    private int tiempoRestante;
    private boolean temporizadorActivo;
    
    /**
     * Constructor para iniciar una nueva partida
     * @param jugador1 El primer jugador
     * @param jugador2 El segundo jugador
     */
    public Partida(Jugador jugador1, Jugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.estado = EstadoPartida.EN_CURSO;
        this.jugadorTurno = this.jugador1;
    }
    
    public void iniciarPartida() {

    }

    public void cambiarTurno() {
    }

    public boolean verificarFinPartida() {
        return false;
    }

    public Jugador determinarGanador() {
        return null;
    }
    
    public Jugador obtenerJugadorTurno() {
        return this.jugadorTurno;
    }

    /**
     * Verifica si el jugador proporcionado es el que tiene el turno actual
     * @param jugador El jugador a verificar
     * @return true si es el turno de ese jugador
     */
    public boolean verificarJugadorTurno(Jugador jugador) {
        return this.jugadorTurno == jugador;
    }

    /**
     * Pausa el flujo del temporizador
     */
    public void pausarTemporizador() {
        this.temporizadorActivo = false;
    }

    /**
     * Reanuda el flujo del temporizador si estaba pausado
     */
    public void reanudarTemporizador() {
        this.temporizadorActivo = true;
    }

    /**
     * Reinicia el contador de tiempo al valor inicial y lo activa
     * Se usaría al inicio de cada turno
     */
    public void reiniciarTemporizador() {
        this.tiempoRestante = TIEMPO_TURNO;
        this.temporizadorActivo = true;
    }
    
    /**
     * Valida si un disparo es legal (turno correcto y casilla no repetida)
     * @param coordenada La coordenada del disparo
     * @param jugador El jugador que intenta disparar
     * @return true si el disparo es válido
     */
    public boolean validarDisparo(Coordenada coordenada, Jugador jugador) {
        return verificarJugadorTurno(jugador) && jugador.validarDisparo(coordenada);
    }

    /**
     * Procesa un disparo de un jugador a otro
     * @param coordenada La coordenada del disparo
     * @param jugadorAtacante El jugador que realiza el disparo
     * @return true si el disparo fue procesado, false si no fue válido
     */
    public boolean recibirDisparo(Coordenada coordenada, Jugador jugadorAtacante) {
        if (!validarDisparo(coordenada, jugadorAtacante)) {
            return false;
        }

        Jugador jugadorDefensor = (jugadorAtacante == this.jugador1) ? this.jugador2 : this.jugador1;
        
        Disparo disparoResultante = jugadorDefensor.recibirDisparo(coordenada);
        
        jugadorAtacante.marcarDisparo(disparoResultante);
        
        cambiarTurno(); 
        
        return true;
    }
}