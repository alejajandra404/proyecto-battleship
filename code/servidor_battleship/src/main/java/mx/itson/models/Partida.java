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
 * Partida.java
 *
 * Clase que representa una partida de batalla naval entre dos jugadores.
 * Gestiona el estado de la partida, los turnos, el temporizador y el procesamiento
 * de disparos. Utiliza un ScheduledExecutorService para controlar el tiempo de cada turno.
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

    // Ganador
    private String idGanador;

    // Timer para controlar el tiempo del turno
    private final ScheduledExecutorService timerExecutor;
    private ScheduledFuture<?> tareaTimer;
    private Consumer<String> callbackTimeout; // Callback para notificar timeout
    private Consumer<Integer> callbackActualizacionTiempo; // Callback para actualizaciones periódicas del tiempo

    /**
     * Constructor para iniciar una nueva partida.
     * Inicializa el executor para el temporizador y establece el estado inicial.
     *
     * @param idPartida ID único de la partida
     * @param jugador1 Primer jugador
     * @param jugador2 Segundo jugador
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

    /**
     * Obtiene el ID único de la partida.
     *
     * @return ID de la partida
     */
    @Override
    public String getIdPartida() {return idPartida;}

    /**
     * Obtiene el primer jugador de la partida.
     *
     * @return Primer jugador
     */
    @Override
    public IJugador getJugador1() {return jugador1;}

    /**
     * Obtiene el segundo jugador de la partida.
     *
     * @return Segundo jugador
     */
    @Override
    public IJugador getJugador2() {return jugador2;}

    /**
     * Obtiene el ID del jugador que tiene el turno actual.
     *
     * @return ID del jugador en turno
     */
    @Override
    public String getIdJugadorEnTurno() {return idJugadorEnTurno;}

    /**
     * Obtiene el ID del jugador ganador.
     *
     * @return ID del ganador, o null si no hay ganador
     */
    @Override
    public String getIdGanador() {return idGanador;}

    /**
     * Verifica si la partida tiene un ganador.
     *
     * @return true si hay un ganador
     */
    @Override
    public boolean hayGanador() {return idGanador != null;}

    /**
     * Verifica si ambos jugadores han colocado sus naves.
     *
     * @return true si ambos jugadores están listos
     */
    @Override
    public boolean ambosJugadoresListos() {return navesColocadasJugador1 && navesColocadasJugador2;}

    /**
     * Obtiene el estado actual de la partida.
     *
     * @return Estado de la partida
     */
    @Override
    public EstadoPartida getEstadoPartida() {return estado;}

    /**
     * Obtiene el tiempo restante del turno actual en segundos.
     *
     * @return Tiempo restante en segundos
     */
    @Override
    public int getTiempoRestante() {return tiempoRestante;}
    
    /**
     * Inicia el juego una vez que ambos jugadores han colocado sus naves.
     * Asigna el turno inicial de forma aleatoria.
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
     * Finaliza la partida declarando un ganador.
     * Cambia el estado de la partida a FINALIZADA.
     *
     * @param idGanador ID del jugador ganador
     */
    private void finalizarPartida(String idGanador) {
        this.idGanador = idGanador;
        this.estado = EstadoPartida.FINALIZADA;
        System.out.println("[PARTIDA] Partida finalizada. Ganador: " + getNombreGanador());
    }

    /**
     * Obtiene un jugador por su ID.
     *
     * @param idJugador ID del jugador a buscar
     * @return El jugador correspondiente al ID
     */
    @Override
    public IJugador getJugador(String idJugador){return (jugador1.getId().equals(idJugador)) ? jugador1 : jugador2;}

    /**
     * Obtiene el oponente de un jugador.
     *
     * @param idJugador ID del jugador
     * @return El jugador oponente
     */
    private IJugador getJugadorOponente(String idJugador){return (jugador1.getId().equals(idJugador)) ? jugador2 : jugador1;}

    /**
     * Obtiene el nombre del jugador que tiene el turno actual.
     *
     * @return Nombre del jugador en turno
     */
    public String getNombreJugadorEnTurno() {
        return (idJugadorEnTurno.equals(jugador1.getId())) ?
                jugador1.getNombre() :
                jugador2.getNombre();
    }

    /**
     * Obtiene el nombre del jugador ganador.
     *
     * @return Nombre del ganador, o null si no hay ganador
     */
    public String getNombreGanador() {
        if (idGanador == null) return null;
        return idGanador.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre();
    }

    /**
     * Verifica si un jugador tiene el turno actual.
     *
     * @param idJugador ID del jugador a verificar
     * @return true si es el turno del jugador
     */
    @Override
    public boolean verificarJugadorTurno(String idJugador) {return getIdJugadorEnTurno().equals(idJugador);}

    /**
     * Valida si un disparo en una coordenada es válido para un jugador.
     *
     * @param idJugador ID del jugador que intenta disparar
     * @param coordenada Coordenada del disparo
     * @return true si el disparo es válido
     * @throws ModelException Si ocurre un error durante la validación
     */
    @Override
    public boolean validarDisparo(String idJugador, CoordenadaDTO coordenada) throws ModelException {
        IJugador jugador = getJugador(idJugador);
        return jugador.validarDisparo(CoordenadaMapper.toEntity(coordenada));
    }

    /**
     * Procesa un disparo de un jugador hacia el oponente.
     * Valida el disparo, actualiza el estado de las naves, registra el disparo
     * en el historial y cambia el turno si es necesario.
     *
     * @param idJugadorDispara ID del jugador que dispara
     * @param coordenada Coordenada del disparo
     * @return DTO del disparo con el resultado (AGUA, IMPACTO_AVERIADA, IMPACTO_HUNDIDA)
     * @throws ModelException Si ocurre un error al procesar el disparo
     */
    @Override
    public synchronized DisparoDTO procesarDisparo(String idJugadorDispara, CoordenadaDTO coordenada) throws ModelException {
        
        /*
            El subsistema valida el turno del jugador, aunque podría incluirse 
            para una mayor integrida en caso de reutilización de la clase
        */
        IJugador jugador = getJugadorOponente(idJugadorDispara);
                
        // Coordenadas
        int x = coordenada.getX();
        int y = coordenada.getY();

        // Validar coordenadas
        if (x < 0 || x >= TAMANO_TABLERO || y < 0 || y >= TAMANO_TABLERO) return null;
        
        // DTO con la información del disparo
        DisparoDTO disparo = new DisparoDTO(coordenada, jugador.getNombre());
        
        // Mapea la coordenada
        Coordenada coordenadaImpacto = CoordenadaMapper.toEntity(coordenada);
        
        // Ejecuta el disparo en el tablero del oponente
        EstadoCasilla estadoCasilla = jugador.recibirDisparo(coordenadaImpacto);
        
        // Mensaje personalizado con información del resultado
        String mensaje;
        
        // Guarda el resultado del disparo
        ResultadoDisparo resultadoDisparo;
        
        switch(estadoCasilla){
            case IMPACTADA_AVERIADA -> {
                // Se actualiza el estado del resultado del disparo
                resultadoDisparo = ResultadoDisparo.IMPACTO_AVERIADA;
                
                // Se obtiene la nave impactada
                Nave naveImpactada = jugador.getTableroNaves().encontrarNaveEnCoordenada(coordenadaImpacto);
                mensaje = "¡Impacto en " + naveImpactada.getTipo() + "!"; // Después puede que implemente un método para devolver una nave como en la otra clase
                
            } case IMPACTADA_HUNDIDA -> {
                // Se actualiza el estado del resultado del disparo
                resultadoDisparo = ResultadoDisparo.IMPACTO_HUNDIDA;
                
                // Se obtiene el tablero de naves del jugador oponente
                ITableroNaves tableroNaves = jugador.getTableroNaves();
                
                // Se obtiene la nave hundida
                Nave naveHundida = tableroNaves.encontrarNaveEnCoordenada(coordenadaImpacto);
                mensaje = "¡" + naveHundida.getTipo() + " hundido!"; // Después puede que implemente un método para devolver una nave como en la otra clase
                
                // Si todas las naves fueron hundidas, se finaliza la partida
                if(tableroNaves.todasNavesHundidas())
                    finalizarPartida(idJugadorDispara);
                
            } default -> {
                // Se actualiza el estado del resultado del disparo
                resultadoDisparo = ResultadoDisparo.AGUA;
                // Se cambia el turno del jugador
                cambiarTurno();
                
                mensaje = "¡Agua!";
            }
        }
        // Se agregan al disparo tanto su resultado como el mensaje personalizado
        disparo.setResultado(resultadoDisparo);
        disparo.setMensaje(mensaje);

        // Registrar el disparo en el historial del jugador que disparó
        IJugador jugadorQueDispara = getJugador(idJugadorDispara);
        Disparo disparoParaRegistrar = new Disparo(coordenadaImpacto, resultadoDisparo, jugadorQueDispara);
        jugadorQueDispara.getTableroDisparos().añadirDisparo(disparoParaRegistrar);

//        // Cambiar turno si es agua
//        if (resultadoDisparo == ResultadoDisparo.AGUA)
//            cambiarTurno();
//        else
//            // Reiniciar tiempo pero mantener turno
//            tiempoRestante = TIEMPO_TURNO;

        System.out.println("[PARTIDA] Disparo: " + coordenada.toStringCoord() + " - " + resultadoDisparo);

        // Finalmente se regresa el resultado
        return disparo;
    }

    /**
     * Coloca las naves de un jugador en su tablero.
     * Si ambos jugadores han colocado sus naves, inicia el juego automáticamente.
     *
     * @param idJugador ID del jugador que coloca las naves
     * @param naves Lista de naves a colocar
     * @return true si se colocaron exitosamente
     * @throws ModelException Si ocurre un error al colocar las naves
     */
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
            // Falta validar si ya están todas las naves colocadas...
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

    /**
     * Cambia el turno al siguiente jugador.
     * Reinicia el tiempo restante del turno.
     */
    private void cambiarTurno(){
        
        idJugadorEnTurno = (idJugadorEnTurno.equals(jugador1.getId())) ? 
                jugador2.getId() : 
                jugador1.getId();
        
        tiempoRestante = TIEMPO_TURNO;
        
        System.out.println("[PARTIDA] Cambio de turno. Ahora juega: " + getNombreJugadorEnTurno());
    }

    /**
     * Maneja el evento cuando se agota el tiempo de un turno.
     * Cambia el turno automáticamente al siguiente jugador.
     */
    @Override
    public synchronized void manejarTiempoAgotado() {
        System.out.println("[PARTIDA] Timeout para: " + getNombreJugadorEnTurno());
        cambiarTurno();
    }

    /**
     * Establece el callback que se ejecutará cuando se agote el tiempo de un turno.
     *
     * @param callback Consumer que recibe el ID del jugador que perdió su turno
     */
    @Override
    public void establecerRespuestaTiempoAgotado(Consumer<String> callback) {this.callbackTimeout = callback;}

    /**
     * Establece el callback para actualizaciones periódicas del tiempo restante.
     *
     * @param callback Consumer que recibe el tiempo restante en segundos
     */
    @Override
    public void establecerCallbackActualizacionTiempo(Consumer<Integer> callback) {
        this.callbackActualizacionTiempo = callback;
    }

    /**
     * Inicia el temporizador del turno actual.
     * Crea una tarea que se ejecuta cada segundo para decrementar el tiempo
     * y notificar cuando se agote.
     */
    @Override
    public synchronized void iniciarTemporizador() {
        // Cancelar timer anterior si existe
        detenerTemporizador();

        tiempoRestante = TIEMPO_TURNO;

        System.out.println("[PARTIDA] Timer iniciado para el turno de: " +
            (idJugadorEnTurno.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre()));

        // Crear tarea que se ejecuta cada segundo
        tareaTimer = timerExecutor.scheduleAtFixedRate(() -> {
            try {
                synchronized (this) {
                    if (estado == EstadoPartida.EN_CURSO && !hayGanador()) {
                        // Decrementar tiempo restante
                        tiempoRestante--;

                        // Enviar actualización periódica al cliente (cada 5 segundos)
                        if (tiempoRestante % 5 == 0 && callbackActualizacionTiempo != null) {
                            callbackActualizacionTiempo.accept(tiempoRestante);
                        }

                        // Verificar timeout
                        if (tiempoRestante <= 0) {
                            System.out.println("[PARTIDA] Timeout para jugador " +
                                (idJugadorEnTurno.equals(jugador1.getId()) ? jugador1.getNombre() : jugador2.getNombre()));

                            String idJugadorQuePerdiTurno = idJugadorEnTurno;

                            // Cambiar turno automáticamente
                            cambiarTurno();

                            // NO detener el timer aquí, dejar que la tarea termine y sea cancelada por iniciarTemporizador()
                            // El ManejadorCliente llamará iniciarTemporizador() que cancela y reinicia correctamente

                            // Notificar al manejador si hay callback
                            if (callbackTimeout != null) {
                                callbackTimeout.accept(idJugadorQuePerdiTurno);
                            }

                            // Detener esta ejecución específica del timer
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("[PARTIDA] Error en timer: " + e.getMessage());
                e.printStackTrace();
            }
        }, 1, 1, TimeUnit.SECONDS); // Ejecutar cada 1 segundo, empezando después de 1 segundo
    }

    /**
     * Detiene el temporizador del turno actual.
     * Cancela la tarea programada si está en ejecución.
     */
    @Override
    public synchronized void detenerTemporizador() {
        if (tareaTimer != null && !tareaTimer.isDone()) {
            tareaTimer.cancel(false);
            System.out.println("[PARTIDA] Timer detenido");
        }
    }

    /**
     * Libera los recursos asociados a la partida.
     * Detiene el temporizador y cierra el executor.
     */
    @Override
    public void liberarRecursos() {
        detenerTemporizador();
        if (timerExecutor != null && !timerExecutor.isShutdown()) {
            timerExecutor.shutdown();
            System.out.println("[PARTIDA] Executor del timer cerrado");
        }
    }

    /**
     * Obtiene el jugador del modelo por su ID.
     *
     * @param idBusqueda ID del jugador a buscar
     * @return El jugador correspondiente, o null si no se encuentra
     */
    public IJugador getJugadorModeloPorId(String idBusqueda) {
        if (this.jugador1.getId().equals(idBusqueda)) {
            return this.jugador1;
        }
        if (this.jugador2.getId().equals(idBusqueda)) {
            return this.jugador2;
        }
        return null;
    }
    
}