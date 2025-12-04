package controllers;

import models.EstadoLocalJuego;
import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import services.ServicioConexion;
import services.ListenerServidor;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.ArrayList;

/**
 * Controlador principal del juego multijugador (MVC)
 * Gestiona el flujo completo de la partida desde la colocación de naves hasta el final
 *
 * ARQUITECTURA MVC:
 * - Modelo: EstadoLocalJuego (contiene DTOs + patrón Observer)
 * - Vista: IVistaJuego, IVistaColocacionNaves
 * - Controlador: Esta clase (ControladorJuego)
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ControladorJuego implements ListenerServidor.ICallbackMensaje {

    // Servicios
    private final ServicioConexion servicioConexion;

    // MODELO (MVC) - Estado local del juego con patrón Observer
    private final EstadoLocalJuego estadoLocal;

    // Estado del controlador
    private EstadoJuego estadoActual;

    // Referencias a vistas
    private IVistaColocacionNaves vistaColocacion;
    private IVistaJuego vistaJuego;

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
        void actualizarTiempoTurno(int tiempoRestante);
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void partidaFinalizada(boolean gane, JugadorDTO ganador, EstadisticaDTO misEstadisticas);
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

        // Crear el MODELO con patrón Observer
        this.estadoLocal = new EstadoLocalJuego(jugadorLocal, oponente, idPartida);

        this.estadoActual = EstadoJuego.ESPERANDO_COLOCACION;

        System.out.println("[CONTROLADOR_JUEGO] Iniciado para partida: " + idPartida);
    }

    /**
     * Establece la vista de colocación de naves
     * @param vista
     */
    public void setVistaColocacion(IVistaColocacionNaves vista) {
        this.vistaColocacion = vista;
    }

    /**
     * Establece la vista del juego
     * @param vista
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
        if (!estadoLocal.isMiTurno()) {
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
                boolean esMiTurno = jugadorEnTurno.getId().equals(estadoLocal.getJugadorLocal().getId());
                estadoActual = EstadoJuego.EN_JUEGO;

                // Actualizar MODELO (notifica a observadores si es necesario)
                estadoLocal.cambiarTurno(esMiTurno, jugadorEnTurno);

                System.out.println("[CONTROLADOR_JUEGO] Turno iniciado - Mi turno: " + esMiTurno);

                if (vistaJuego != null) {
                    vistaJuego.actualizarTurno(esMiTurno, jugadorEnTurno);
                }
                break;

            case CAMBIO_TURNO:
                JugadorDTO nuevoTurno = (JugadorDTO) mensaje.getDatos();
                boolean esNuevoMiTurno = nuevoTurno.getId().equals(estadoLocal.getJugadorLocal().getId());

                // Actualizar MODELO
                estadoLocal.cambiarTurno(esNuevoMiTurno, nuevoTurno);

                System.out.println("[CONTROLADOR_JUEGO] Cambio de turno - Mi turno: " + esNuevoMiTurno);

                if (vistaJuego != null) {
                    vistaJuego.actualizarTurno(esNuevoMiTurno, nuevoTurno);
                }
                break;

            case TURNO_TIMEOUT:
                System.out.println("[CONTROLADOR_JUEGO] Timeout de turno: " + mensaje.getContenido());
                if (vistaJuego != null) {
                    vistaJuego.mostrarMensaje("⏱️ " + mensaje.getContenido());
                }
                break;

            case ACTUALIZAR_TIEMPO_TURNO:
                Integer tiempoRestante = (Integer) mensaje.getDatos();

                // Actualizar MODELO
                estadoLocal.actualizarTiempo(tiempoRestante);

                System.out.println("[CONTROLADOR_JUEGO] Actualización tiempo: " + tiempoRestante + "s");
                if (vistaJuego != null) {
                    vistaJuego.actualizarTiempoTurno(tiempoRestante);
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
                System.out.println("[CONTROLADOR_JUEGO] Mi ID local: " + estadoLocal.getJugadorLocal().getId());

                TableroDTO miTablero = null;
                TableroDTO tableroOponente = null;

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

                    if (tablero.getIdJugador().equals(estadoLocal.getJugadorLocal().getId())) {
                        miTablero = tablero;
                        System.out.println("[CONTROLADOR_JUEGO] Asignado como MI TABLERO");
                    } else {
                        tableroOponente = tablero;
                        System.out.println("[CONTROLADOR_JUEGO] Asignado como TABLERO OPONENTE");
                    }
                }

                // Actualizar MODELO
                if (miTablero != null && tableroOponente != null) {
                    estadoLocal.actualizarTableros(miTablero, tableroOponente);
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
            case PARTIDA_PERDIDA:
                Object datosRecibidos = mensaje.getDatos();
                EstadisticaDTO misStats = null;

                if (datosRecibidos instanceof List) {
                    List<EstadisticaDTO> listaStats = (List<EstadisticaDTO>) datosRecibidos;

                    for (EstadisticaDTO s : listaStats) {
                        if (s.getNombreJugador().equals(estadoLocal.getJugadorLocal().getNombre())) {
                            misStats = s;
                            break;
                        }
                    }

                    boolean gane = (mensaje.getTipo() == TipoMensaje.PARTIDA_GANADA);
                    JugadorDTO ganadorDTO = gane ? estadoLocal.getJugadorLocal() : estadoLocal.getOponente();

                    // Actualizar MODELO
                    estadoLocal.finalizarPartida(gane, ganadorDTO);

                    if (vistaJuego != null && misStats != null) {
                        vistaJuego.partidaFinalizada(gane, ganadorDTO, misStats);
                    }

                } else if (datosRecibidos instanceof JugadorDTO) {
                    System.err.println("ERROR CRÍTICO: El servidor envió un JugadorDTO "
                            + "en lugar de las Estadísticas.");
                    System.err.println("Por favor actualiza el código del Servidor.");

                    boolean gane = (mensaje.getTipo() == TipoMensaje.PARTIDA_GANADA);
                    EstadisticaDTO statsF = new EstadisticaDTO(
                            estadoLocal.getJugadorLocal().getNombre(), gane, 0, 0,0);

                    if (vistaJuego != null) {
                        vistaJuego.partidaFinalizada(gane, (JugadorDTO) datosRecibidos, statsF);
                    }
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
    
    // Getters

    public JugadorDTO getJugadorLocal() {
        return estadoLocal.getJugadorLocal();
    }

    public JugadorDTO getOponente() {
        return estadoLocal.getOponente();
    }

    public String getIdPartida() {
        return estadoLocal.getIdPartida();
    }

    public boolean isMiTurno() {
        return estadoLocal.isMiTurno();
    }

    public EstadoJuego getEstadoActual() {
        return estadoActual;
    }

    public ServicioConexion getServicioConexion() {
        return servicioConexion;
    }

    /**
     * Obtiene el modelo del juego (EstadoLocalJuego)
     * @return Estado local del juego
     */
    public EstadoLocalJuego getEstadoLocal() {
        return estadoLocal;
    }
}
