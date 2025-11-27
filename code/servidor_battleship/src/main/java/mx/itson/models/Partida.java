package mx.itson.models;

import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import mx.itson.exceptions.ModelException;
import mx.itson.mappers.CoordenadaMapper;
import mx.itson.utils.dtos.CoordenadaDTO;
import mx.itson.utils.dtos.DisparoDTO;
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
    private final ScheduledExecutorService timerExecutor;
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
        this.tiempoRestante = TIEMPO_TURNO;
        
        // Inicializar executor para el timer
        this.timerExecutor = Executors.newSingleThreadScheduledExecutor();
        
        System.out.println("[PARTIDA] Partida creada: " + jugador1.getNombre() + " vs " + jugador2.getNombre());
    }

    @Override
    public String getIdPartida() {return idPartida;}
    
    @Override
    public IJugador getJugador1() {return jugador1;}

    @Override
    public IJugador getJugador2() {return jugador2;}

    public EstadoPartida getEstado() {return estado;}

    public void setEstadoPartida(EstadoPartida estado) {this.estado = estado;}

    public void setTiempoRestante(int tiempoRestante) {this.tiempoRestante = tiempoRestante;}
    
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
    
    /**
     * Inicia el juego una vez que ambos jugadores han colocado sus naves
     */
    private void iniciarJuego() {
        // Asignar turno aleatorio
        SecureRandom random = new SecureRandom();
        boolean jugador1Inicia = random.nextBoolean();
        idJugadorEnTurno = jugador1Inicia ? jugador1.getId() : jugador2.getId();
        tiempoRestante = TIEMPO_TURNO;

        String nombreInicia = jugador1Inicia ? jugador1.getNombre() : jugador2.getNombre();
        System.out.println("[PARTIDA] Juego iniciado. Turno aleatorio asignado a: " + nombreInicia);
    }
    
     /**
     * Finaliza la partida declarando un ganador
     *
     * @param idGanador ID del jugador ganador
     */
    private void finalizarPartida(String idGanador) {
        this.idGanador = idGanador;
        this.estadoPartida = EstadoPartida.FINALIZADA;
        System.out.println("[PARTIDA] Partida finalizada. Ganador: " + getNombreGanador());
    }
    
    private IJugador obtenerJugador(String idJugador){return (jugador1.getId().equals(idJugador)) ? jugador1 : jugador2;}
    
    private IJugador obtenerJugadorOponente(String idJugador){return (jugador1.getId().equals(idJugador)) ? jugador2 : jugador1;}
    
    @Override
    public String getIdJugadorEnTurno() {return idJugadorEnTurno;}
    
    public String getNombreJugadorEnTurno() {
        return (idJugadorEnTurno.equals(jugador1.getId())) ? 
                jugador1.getNombre() : 
                jugador2.getNombre();
    }
    
    @Override
    public String getIdGanador() {return idGanador;}
    
    public String getNombreGanador() {
        if (idGanador == null)
            return null;
        return idGanador.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre();
    }
    
    public boolean hayGanador() {return idGanador != null;}
    
    @Override
    public boolean ambosJugadoresListos() {return navesColocadasJugador1 && navesColocadasJugador2;}

    @Override
    public int getTiempoRestante() {return tiempoRestante;}
    
    // EN PROCESO DE ORGANIZACIÓN, POR LO QUE PUEDE CONTENER ERRORES
    @Override
    public synchronized DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) throws ModelException {
        // Validar que sea el turno del jugador (puede que este se pase al subsistema)
        if (!idJugadorDispara.equals(idJugadorEnTurno))
            return null;
        
        IJugador jugador = obtenerJugadorOponente(idJugadorDispara);
        
        int x = coordenada.getX();
        int y = coordenada.getY();

        // Validar coordenadas
        if (x < 0 || x >= TAMANO_TABLERO || y < 0 || y >= TAMANO_TABLERO)
            return null;
        
        DisparoDTO disparo = new DisparoDTO(coordenada, jugador.getNombre());
        ResultadoDisparo resultadoDisparo;
        EstadoCasilla estadoCasilla = jugador.recibirDisparo(CoordenadaMapper.toEntity(coordenada));
        String mensaje;
        
        switch(estadoCasilla){
            case IMPACTADA_AVERIADA -> {
                resultadoDisparo = ResultadoDisparo.IMPACTO_AVERIADA;
                mensaje = "¡Nave averiada!"; // Después puede que implemente un método para devolver una nave como en la otra clase
            } case IMPACTADA_HUNDIDA -> {
                resultadoDisparo = ResultadoDisparo.IMPACTO_HUNDIDA;
                mensaje = "¡Nave hundida!"; // Después puede que implemente un método para devolver una nave como en la otra clase
            } default -> {
                resultadoDisparo = ResultadoDisparo.AGUA;
                mensaje = "¡Agua!";
            }
        }
        disparo.setResultado(resultadoDisparo);
        disparo.setMensaje(mensaje);
        
        // Cambiar turno si es agua
        if (resultadoDisparo == ResultadoDisparo.AGUA) 
            cambiarTurno();
        else 
            // Reiniciar tiempo pero mantener turno
            tiempoRestante = TIEMPO_TURNO;
        
        System.out.println("[PARTIDA] Disparo: " + coordenada.toStringCoord() + " - " + resultadoDisparo);
        
        return disparo;
    }

    @Override
    public synchronized boolean colocarNaves(String idJugador, List<Nave> naves) throws ModelException {
        IJugador jugadorNaves;
        boolean esJugador1 = idJugador.equals(jugador1.getId());
        
        if (esJugador1)
            jugadorNaves = jugador1;
        else if (idJugador.equals(jugador2.getId()))
            jugadorNaves = jugador2;
        else 
            return false;
        
        if(jugadorNaves.colocarNaves(naves)){
            if(esJugador1)
                navesColocadasJugador1 = true;
            else
                navesColocadasJugador2 = true;
            System.out.println("[PARTIDA] Naves colocadas para " + jugadorNaves.getNombre());
            
            // Si ambos jugadores han colocado naves, iniciar el juego
        if (navesColocadasJugador1 && navesColocadasJugador2)
            iniciarJuego();
        
        return true;
        
        } else
            return false;
    }

    @Override
    public synchronized boolean agregarNave(Nave naveNueva, String idJugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public synchronized boolean quitarNave(Coordenada[] coordenadas, String idJugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void cambiarTurno(){
        if (idJugadorEnTurno.equals(jugador1.getId())) {
            idJugadorEnTurno = jugador2.getId();
        } else {
            idJugadorEnTurno = jugador1.getId();
        }
        tiempoRestante = TIEMPO_TURNO;
        System.out.println("[PARTIDA] Cambio de turno. Ahora juega: " + getNombreJugadorEnTurno());
    }
    
    @Override
    public synchronized void manejarTiempoAgotado() {
        System.out.println("[PARTIDA] Timeout para: " + getNombreJugadorEnTurno());
        cambiarTurno();
    }

    @Override
    public void establecerRespuestaTiempoAgotado(Consumer<String> callback) {this.callbackTimeout = callback;}

    @Override
    public synchronized void iniciarTemporizador() {
        // Cancelar timer anterior si existe
        detenerTemporizador();

        tiempoRestante = TIEMPO_TURNO;

        System.out.println("[PARTIDA] Timer iniciado para el turno de: " +
            (idJugadorEnTurno.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre()));

        // Crear nueva tarea que se ejecuta después de TIEMPO_TURNO segundos
        tareaTimer = timerExecutor.schedule(() -> {
            synchronized (this) {
                if (estadoPartida == EstadoPartida.EN_CURSO && !hayGanador()) {
                    System.out.println("[PARTIDA] ¡TIMEOUT! Jugador " +
                        (idJugadorEnTurno.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre()) +
                        " se quedó sin tiempo");

                    String idJugadorQuePerdiTurno = idJugadorEnTurno;

                    // Cambiar turno automáticamente
                    cambiarTurno();

                    // Notificar al manejador si hay callback
                    if (callbackTimeout != null) 
                        callbackTimeout.accept(idJugadorQuePerdiTurno);
                }
            }
        }, TIEMPO_TURNO, TimeUnit.SECONDS);
    }

    @Override
    public synchronized void detenerTemporizador() {
        if (tareaTimer != null && !tareaTimer.isDone()) {
            tareaTimer.cancel(false);
            System.out.println("[PARTIDA] Timer detenido");
        }
    }

    @Override
    public void limpiarRecursos() {
        detenerTemporizador();
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            timerExecutor.shutdown();
            System.out.println("[PARTIDA] Executor del timer cerrado");
        }
    }

}