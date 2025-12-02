package mx.itson.servidor;

import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Clase que representa una partida de Battleship entre dos jugadores
 * Gestiona el estado del juego, turnos, disparos y victoria
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class Partida {

    private static final int TAMANO_TABLERO = 10;
    private static final int TIEMPO_TURNO = 30; // segundos (cambiado de 60 a 30)

    private final String idPartida;
    private final JugadorDTO jugador1;
    private final JugadorDTO jugador2;

    // Tableros
    private TableroDTO tableroJugador1;
    private TableroDTO tableroJugador2;

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
     * Constructor de la partida
     *
     * @param jugador1
     * @param jugador2
     */
    public Partida(JugadorDTO jugador1, JugadorDTO jugador2) {
        this.idPartida = UUID.randomUUID().toString();
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;

        // Inicializar tableros
        this.tableroJugador1 = new TableroDTO(jugador1.getId(), jugador1.getNombre(), TAMANO_TABLERO);
        this.tableroJugador2 = new TableroDTO(jugador2.getId(), jugador2.getNombre(), TAMANO_TABLERO);

        this.navesColocadasJugador1 = false;
        this.navesColocadasJugador2 = false;
        this.estadoPartida = EstadoPartida.EN_CURSO;
        this.tiempoRestante = TIEMPO_TURNO;

        // Inicializar executor para el timer
        this.timerExecutor = Executors.newSingleThreadScheduledExecutor();

        System.out.println("[PARTIDA] Partida creada: " + jugador1.getNombre() + " vs " + jugador2.getNombre());
    }

    /**
     * Coloca las naves de un jugador en su tablero
     *
     * @param idJugador ID del jugador
     * @param naves Lista de naves a colocar
     * @return true si se colocaron correctamente, false si hay error
     */
    public synchronized boolean colocarNaves(String idJugador, List<NaveDTO> naves) {
        TableroDTO tablero;
        boolean esJugador1 = idJugador.equals(jugador1.getId());

        if (esJugador1) {
            tablero = tableroJugador1;
        } else if (idJugador.equals(jugador2.getId())) {
            tablero = tableroJugador2;
        } else {
            return false;
        }

        // Validar que las naves no se sobrepongan
        if (!validarPosicionamientoNaves(naves)) {
            return false;
        }

        // Colocar las naves en el tablero
        for (NaveDTO nave : naves) {
            for (CoordenadaDTO coord : nave.getCoordenadas()) {
                tablero.getCasillas()[coord.getX()][coord.getY()] = EstadoCasilla.OCUPADA;
            }
        }

        tablero.setNaves(naves);
        tablero.setTotalNaves(naves.size());

        // Marcar que este jugador ha colocado sus naves
        if (esJugador1) {
            navesColocadasJugador1 = true;
        } else {
            navesColocadasJugador2 = true;
        }

        System.out.println("[PARTIDA] Naves colocadas para " + tablero.getNombreJugador());

        // Si ambos jugadores han colocado naves, iniciar el juego
        if (navesColocadasJugador1 && navesColocadasJugador2) {
            iniciarJuego();
        }

        return true;
    }

    /**
     * Valida que las naves no se sobrepongan
     *
     * @param naves Lista de naves
     * @return true si son válidas
     */
    private boolean validarPosicionamientoNaves(List<NaveDTO> naves) {
        Set<String> coordenadasOcupadas = new HashSet<>();

        for (NaveDTO nave : naves) {
            for (CoordenadaDTO coord : nave.getCoordenadas()) {
                // Validar que esté dentro del tablero
                if (coord.getX() < 0 || coord.getX() >= TAMANO_TABLERO ||
                    coord.getY() < 0 || coord.getY() >= TAMANO_TABLERO) {
                    return false;
                }

                String key = coord.getX() + "," + coord.getY();
                if (coordenadasOcupadas.contains(key)) {
                    return false; // Se sobrep one con otra nave
                }
                coordenadasOcupadas.add(key);
            }
        }

        return true;
    }

    /**
     * Inicia el juego una vez que ambos jugadores han colocado sus naves
     */
    private void iniciarJuego() {
        // Asignar turno aleatorio
        Random random = new Random();
        boolean jugador1Inicia = random.nextBoolean();
        idJugadorEnTurno = jugador1Inicia ? jugador1.getId() : jugador2.getId();
        tiempoRestante = TIEMPO_TURNO;

        String nombreInicia = jugador1Inicia ? jugador1.getNombre() : jugador2.getNombre();
        System.out.println("[PARTIDA] Juego iniciado. Turno aleatorio asignado a: " + nombreInicia);
    }

    /**
     * Procesa un disparo
     *
     * @param idJugadorDispara ID del jugador que dispara
     * @param coordenada Coordenada del disparo
     * @return DTO con el resultado del disparo
     */
    public synchronized DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) {
        // Validar que sea el turno del jugador
        if (!idJugadorDispara.equals(idJugadorEnTurno)) {
            return null;
        }

        // Obtener el tablero del oponente
        TableroDTO tableroOponente;
        String nombreJugadorDispara;

        if (idJugadorDispara.equals(jugador1.getId())) {
            tableroOponente = tableroJugador2;
            nombreJugadorDispara = jugador1.getNombre();
        } else {
            tableroOponente = tableroJugador1;
            nombreJugadorDispara = jugador2.getNombre();
        }

        int x = coordenada.getX();
        int y = coordenada.getY();

        // Validar coordenadas
        if (x < 0 || x >= TAMANO_TABLERO || y < 0 || y >= TAMANO_TABLERO) {
            return null;
        }

        EstadoCasilla estadoActual = tableroOponente.getCasillas()[x][y];
        DisparoDTO disparo = new DisparoDTO(coordenada, nombreJugadorDispara);
        ResultadoDisparo resultado;
        String mensaje;

        // Procesar el disparo según el estado de la casilla
        switch (estadoActual) {
            case VACIA:
                // Agua
                tableroOponente.getCasillas()[x][y] = EstadoCasilla.IMPACTADA_VACIA;
                resultado = ResultadoDisparo.AGUA;
                mensaje = "¡Agua!";
                break;

            case OCUPADA:
                // Impacto en nave
                tableroOponente.getCasillas()[x][y] = EstadoCasilla.IMPACTADA_AVERIADA;

                // Buscar la nave impactada y verificar si se hundió
                NaveDTO naveImpactada = encontrarNaveEnCoordenada(tableroOponente, coordenada);
                if (naveImpactada != null) {
                    naveImpactada.setImpactosRecibidos(naveImpactada.getImpactosRecibidos() + 1);

                    if (naveImpactada.estaHundida()) {
                        // Nave hundida
                        naveImpactada.setEstado(EstadoNave.HUNDIDA);
                        resultado = ResultadoDisparo.IMPACTO_HUNDIDA;
                        mensaje = "¡" + naveImpactada.getTipo() + " hundido!";

                        // Marcar todas las casillas de la nave como hundida
                        for (CoordenadaDTO coord : naveImpactada.getCoordenadas()) {
                            tableroOponente.getCasillas()[coord.getX()][coord.getY()] =
                                EstadoCasilla.IMPACTADA_HUNDIDA;
                        }

                        // Incrementar contador de naves hundidas
                        tableroOponente.setNavesHundidas(tableroOponente.getNavesHundidas() + 1);

                        // Verificar si el jugador ganó
                        if (tableroOponente.todasNavesHundidas()) {
                            finalizarPartida(idJugadorDispara);
                        }
                    } else {
                        // Nave averiada
                        naveImpactada.setEstado(EstadoNave.AVERIADA);
                        resultado = ResultadoDisparo.IMPACTO_AVERIADA;
                        mensaje = "¡Impacto en " + naveImpactada.getTipo() + "!";
                    }
                } else {
                    resultado = ResultadoDisparo.IMPACTO_AVERIADA;
                    mensaje = "¡Impacto!";
                }
                break;

            default:
                // Ya se disparó aquí antes
                return null;
        }

        disparo.setResultado(resultado);
        disparo.setMensaje(mensaje);

        // Cambiar turno si es agua
        if (resultado == ResultadoDisparo.AGUA) {
            cambiarTurno();
        } else {
            // Reiniciar tiempo pero mantener turno
            tiempoRestante = TIEMPO_TURNO;
        }

        System.out.println("[PARTIDA] Disparo: " + coordenada.toStringCoord() + " - " + resultado);

        return disparo;
    }

    /**
     * Encuentra la nave que ocupa una coordenada específica
     *
     * @param tablero Tablero donde buscar
     * @param coordenada Coordenada a buscar
     * @return Nave encontrada o null
     */
    private NaveDTO encontrarNaveEnCoordenada(TableroDTO tablero, CoordenadaDTO coordenada) {
        for (NaveDTO nave : tablero.getNaves()) {
            for (CoordenadaDTO coord : nave.getCoordenadas()) {
                if (coord.equals(coordenada)) {
                    return nave;
                }
            }
        }
        return null;
    }

    /**
     * Cambia el turno al otro jugador
     */
    private void cambiarTurno() {
        if (idJugadorEnTurno.equals(jugador1.getId())) {
            idJugadorEnTurno = jugador2.getId();
        } else {
            idJugadorEnTurno = jugador1.getId();
        }
        tiempoRestante = TIEMPO_TURNO;
        System.out.println("[PARTIDA] Cambio de turno. Ahora juega: " + getNombreJugadorEnTurno());
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

    /**
     * Maneja el timeout de un turno
     */
    public synchronized void manejarTimeout() {
        System.out.println("[PARTIDA] Timeout para: " + getNombreJugadorEnTurno());
        cambiarTurno();
    }

    // Getters
    public String getIdPartida() {
        return idPartida;
    }

    public JugadorDTO getJugador1() {
        return jugador1;
    }

    public JugadorDTO getJugador2() {
        return jugador2;
    }

    public TableroDTO getTableroJugador1() {
        return tableroJugador1;
    }

    public TableroDTO getTableroJugador2() {
        return tableroJugador2;
    }

    public TableroDTO getTableroDeJugador(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return tableroJugador1;
        } else if (idJugador.equals(jugador2.getId())) {
            return tableroJugador2;
        }
        return null;
    }

    public TableroDTO getTableroOponente(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return tableroJugador2;
        } else if (idJugador.equals(jugador2.getId())) {
            return tableroJugador1;
        }
        return null;
    }

    public boolean ambosListos() {
        return navesColocadasJugador1 && navesColocadasJugador2;
    }

    public boolean ambosJugadoresListos() {
        return ambosListos();
    }

    public String getIdJugadorEnTurno() {
        return idJugadorEnTurno;
    }

    public String getNombreJugadorEnTurno() {
        if (idJugadorEnTurno.equals(jugador1.getId())) {
            return jugador1.getNombre();
        } else {
            return jugador2.getNombre();
        }
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }

    public EstadoPartida getEstadoPartida() {
        return estadoPartida;
    }

    public String getIdGanador() {
        return idGanador;
    }

    public String getNombreGanador() {
        if (idGanador == null) {
            return null;
        }
        return idGanador.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre();
    }

    public boolean hayGanador() {
        return idGanador != null;
    }

    public JugadorDTO getGanador() {
        if (idGanador == null) {
            return null;
        }
        return idGanador.equals(jugador1.getId()) ? jugador1 : jugador2;
    }
    
    public JugadorDTO getPerdedor() {
        if (idGanador == null) {
            return null;
        }
        return idGanador.equals(jugador1.getId()) ? jugador2 : jugador1;
    }

    public TableroDTO getTableroJugador(String idJugador) {
        return getTableroDeJugador(idJugador);
    }

    public boolean participaJugador(String idJugador) {
        return idJugador.equals(jugador1.getId()) || idJugador.equals(jugador2.getId());
    }

    public JugadorDTO getOponente(String idJugador) {
        if (idJugador.equals(jugador1.getId())) {
            return jugador2;
        } else if (idJugador.equals(jugador2.getId())) {
            return jugador1;
        }
        return null;
    }

    /**
     * Establece el callback para notificar cuando se agota el tiempo
     * @param callback Consumer que recibe el ID del jugador que perdió su turno
     */
    public void setCallbackTimeout(Consumer<String> callback) {
        this.callbackTimeout = callback;
    }

    /**
     * Inicia el timer del turno actual
     */
    public synchronized void iniciarTimer() {
        // Cancelar timer anterior si existe
        detenerTimer();

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
                    if (callbackTimeout != null) {
                        callbackTimeout.accept(idJugadorQuePerdiTurno);
                    }
                }
            }
        }, TIEMPO_TURNO, TimeUnit.SECONDS);
    }

    /**
     * Detiene el timer actual
     */
    public synchronized void detenerTimer() {
        if (tareaTimer != null && !tareaTimer.isDone()) {
            tareaTimer.cancel(false);
            System.out.println("[PARTIDA] Timer detenido");
        }
    }

    /**
     * Limpia recursos del timer cuando termina la partida
     * // ¿Por qué no tiene synchronized también?
     */
    public void limpiarRecursos() {
        detenerTimer();
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            timerExecutor.shutdown();
            System.out.println("[PARTIDA] Executor del timer cerrado");
        }
    }
}
