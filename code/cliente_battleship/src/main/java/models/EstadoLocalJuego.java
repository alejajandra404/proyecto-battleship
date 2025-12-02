package models;

import mx.itson.utils.dtos.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado local del juego en el cliente.
 * Implementa el patrón Observer para notificar cambios a las vistas.
 *
 * Responsabilidades:
 * - Mantener el estado temporal del juego usando DTOs
 * - Notificar a las vistas cuando hay cambios (patrón Observer)
 * - Proveer acceso controlado al estado del juego
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class EstadoLocalJuego implements ISubject {

    // Estado del juego (DTOs)
    private JugadorDTO jugadorLocal;
    private JugadorDTO oponente;
    private TableroDTO miTablero;
    private TableroDTO tableroOponente;
    private boolean miTurno;
    private int tiempoRestante;
    private String idPartida;

    // Patrón Observer
    private final List<IObserver> observadores;

    /**
     * Constructor
     * @param jugadorLocal El jugador local
     * @param oponente El oponente
     */
    public EstadoLocalJuego(JugadorDTO jugadorLocal, JugadorDTO oponente) {
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.observadores = new ArrayList<>();
        this.miTurno = false;
        this.tiempoRestante = 30;
    }

    /**
     * Constructor con ID de partida
     * @param jugadorLocal El jugador local
     * @param oponente El oponente
     * @param idPartida ID de la partida
     */
    public EstadoLocalJuego(JugadorDTO jugadorLocal, JugadorDTO oponente, String idPartida) {
        this(jugadorLocal, oponente);
        this.idPartida = idPartida;
    }

    // ========== MÉTODOS DE ACTUALIZACIÓN ==========

    /**
     * Actualiza los tableros del juego
     * @param miTablero Tablero del jugador local
     * @param tableroOponente Tablero del oponente
     */
    public void actualizarTableros(TableroDTO miTablero, TableroDTO tableroOponente) {
        this.miTablero = miTablero;
        this.tableroOponente = tableroOponente;
        notificarObservadores("TABLEROS_ACTUALIZADOS");
    }

    /**
     * Cambia el turno del juego
     * @param esMiTurno true si es el turno del jugador local
     * @param jugadorEnTurno El jugador que tiene el turno
     */
    public void cambiarTurno(boolean esMiTurno, JugadorDTO jugadorEnTurno) {
        this.miTurno = esMiTurno;
        
        String nombreJugador;
        if (jugadorEnTurno != null) {
            nombreJugador = jugadorEnTurno.getNombre();
        } else {
            nombreJugador = "";
        }
        
        notificarObservadores("TURNO_CAMBIADO", nombreJugador);
    }

    /**
     * Actualiza el tiempo restante del turno
     * @param segundos Segundos restantes
     */
    public void actualizarTiempo(int segundos) {
        this.tiempoRestante = segundos;
        notificarObservadores("TIEMPO_ACTUALIZADO", String.valueOf(segundos));
    }

    /**
     * Registra el resultado de un disparo
     * @param disparo El disparo realizado
     */
    public void registrarDisparo(DisparoDTO disparo) {
        notificarObservadores("DISPARO_REALIZADO", disparo.getMensaje());
    }

    /**
     * Finaliza la partida
     * @param gane true si el jugador local ganó
     * @param ganador El jugador ganador
     */
    public void finalizarPartida(boolean gane, JugadorDTO ganador) {
        String resultado;

        if (gane) {
            resultado = "VICTORIA";
        } else {
            resultado = "DERROTA";
        }
        
        notificarObservadores("PARTIDA_FINALIZADA", resultado);
    }

    // ========== GETTERS ==========

    public JugadorDTO getJugadorLocal() {
        return jugadorLocal;
    }

    public JugadorDTO getOponente() {
        return oponente;
    }

    public TableroDTO getMiTablero() {
        return miTablero;
    }

    public TableroDTO getTableroOponente() {
        return tableroOponente;
    }

    public boolean isMiTurno() {
        return miTurno;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public void setIdPartida(String idPartida) {
        this.idPartida = idPartida;
    }

    // ========== IMPLEMENTACIÓN DE ISubject (Patrón Observer) ==========

    @Override
    public void agregarObserver(IObserver observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void quitarObserver(IObserver observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(String mensaje) {
        notificarObservadores(mensaje, "");
    }

    /**
     * Notifica a todos los observadores con un mensaje y datos adicionales
     * @param evento El tipo de evento
     * @param datos Datos adicionales del evento
     */
    public void notificarObservadores(String evento, String datos) {
        for (IObserver observador : observadores) {
            observador.notificar(evento, datos);
        }
    }
}
