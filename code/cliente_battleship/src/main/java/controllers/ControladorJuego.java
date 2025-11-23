package controllers;

import static enums.ResultadoDisparo.AGUA;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import services.ServicioConexion;
import services.ListenerServidor;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador principal del juego multijugador
 * Gestiona el flujo completo de la partida desde la colocación de naves hasta el final
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ControladorJuego implements ListenerServidor.ICallbackMensaje {

    // Estados del juego
    private enum EstadoJuego {
        ESPERANDO_COLOCACION,
        ESPERANDO_OPONENTE,
        EN_JUEGO,
        FINALIZADO
    }

    private final ServicioConexion servicioConexion;
    private final JugadorDTO jugadorLocal;
    private JugadorDTO oponente;
    private String idPartida;

    private EstadoJuego estadoActual;
    private boolean miTurno;

    // Referencias a vistas
    private IVistaColocacionNaves vistaColocacion;
    private IVistaJuego vistaJuego;

    // Datos del juego
    private TableroDTO miTablero;
    private TableroDTO tableroOponente;

    /**
     * Interfaz para la vista de colocación de naves
     */
    public interface IVistaColocacionNaves {
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void navesColocadas();
        void esperandoOponente();
        void iniciarJuego();
    }

    /**
     * Interfaz para la vista del juego
     */
    public interface IVistaJuego {
        void actualizarTableros(TableroDTO miTablero, TableroDTO tableroOponente);
        void mostrarResultadoDisparo(DisparoDTO disparo);
        void actualizarTurno(boolean miTurno, JugadorDTO jugadorEnTurno);
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void partidaFinalizada(boolean gane, JugadorDTO ganador);
    }

    /**
     * Constructor
     * @param servicioConexion Servicio de conexión activo
     * @param jugadorLocal Jugador local
     * @param oponente Oponente
     * @param idPartida ID de la partida
     */
    public ControladorJuego(ServicioConexion servicioConexion, JugadorDTO jugadorLocal,
                           JugadorDTO oponente, String idPartida) {
        this.servicioConexion = servicioConexion;
        this.jugadorLocal = jugadorLocal;
        this.oponente = oponente;
        this.idPartida = idPartida;
        this.estadoActual = EstadoJuego.ESPERANDO_COLOCACION;
        this.miTurno = false;

        System.out.println("[CONTROLADOR_JUEGO] Iniciado para partida: " + idPartida);
    }

    /**
     * Establece la vista de colocación de naves
     */
    public void setVistaColocacion(IVistaColocacionNaves vista) {
        this.vistaColocacion = vista;
    }

    /**
     * Establece la vista del juego
     */
    public void setVistaJuego(IVistaJuego vista) {
        this.vistaJuego = vista;
    }

    /**
     * Envía la configuración de naves al servidor
     * @param naves Lista de naves colocadas
     */
    public void enviarNavesColocadas(List<NaveDTO> naves) {
        try {
            System.out.println("[CONTROLADOR_JUEGO] Enviando " + naves.size() + " naves al servidor");

            // Asegurar que sea ArrayList para serialización correcta
            ArrayList<NaveDTO> navesSerializables = new ArrayList<>(naves);

            System.out.println("[CONTROLADOR_JUEGO] Debug - Naves a enviar:");
            for (int i = 0; i < navesSerializables.size(); i++) {
                NaveDTO nave = navesSerializables.get(i);
                System.out.println("  [" + i + "] " + nave.getTipo() +
                    " - Long: " + nave.getLongitudTotal() +
                    " - Coords: " + nave.getCoordenadas().length);
            }

            MensajeDTO mensaje = new MensajeDTO(
                TipoMensaje.COLOCAR_NAVES,
                "Naves colocadas",
                navesSerializables
            );

            System.out.println("[CONTROLADOR_JUEGO] Enviando mensaje al servidor...");
            servicioConexion.enviarMensaje(mensaje);
            estadoActual = EstadoJuego.ESPERANDO_OPONENTE;
            System.out.println("[CONTROLADOR_JUEGO] Mensaje enviado exitosamente");

        } catch (Exception e) {
            System.err.println("[CONTROLADOR_JUEGO] Error al enviar naves: " + e.getMessage());
            e.printStackTrace();
            if (vistaColocacion != null) {
                vistaColocacion.mostrarError("Error al enviar naves: " + e.getMessage());
            }
        }
    }

    /**
     * Envía un disparo al servidor
     * @param coordenada Coordenada del disparo
     */
    public void realizarDisparo(CoordenadaDTO coordenada) {
        if (!miTurno) {
            if (vistaJuego != null) {
                vistaJuego.mostrarError("No es tu turno");
            }
            return;
        }

        if (estadoActual != EstadoJuego.EN_JUEGO) {
            return;
        }

        try {
            System.out.println("[CONTROLADOR_JUEGO] Disparando a (" + coordenada.getX() + "," + coordenada.getY() + ")");

            MensajeDTO mensaje = new MensajeDTO(
                TipoMensaje.ENVIAR_DISPARO,
                "Disparo realizado",
                coordenada
            );

            servicioConexion.enviarMensaje(mensaje);

        } catch (Exception e) {
            System.err.println("[CONTROLADOR_JUEGO] Error al enviar disparo: " + e.getMessage());
            if (vistaJuego != null) {
                vistaJuego.mostrarError("Error al enviar disparo: " + e.getMessage());
            }
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
            System.err.println("[CONTROLADOR_JUEGO] Error de conexión: " + e.getMessage());
            if (vistaJuego != null) {
                vistaJuego.mostrarError("Error de conexión: " + e.getMessage());
            } else if (vistaColocacion != null) {
                vistaColocacion.mostrarError("Error de conexión: " + e.getMessage());
            }
        });
    }

    @Override
    public void onDesconexion() {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[CONTROLADOR_JUEGO] Desconectado del servidor");
            if (vistaJuego != null) {
                vistaJuego.mostrarError("Se perdió la conexión con el servidor");
            } else if (vistaColocacion != null) {
                vistaColocacion.mostrarError("Se perdió la conexión con el servidor");
            }
        });
    }

    /**
     * Procesa mensajes recibidos del servidor
     */
    private void procesarMensaje(MensajeDTO mensaje) {
        System.out.println("[CONTROLADOR_JUEGO] Procesando: " + mensaje.getTipo() + " - Estado: " + estadoActual);

        switch (mensaje.getTipo()) {
            case COLOCAR_NAVES:
                // Confirmación de que debemos colocar naves
                System.out.println("[CONTROLADOR_JUEGO] Servidor solicita colocar naves");
                break;

            case NAVES_COLOCADAS:
                if (vistaColocacion != null) {
                    vistaColocacion.navesColocadas();
                }
                break;

            case ESPERANDO_OPONENTE_NAVES:
                if (vistaColocacion != null) {
                    vistaColocacion.esperandoOponente();
                }
                break;

            case AMBOS_LISTOS:
                System.out.println("[CONTROLADOR_JUEGO] Ambos jugadores listos");
                if (vistaColocacion != null) {
                    vistaColocacion.iniciarJuego();
                }
                break;

            case TURNO_INICIADO:
                JugadorDTO jugadorEnTurno = (JugadorDTO) mensaje.getDatos();
                miTurno = jugadorEnTurno.getId().equals(jugadorLocal.getId());
                estadoActual = EstadoJuego.EN_JUEGO;

                System.out.println("[CONTROLADOR_JUEGO] Turno iniciado - Mi turno: " + miTurno);

                if (vistaJuego != null) {
                    vistaJuego.actualizarTurno(miTurno, jugadorEnTurno);
                }
                break;

            case CAMBIO_TURNO:
                JugadorDTO nuevoTurno = (JugadorDTO) mensaje.getDatos();
                miTurno = nuevoTurno.getId().equals(jugadorLocal.getId());

                System.out.println("[CONTROLADOR_JUEGO] Cambio de turno - Mi turno: " + miTurno);

                if (vistaJuego != null) {
                    vistaJuego.actualizarTurno(miTurno, nuevoTurno);
                }
                break;

            case TURNO_TIMEOUT:
                System.out.println("[CONTROLADOR_JUEGO] Timeout de turno: " + mensaje.getContenido());
                if (vistaJuego != null) {
                    vistaJuego.mostrarMensaje("⏱️ " + mensaje.getContenido());
                }
                break;

            case RESULTADO_DISPARO:
                DisparoDTO disparo = (DisparoDTO) mensaje.getDatos();
                System.out.println("[CONTROLADOR_JUEGO] Resultado disparo: " + disparo.getResultado());

                if (vistaJuego != null) {
                    vistaJuego.mostrarResultadoDisparo(disparo);
                }
                break;

            case ACTUALIZAR_TABLEROS:
                System.out.println("[CONTROLADOR_JUEGO] ========== RECIBIENDO ACTUALIZAR_TABLEROS ==========");

                @SuppressWarnings("unchecked")
                List<TableroDTO> tableros = (List<TableroDTO>) mensaje.getDatos();

                System.out.println("[CONTROLADOR_JUEGO] Número de tableros recibidos: " + tableros.size());
                System.out.println("[CONTROLADOR_JUEGO] Mi ID local: " + jugadorLocal.getId());

                // Identificar cuál tablero es cuál
                for (int i = 0; i < tableros.size(); i++) {
                    TableroDTO tablero = tableros.get(i);
                    System.out.println("[CONTROLADOR_JUEGO] Tablero " + i + " ID: " + tablero);

                    // Contar disparos
                    int disparos = 0;
                    for (int f = 0; f < 10; f++) {
                        for (int c = 0; c < 10; c++) {
                            EstadoCasilla estado = tablero.getCasillas()[f][c];
                            if (estado != EstadoCasilla.VACIA && estado != EstadoCasilla.OCUPADA) {
                                disparos++;
                                System.out.println("[CONTROLADOR_JUEGO] Tablero " + i + "[" + f + "," + c + "]: " + estado);
                            }
                        }
                    }
                    System.out.println("[CONTROLADOR_JUEGO] Total disparos en tablero " + i + ": " + disparos);

                    if (tablero.getIdJugador().equals(jugadorLocal.getId())) {
                        miTablero = tablero;
                        System.out.println("[CONTROLADOR_JUEGO] Asignado como MI TABLERO");
                    } else {
                        tableroOponente = tablero;
                        System.out.println("[CONTROLADOR_JUEGO] Asignado como TABLERO OPONENTE");
                    }
                }

                System.out.println("[CONTROLADOR_JUEGO] miTablero: " + (miTablero != null ? "OK" : "NULL"));
                System.out.println("[CONTROLADOR_JUEGO] tableroOponente: " + (tableroOponente != null ? "OK" : "NULL"));
                System.out.println("[CONTROLADOR_JUEGO] vistaJuego: " + (vistaJuego != null ? "OK" : "NULL"));

                if (vistaJuego != null && miTablero != null && tableroOponente != null) {
                    System.out.println("[CONTROLADOR_JUEGO] Llamando a vistaJuego.actualizarTableros()");
                    vistaJuego.actualizarTableros(miTablero, tableroOponente);
                } else {
                    System.err.println("[CONTROLADOR_JUEGO] ERROR: No se puede actualizar vista!");
                    if (vistaJuego == null) System.err.println("  - vistaJuego es null");
                    if (miTablero == null) System.err.println("  - miTablero es null");
                    if (tableroOponente == null) System.err.println("  - tableroOponente es null");
                }

                System.out.println("[CONTROLADOR_JUEGO] ========== FIN ACTUALIZAR_TABLEROS ==========");
                break;

            case PARTIDA_GANADA:
                JugadorDTO ganador = (JugadorDTO) mensaje.getDatos();
                estadoActual = EstadoJuego.FINALIZADO;

                System.out.println("[CONTROLADOR_JUEGO] ¡Partida ganada!");

                if (vistaJuego != null) {
                    vistaJuego.partidaFinalizada(true, ganador);
                }
                break;

            case PARTIDA_PERDIDA:
                JugadorDTO perdedor = (JugadorDTO) mensaje.getDatos();
                estadoActual = EstadoJuego.FINALIZADO;

                System.out.println("[CONTROLADOR_JUEGO] Partida perdida");

                if (vistaJuego != null) {
                    vistaJuego.partidaFinalizada(false, oponente);
                }
                break;

            case ERROR:
                String error = mensaje.getContenido();
                System.err.println("[CONTROLADOR_JUEGO] Error del servidor: " + error);

                if (vistaJuego != null) {
                    vistaJuego.mostrarError(error);
                } else if (vistaColocacion != null) {
                    vistaColocacion.mostrarError(error);
                }
                break;

            default:
                System.out.println("[CONTROLADOR_JUEGO] Mensaje no manejado: " + mensaje.getTipo());
        }
    }

    public EstadisticaDTO generarEstadisticas(JugadorDTO ganador) {
        
        return null; 
    }
    
    // Getters

    public JugadorDTO getJugadorLocal() {
        return jugadorLocal;
    }

    public JugadorDTO getOponente() {
        return oponente;
    }

    public String getIdPartida() {
        return idPartida;
    }

    public boolean isMiTurno() {
        return miTurno;
    }

    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }

    public ServicioConexion getServicioConexion() {
        return servicioConexion;
    }
}
