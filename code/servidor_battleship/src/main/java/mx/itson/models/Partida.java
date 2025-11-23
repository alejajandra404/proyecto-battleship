package mx.itson.models;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import mx.itson.exceptions.ModelException;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
import mx.itson.utils.dtos.NaveDTO;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.EstadoPartida;
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
public class Partida implements IPartida {
    
    private static final int TAMANO_TABLERO = 10;
    private static final int TIEMPO_TURNO = 30; // segundos (cambiado de 60 a 30)
    
    private final String idPartida;
    private IJugador jugadorTurno;
    private EstadoPartida estado;
    private final IJugador jugador1;
    private final IJugador jugador2;
    
    // Estado de naves colocadas
    private boolean navesColocadasJugador1;
    private boolean navesColocadasJugador2;
    
    // Control de turno
    private String idJugadorEnTurno;
    private int tiempoRestante;
    private EstadoPartida estadoPartida;
    
    // Ganador
    private String idGanador;

    // Timer para controlar el tiempo del turno
    private ScheduledExecutorService timerExecutor;
    private ScheduledFuture<?> tareaTimer;
    private Consumer<String> callbackTimeout; // Callback para notificar timeout
    
    /**
     * Constructor para iniciar una nueva partida
     * @param idPartida ID de la partida
     * @param jugador1 El primer jugador
     * @param jugador2 El segundo jugador
     */
    public Partida(String idPartida, IJugador jugador1, IJugador jugador2) {
        this.idPartida = idPartida;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.estado = EstadoPartida.EN_CURSO;
    }

    public String getIdPartida() {return idPartida;}
    
    public IJugador getJugador1() {return jugador1;}

    public IJugador getJugador2() {return jugador2;}

    public EstadoPartida getEstado() {return estado;}

    public void setEstado(EstadoPartida estado) {this.estado = estado;}

    @Override
    public String obtenerIdPartida() {return getIdPartida();}
    
//    @Override
//    public void iniciarPartida() throws ModelException {
//        this.jugadorTurno = this.jugador1;
//    }
//
//    @Override
//    public void cambiarTurno() throws ModelException {
//        if(jugador1.equals(jugadorTurno))
//            jugadorTurno = jugador2;
//        else
//            jugadorTurno = jugador1;
//    }
//
//    @Override
//    public boolean validarDisparo(Disparo disparo, IJugador jugador) throws ModelException {
//        return jugador.validarDisparo(disparo);
//    }
//
//    @Override
//    public boolean recibirDisparo(Coordenada coordenada, IJugador jugadorAtacante) throws ModelException {
//        
//        IJugador jugadorDefensor = (jugadorAtacante == this.jugador1) ? this.jugador2 : this.jugador1;
//        
//        EstadoCasilla estadoCasilla = jugadorDefensor.recibirDisparo(coordenada);
//        
//        // Determinar el resultado del disparo
//        ResultadoDisparo resultadoDisparo;
//        boolean esImpacto;
//        
//        // Si el estado de la casilla impactada, es averiada o hundida, se actualiza el resultado del Diparo como IMPACTO
//        switch (estadoCasilla) {
//            case IMPACTADA_AVERIADA -> {
//                resultadoDisparo = ResultadoDisparo.IMPACTO_AVERIADA;
//                esImpacto = true;
//            }
//            case IMPACTADA_HUNDIDA -> {
//                resultadoDisparo = ResultadoDisparo.IMPACTO_HUNDIDA;
//                esImpacto = true;
//                // De lo contrario, se actualiza el resultado como AGUA.
//            }
//            default -> {
//                resultadoDisparo = ResultadoDisparo.AGUA;
//                esImpacto = false;
//            }
//        }
//        
//        // Se crea una nueva instancia de un Disparo, con su resultado anterior.
//        Disparo disparoResultante = new Disparo(coordenada, resultadoDisparo, jugadorAtacante);
//        
//        // Marcar el disparo en el tablero del atacante
//        jugadorAtacante.marcarDisparo(disparoResultante);
//        
//        // Retornar si fue impacto o agua
//        return esImpacto;
//    }
//
//    @Override
//    public boolean verificarJugadorTurno(IJugador jugador) throws ModelException {
//        return this.jugadorTurno == jugador;
//    }

    @Override
    public boolean ambosJugadoresListos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int obtenerTiempoRestante() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String obtenerIdJugadorEnTurno() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String obtenerIdGanador() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean colocarNaves(String idJugador, List<NaveDTO> naves) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean agregarNave(Nave naveNueva, String idJugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean quitarNave(Coordenada[] coordenadas, String idJugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void establecerCallbackTimeout(Consumer<String> callback) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void iniciarTemporizador() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void detenerTemporizador() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void limpiarRecursos() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}