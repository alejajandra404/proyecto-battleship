package controllers;

import mx.itson.utils.dtos.*;
import services.ServicioConexion;
import services.ListenerServidor;
import views.FlujoVista;
import java.util.List;
import javax.swing.SwingUtilities;
import mx.itson.utils.enums.TipoMensaje;

/**
 * Controlador MVC para la lista de jugadores disponibles
 * Maneja la actualización de la lista y la selección de oponente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ControladorListaJugadores implements ListenerServidor.ICallbackMensaje {

    private final ServicioConexion servicioConexion;
    private IVistaListaJugadores vista;
    private ListenerServidor listenerServidor;
    private JugadorDTO jugadorLocal;
    private ControladorJuego controladorJuego;
    private SolicitudPartidaDTO solicitudActual;

    /**
     * Interfaz que debe implementar la vista
     */
    public interface IVistaListaJugadores {
        void actualizarListaJugadores(List<JugadorDTO> jugadores);
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void jugadorSeleccionado(JugadorDTO jugador);
        void mostrarSinJugadores();
        void mostrarInvitacion(SolicitudPartidaDTO solicitud);
        void partidaAceptada(SolicitudPartidaDTO solicitud);
        void partidaRechazada(String mensaje);
        void partidaIniciada(SolicitudPartidaDTO solicitud);
    }

    /**
     * Constructor
     * @param servicioConexion Servicio de conexión activo
     * @param jugadorLocal Jugador local registrado
     */
    public ControladorListaJugadores(ServicioConexion servicioConexion, JugadorDTO jugadorLocal) {
        this.servicioConexion = servicioConexion;
        this.jugadorLocal = jugadorLocal;
    }

    /**
     * Establece la vista asociada
     * @param vista Vista que implementa IVistaListaJugadores
     */
    public void setVista(IVistaListaJugadores vista) {
        this.vista = vista;
    }

    /**
     * Inicia el listener del servidor y solicita la lista inicial
     */
    public void iniciar() {
        // Solo crear listener si no existe o no está ejecutando
        if (listenerServidor == null || !listenerServidor.isEjecutando()) {
            System.out.println("[CONTROLADOR] Creando nuevo listener");
            listenerServidor = new ListenerServidor(servicioConexion.getEntrada(), this);
            listenerServidor.start();
            System.out.println("[CONTROLADOR] Listener iniciado");
        } else {
            System.out.println("[CONTROLADOR] Listener ya existe y está activo, reutilizando");
        }

        // Solicitar lista inicial de jugadores
        solicitarListaJugadores();
    }

    /**
     * Solicita la lista de jugadores disponibles al servidor
     * La respuesta será manejada de forma asíncrona por el ListenerServidor
     */
    public void solicitarListaJugadores() {
        try {
            System.out.println("[CONTROLADOR] Solicitando lista de jugadores...");
            servicioConexion.solicitarJugadores();
        } catch (Exception e) {
            System.err.println("[CONTROLADOR] Error al solicitar jugadores: " + e.getMessage());
            vista.mostrarError("Error al obtener lista de jugadores: " + e.getMessage());
        }
    }

    /**
     * Maneja la selección de un jugador y envía solicitud de partida
     * @param jugadorSeleccionado Jugador seleccionado como oponente
     */
    public void seleccionarOponente(JugadorDTO jugadorSeleccionado) {
        try {
            System.out.println("[CONTROLADOR] Enviando solicitud a: " + jugadorSeleccionado.getNombre());

            // Crear solicitud
            SolicitudPartidaDTO solicitud = new SolicitudPartidaDTO(
                jugadorLocal.getId(),
                jugadorLocal.getNombre(),
                jugadorSeleccionado.getId(),
                jugadorSeleccionado.getNombre()
            );

            // Enviar al servidor
            MensajeDTO mensaje = new MensajeDTO(
                TipoMensaje.SOLICITAR_PARTIDA,
                "Solicitud de partida",
                solicitud
            );

            servicioConexion.enviarMensaje(mensaje);

            vista.jugadorSeleccionado(jugadorSeleccionado);

        } catch (Exception e) {
            vista.mostrarError("Error al enviar solicitud: " + e.getMessage());
        }
    }

    /**
     * Acepta una invitación de partida
     */
    public void aceptarInvitacion() {
        try {
            MensajeDTO mensaje = new MensajeDTO(
                TipoMensaje.ACEPTAR_PARTIDA,
                "Aceptar invitación"
            );
            servicioConexion.enviarMensaje(mensaje);
        } catch (Exception e) {
            vista.mostrarError("Error al aceptar: " + e.getMessage());
        }
    }

    /**
     * Rechaza una invitación de partida
     */
    public void rechazarInvitacion() {
        try {
            MensajeDTO mensaje = new MensajeDTO(
                TipoMensaje.RECHAZAR_PARTIDA,
                "Rechazar invitación"
            );
            servicioConexion.enviarMensaje(mensaje);
        } catch (Exception e) {
            vista.mostrarError("Error al rechazar: " + e.getMessage());
        }
    }

    // Implementación de ICallbackMensaje

    @Override
    public void onMensajeRecibido(MensajeDTO mensaje) {
        SwingUtilities.invokeLater(() -> procesarMensaje(mensaje));
    }

    @Override
    public void onError(Exception e) {
        SwingUtilities.invokeLater(() -> {
            vista.mostrarError("Error de conexión: " + e.getMessage());
        });
    }

    @Override
    public void onDesconexion() {
        SwingUtilities.invokeLater(() -> {
            vista.mostrarError("Se perdió la conexión con el servidor");
        });
    }

    /**
     * Procesa mensajes recibidos del servidor
     * @param mensaje Mensaje a procesar
     */
    private void procesarMensaje(MensajeDTO mensaje) {
        System.out.println("[CONTROLADOR] Procesando mensaje: " + mensaje.getTipo());

        // Si ya existe un controlador de juego, delegar mensajes relacionados con el juego
        if (controladorJuego != null) {
            switch (mensaje.getTipo()) {
                case NAVES_COLOCADAS:
                case ESPERANDO_OPONENTE_NAVES:
                case AMBOS_LISTOS:
                case TURNO_INICIADO:
                case CAMBIO_TURNO:
                case TURNO_TIMEOUT:
                case ACTUALIZAR_TIEMPO_TURNO:
                case RESULTADO_DISPARO:
                case ACTUALIZAR_TABLEROS:
                case PARTIDA_GANADA:
                case PARTIDA_PERDIDA:
                case PARTIDA_FINALIZADA:
                case PARTIDA_ABANDONADA:
                    System.out.println("[CONTROLADOR] Delegando mensaje al ControladorJuego");
                    controladorJuego.onMensajeRecibido(mensaje);
                    return;
            }
        }

        switch (mensaje.getTipo()) {
            case LISTA_JUGADORES:
                @SuppressWarnings("unchecked")
                List<JugadorDTO> jugadores = (List<JugadorDTO>) mensaje.getDatos();
                vista.actualizarListaJugadores(jugadores);
                break;

            case SIN_JUGADORES_DISPONIBLES:
                vista.mostrarSinJugadores();
                break;

            case JUGADOR_DISPONIBLE:
                // Cuando se conecta un nuevo jugador, solicitar lista actualizada
                solicitarListaJugadores();
                break;

            case INVITACION_RECIBIDA:
                SolicitudPartidaDTO invitacion = (SolicitudPartidaDTO) mensaje.getDatos();
                vista.mostrarInvitacion(invitacion);
                break;

            case PARTIDA_ACEPTADA:
                SolicitudPartidaDTO solicitudAceptada = (SolicitudPartidaDTO) mensaje.getDatos();
                vista.partidaAceptada(solicitudAceptada);
                break;

            case PARTIDA_RECHAZADA:
                vista.partidaRechazada(mensaje.getContenido());
                break;

            case PARTIDA_INICIADA:
                SolicitudPartidaDTO partidaIniciada = (SolicitudPartidaDTO) mensaje.getDatos();
                solicitudActual = partidaIniciada;
                vista.partidaIniciada(partidaIniciada);
                break;

            case COLOCAR_NAVES:
                // Cuando el servidor pide colocar naves, iniciamos el flujo del juego
                iniciarJuego(mensaje);
                break;

            case ERROR:
                String error = mensaje.getContenido();
                if (error == null || error.isEmpty()) {
                    error = "Error desconocido";
                }
                vista.mostrarError(error);
                break;

            default:
                System.out.println("[CONTROLADOR] Tipo de mensaje no manejado: " + mensaje.getTipo());
        }
    }

    /**
     * Detiene el listener
     */
    public void detener() {
        if (listenerServidor != null) {
            listenerServidor.detener();
        }
    }

    /**
     * Resetea el estado del controlador después de una partida
     * Esto permite iniciar nuevas partidas sin interferencia del controlador anterior
     */
    public void resetearEstadoJuego() {
        System.out.println("[CONTROLADOR] Reseteando estado de juego anterior");
        controladorJuego = null;
        solicitudActual = null;
    }

    /**
     * Inicia el flujo del juego creando el ControladorJuego y cambiando a la vista de colocación
     */
    private void iniciarJuego(MensajeDTO mensajeInicial) {
        if (solicitudActual == null) {
            System.err.println("[CONTROLADOR] No hay solicitud activa para iniciar juego");
            return;
        }

        System.out.println("[CONTROLADOR] Iniciando flujo de juego");

        // Determinar quién es el oponente
        JugadorDTO oponente;
        if (solicitudActual.getIdSolicitante().equals(jugadorLocal.getId())) {
            // Yo solicité, el oponente es el invitado
            oponente = new JugadorDTO(
                solicitudActual.getIdInvitado(),
                solicitudActual.getNombreInvitado(),
                null
            );
        } else {
            // Me invitaron, el oponente es el solicitante
            oponente = new JugadorDTO(
                solicitudActual.getIdSolicitante(),
                solicitudActual.getNombreSolicitante(),
                null
            );
        }

        // Obtener ID de partida del mensaje (está en el campo datos)
        String idPartida = (String) mensajeInicial.getDatos();

        // Crear controlador de juego
        controladorJuego = new ControladorJuego(
            servicioConexion,
            jugadorLocal,
            oponente,
            idPartida
        );

        System.out.println("[CONTROLADOR] ControladorJuego creado. Los mensajes del juego se delegarán automáticamente.");

        // Procesar el mensaje inicial con el controlador de juego
        controladorJuego.onMensajeRecibido(mensajeInicial);

        // Cambiar a la vista de colocación de naves
        // El listener original sigue ejecutándose pero ahora delega mensajes del juego
        FlujoVista.mostrarColocacionNaves(servicioConexion, jugadorLocal, oponente, controladorJuego);
    }

    /**
     * Obtiene el jugador local
     * @return JugadorDTO del jugador local
     */
    public JugadorDTO getJugadorLocal() {
        return jugadorLocal;
    }

    /**
     * Obtiene el servicio de conexión
     * @return ServicioConexion
     */
    public ServicioConexion getServicioConexion() {
        return servicioConexion;
    }
}
