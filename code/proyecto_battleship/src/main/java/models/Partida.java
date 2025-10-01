package models;
import enums.EstadoCasilla;
import enums.EstadoPartida;
import enums.ResultadoDisparo;
import exceptions.CasillaException;
import exceptions.TableroException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
public class Partida implements ISubject{
    
    private ArrayList<IObserver> observadores;
    private Jugador jugadorTurno;
    private EstadoPartida estado;
    private final Jugador jugador1;
    private final Jugador jugador2;
    private ScheduledExecutorService temporizador;
    private ScheduledFuture<?> tarea;
    private int tiempoRestante;
    
    /**
     * Constructor para iniciar una nueva partida
     * @param jugador1 El primer jugador
     * @param jugador2 El segundo jugador
     */
    public Partida(Jugador jugador1, Jugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.estado = EstadoPartida.EN_CURSO;
        this.tiempoRestante = 0;
        this.observadores = new ArrayList<>();
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public void setEstado(EstadoPartida estado) {
        this.estado = estado;
    }
    
    public void iniciarPartida() {
        this.jugadorTurno = this.jugador1;
        iniciarTemporizador();
    }

    public void cambiarTurno() {
        if(jugador1.equals(jugadorTurno))
            jugadorTurno = jugador2;
        else
            jugadorTurno = jugador1;
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
    public boolean verificarJugadorTurno(Jugador jugador) {return this.jugadorTurno == jugador;}
    
    private void iniciarTemporizador(){
        tiempoRestante = 30;
        tarea = temporizador.scheduleAtFixedRate(() -> tarea(), 0, 1, TimeUnit.SECONDS);
    }
    
    /**
     * Pausa el flujo del temporizador
     */
    public void pausarTemporizador() {
        if(!tarea.isCancelled())
            tarea.cancel(true);
    }

    /**
     * Reanuda el flujo del temporizador si estaba pausado
     */
    public void reanudarTemporizador() {
        if(tarea.isCancelled())
            tarea = temporizador.scheduleAtFixedRate(() -> tarea(), 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Reinicia el contador de tiempo al valor inicial y lo activa
     * Se usaría al inicio de cada turno
     */
    public void reiniciarTemporizador() {
        if(!tarea.isCancelled())
            tarea.cancel(true);
        temporizador.schedule(() -> iniciarTemporizador(), 1, TimeUnit.SECONDS);
    }
    
    private void tarea() {
        
        tiempoRestante--;

        if (tiempoRestante <= 0) {
            notificarObservadores("Turno perdido");
            
            cambiarTurno();
            // Cancela la tarea programada si el tiempo se agota
            tarea.cancel(true);
            // Iniciar de nuevo el temporizador después de un delay de 5 segundos
            temporizador.schedule(() -> iniciarTemporizador(), 5, TimeUnit.SECONDS);
        }
    }
    
    /**
     * Valida si un disparo es legal (turno correcto y casilla no repetida)
     * @param coordenada La coordenada del disparo
     * @param jugador El jugador que intenta disparar
     * @return true si el disparo es válido
     * @throws exceptions.TableroException
     */
    public boolean validarDisparo(Coordenada coordenada, Jugador jugador) throws TableroException {return jugador.validarDisparo(coordenada);}

    /**
     * Procesa un disparo de un jugador a otro
     * @param coordenada La coordenada del disparo
     * @param jugadorAtacante El jugador que realiza el disparo
     * @return true si el disparo fue procesado, false si no fue válido
     * @throws exceptions.TableroException
     * @throws exceptions.CasillaException
     */
    public boolean recibirDisparo(Coordenada coordenada, Jugador jugadorAtacante) throws TableroException, CasillaException {

        Jugador jugadorDefensor = (jugadorAtacante == this.jugador1) ? this.jugador2 : this.jugador1;
        
        EstadoCasilla estadoCasilla = jugadorDefensor.recibirDisparo(coordenada);
        
        // Resultado del disparo a añadir a una instancia de Disparo
        ResultadoDisparo resultadoDisparo;
        
        // Si el estado de la casilla impactada, es averiada o hundida, se actualiza el resultado del Diparo como IMPACTO
        if (estadoCasilla == EstadoCasilla.IMPACTADA_AVERIADA || estadoCasilla == EstadoCasilla.IMPACTADA_HUNDIDA)
            resultadoDisparo = ResultadoDisparo.IMPACTO;
        // De lo contrario, se actualiza el resultado como AGUA.
        else 
            resultadoDisparo = ResultadoDisparo.AGUA;
        
        // Se crea una nueva instancia de un Disparo, con su resultado anterior.
        Disparo disparoResultante = new Disparo(coordenada, resultadoDisparo, jugadorAtacante);
        
        jugadorAtacante.marcarDisparo(disparoResultante);
        
        return true;
    }

    @Override
    public void agregarObserver(IObserver observador) {this.observadores.add(observador);}

    @Override
    public void quitarObserver(IObserver observador) {this.observadores.remove(observador);}

    @Override
    public void notificarObservadores(String mensaje) {
        observadores.stream().forEach(o -> o.notificar(mensaje, this.jugadorTurno.getNombre()));
    }
}