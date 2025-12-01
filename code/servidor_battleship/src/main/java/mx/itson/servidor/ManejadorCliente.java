package mx.itson.servidor;

import mx.itson.utils.dtos.*;
import mx.itson.utils.enums.*;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import mx.itson.exceptions.GestorPartidasException;
import mx.itson.models.IJugador;
import mx.itson.subsistema_gestor_partidas.IGestorPartidas;

/**
 * Maneja la comunicación con un cliente conectado
 * Corre en un thread separado por cada cliente
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final GestorJugadores gestorJugadores;
    private final IGestorPartidas gestorPartidas;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private JugadorDTO jugadorAsociado;
    private boolean conectado;

    /**
     * Constructor
     * @param socket Socket del cliente
     * @param gestorJugadores Gestor de jugadores del servidor
     * @param gestorPartidas Gestor de partidas del servidor
     */
    public ManejadorCliente(Socket socket, GestorJugadores gestorJugadores, IGestorPartidas gestorPartidas) {
        this.socket = socket;
        this.gestorJugadores = gestorJugadores;
        this.gestorPartidas = gestorPartidas;
        this.conectado = true;
    }

    @Override
    public void run() {
        try {
            // Configurar streams de comunicación
            salida = new ObjectOutputStream(socket.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socket.getInputStream());

            System.out.println("[MANEJADOR] Cliente conectado desde: " + socket.getInetAddress());

            // Procesar mensajes del cliente
            while (conectado) {
                MensajeDTO mensaje = (MensajeDTO) entrada.readObject();
                procesarMensaje(mensaje);
            }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[MANEJADOR] Error en comunicación: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    /**
     * Procesa un mensaje recibido del cliente
     * @param mensaje Mensaje a procesar
     */
    private void procesarMensaje(MensajeDTO mensaje) {
        System.out.println("[MANEJADOR] Mensaje recibido: " + mensaje.getTipo());

        switch (mensaje.getTipo()) {
            case REGISTRO_JUGADOR -> procesarRegistro(mensaje);

            case SOLICITAR_JUGADORES -> enviarListaJugadores();

            case SOLICITAR_PARTIDA -> procesarSolicitudPartida(mensaje);

            case ACEPTAR_PARTIDA -> procesarAceptarPartida(mensaje);

            case RECHAZAR_PARTIDA -> procesarRechazarPartida(mensaje);

            case COLOCAR_NAVES -> procesarColocacionNaves(mensaje);

            case ENVIAR_DISPARO -> procesarDisparo(mensaje);

            case DESCONEXION -> conectado = false;

            default -> System.err.println("[MANEJADOR] Tipo de mensaje no manejado: " + mensaje.getTipo());
        }
    }

    /**
     * Procesa el registro de un jugador
     * @param mensaje Mensaje con datos de registro
     */
    private void procesarRegistro(MensajeDTO mensaje) {
        RegistroJugadorDTO registro = (RegistroJugadorDTO) mensaje.getDatos();

        // Verificar que el nombre no esté duplicado
        if (!gestorJugadores.nombreDisponible(registro.getNombre())) {
            // Nombre duplicado
            MensajeDTO respuesta = new MensajeDTO(
                    TipoMensaje.NOMBRE_DUPLICADO,
                    "El nombre '" + registro.getNombre() + "' ya está en uso. Por favor, elija otro."
            );
            enviarMensaje(respuesta);
            return;
        }

        // Crear jugador con ID único
        String id = UUID.randomUUID().toString();
        jugadorAsociado = new JugadorDTO(id, registro.getNombre(), registro.getColor());

        // Registrar en el gestor
        boolean registrado = gestorJugadores.registrarJugador(jugadorAsociado, this);

        if (registrado) {
            // Registro exitoso
            MensajeDTO respuesta = new MensajeDTO(
                    TipoMensaje.REGISTRO_EXITOSO,
                    "Jugador registrado exitosamente",
                    jugadorAsociado
            );
            enviarMensaje(respuesta);

            // Enviar lista de jugadores disponibles
            enviarListaJugadores();
        } else {
            // Error en el registro
            MensajeDTO respuesta = new MensajeDTO(
                    TipoMensaje.ERROR,
                    "Error al registrar jugador"
            );
            enviarMensaje(respuesta);
        }
    }

    /**
     * Envía la lista de jugadores disponibles al cliente
     */
    private void enviarListaJugadores() {
        System.out.println("[MANEJADOR] ========== ENVIANDO LISTA DE JUGADORES ==========");
        List<JugadorDTO> disponibles = gestorJugadores.obtenerJugadoresDisponibles();

        // Filtrar para no incluir al propio jugador
        if (jugadorAsociado != null) {
            disponibles = new java.util.ArrayList<>(disponibles.stream()
                    .filter(j -> !j.getId().equals(jugadorAsociado.getId()))
                    .toList());
        }

        System.out.println("[MANEJADOR] Jugadores disponibles: " + disponibles.size());

        if (disponibles.isEmpty()) {
            // No hay jugadores disponibles
            MensajeDTO respuesta = new MensajeDTO(
                    TipoMensaje.SIN_JUGADORES_DISPONIBLES,
                    "No hay jugadores disponibles en este momento. Esperando..."
            );
            System.out.println("[MANEJADOR] Enviando SIN_JUGADORES_DISPONIBLES");
            System.out.println("[MANEJADOR] Tipo de objeto a enviar: " + respuesta.getClass().getName());
            enviarMensaje(respuesta);
        } else {
            // Enviar lista de jugadores disponibles
            MensajeDTO respuesta = new MensajeDTO(
                    TipoMensaje.LISTA_JUGADORES,
                    "Jugadores disponibles",
                    disponibles
            );
            System.out.println("[MANEJADOR] Enviando LISTA_JUGADORES");
            System.out.println("[MANEJADOR] Tipo de objeto a enviar: " + respuesta.getClass().getName());
            System.out.println("[MANEJADOR] Contenido del mensaje: " + respuesta.getContenido());
            enviarMensaje(respuesta);
        }
        System.out.println("[MANEJADOR] ========== FIN ENVIAR LISTA ==========");
    }

    /**
     * Notifica al cliente que hay un jugador disponible
     * Llamado por el GestorJugadores cuando un nuevo jugador se conecta
     */
    public void notificarJugadorDisponible() {
        if (jugadorAsociado != null && !jugadorAsociado.isEnPartida())
            enviarListaJugadores();
    }

    /**
     * Envía un mensaje al cliente
     * @param mensaje Mensaje a enviar
     */
    private synchronized void enviarMensaje(MensajeDTO mensaje) {
        try {
            salida.writeObject(mensaje);
            salida.flush();
            System.out.println("[MANEJADOR] Mensaje enviado: " + mensaje.getTipo());
        } catch (IOException e) {
            System.err.println("[MANEJADOR] Error al enviar mensaje: " + e.getMessage());
            conectado = false;
        }
    }

    /**
     * Procesa una solicitud de partida de un jugador
     * @param mensaje Mensaje con la solicitud
     */
    private void procesarSolicitudPartida(MensajeDTO mensaje) {
        SolicitudPartidaDTO solicitud = (SolicitudPartidaDTO) mensaje.getDatos();

        System.out.println("[MANEJADOR] Solicitud de partida: " +
                          solicitud.getNombreSolicitante() + " -> " + solicitud.getNombreInvitado());

        // Verificar que el solicitante es el jugador asociado
        if (jugadorAsociado == null || !jugadorAsociado.getId().equals(solicitud.getIdSolicitante())) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "No autorizado para enviar esta solicitud"
            ));
            return;
        }

        // Registrar solicitud en el gestor
        boolean registrada = gestorJugadores.registrarSolicitudPartida(solicitud);

        if (!registrada) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "El jugador no está disponible o ya tiene una invitación pendiente"
            ));
            return;
        }

        // Obtener manejador del invitado y enviarle la invitación
        ManejadorCliente manejadorInvitado = gestorJugadores.obtenerManejador(solicitud.getIdInvitado());
        if (manejadorInvitado != null) {
            MensajeDTO invitacion = new MensajeDTO(
                TipoMensaje.INVITACION_RECIBIDA,
                "Invitación de " + solicitud.getNombreSolicitante(),
                solicitud
            );
            manejadorInvitado.enviarMensaje(invitacion);

            // Confirmar al solicitante que se envió la invitación
            enviarMensaje(new MensajeDTO(
                TipoMensaje.SOLICITAR_PARTIDA,
                "Invitación enviada a " + solicitud.getNombreInvitado()
            ));
        }
    }

    /**
     * Procesa la aceptación de una partida
     * @param mensaje Mensaje de aceptación
     */
    private void procesarAceptarPartida(MensajeDTO mensaje) {
        if (jugadorAsociado == null) return;

        // Obtener la solicitud pendiente
        SolicitudPartidaDTO solicitud = gestorJugadores.eliminarSolicitudPendiente(jugadorAsociado.getId());

        if (solicitud == null) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "No hay solicitud pendiente"
            ));
            return;
        }

        try {
            System.out.println("[MANEJADOR] Partida aceptada: " +
                                solicitud.getNombreSolicitante() + " vs " + solicitud.getNombreInvitado());

            // Marcar ambos jugadores como en partida
            gestorJugadores.marcarEnPartida(solicitud.getIdSolicitante());
            gestorJugadores.marcarEnPartida(solicitud.getIdInvitado());

            // Obtener jugadores completos
            JugadorDTO jugador1 = gestorJugadores.obtenerJugador(solicitud.getIdSolicitante());
            JugadorDTO jugador2 = gestorJugadores.obtenerJugador(solicitud.getIdInvitado());

            // Crear partida
            PartidaDTO partida = gestorPartidas.crearPartida(jugador1, jugador2);

            System.out.println("[MANEJADOR] Partida creada: " + partida.getIdPartida());

            // Notificar al solicitante
            ManejadorCliente manejadorSolicitante = gestorJugadores.obtenerManejador(solicitud.getIdSolicitante());
            if (manejadorSolicitante != null) {
                manejadorSolicitante.enviarMensaje(new MensajeDTO(
                    TipoMensaje.PARTIDA_ACEPTADA,
                    solicitud.getNombreInvitado() + " aceptó tu invitación",
                    solicitud
                ));
            }

            // Notificar al invitado (este cliente)
            enviarMensaje(new MensajeDTO(
                TipoMensaje.PARTIDA_INICIADA,
                "Partida iniciada con " + solicitud.getNombreSolicitante(),
                solicitud
            ));

            // Notificar al solicitante que la partida inicia
            if (manejadorSolicitante != null) {
                manejadorSolicitante.enviarMensaje(new MensajeDTO(
                    TipoMensaje.PARTIDA_INICIADA,
                    "Partida iniciada con " + solicitud.getNombreInvitado(),
                    solicitud
                ));
            }

            // Enviar mensaje para colocar naves
            enviarMensaje(new MensajeDTO(
                TipoMensaje.COLOCAR_NAVES,
                "Coloca tus naves en el tablero",
                partida.getIdPartida()
            ));

            if (manejadorSolicitante != null) {
                manejadorSolicitante.enviarMensaje(new MensajeDTO(
                    TipoMensaje.COLOCAR_NAVES,
                    "Coloca tus naves en el tablero",
                    partida.getIdPartida()
                ));
            }
        } catch (GestorPartidasException e) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                e.getMessage()
            ));
        }
    }

    /**
     * Procesa el rechazo de una partida
     * @param mensaje Mensaje de rechazo
     */
    private void procesarRechazarPartida(MensajeDTO mensaje) {
        if (jugadorAsociado == null) return;

        // Obtener y eliminar la solicitud pendiente
        SolicitudPartidaDTO solicitud = gestorJugadores.eliminarSolicitudPendiente(jugadorAsociado.getId());

        if (solicitud == null) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "No hay solicitud pendiente"
            ));
            return;
        }

        System.out.println("[MANEJADOR] Partida rechazada: " +
                          solicitud.getNombreSolicitante() + " vs " + solicitud.getNombreInvitado());

        // Notificar al solicitante
        ManejadorCliente manejadorSolicitante = gestorJugadores.obtenerManejador(solicitud.getIdSolicitante());
        if (manejadorSolicitante != null) {
            manejadorSolicitante.enviarMensaje(new MensajeDTO(
                TipoMensaje.PARTIDA_RECHAZADA,
                solicitud.getNombreInvitado() + " rechazó tu invitación",
                solicitud
            ));
        }

        // Confirmar al invitado
        enviarMensaje(new MensajeDTO(
            TipoMensaje.RECHAZAR_PARTIDA,
            "Invitación rechazada"
        ));
    }

    /**
     * Procesa la colocación de naves de un jugador
     * @param mensaje Mensaje con las naves colocadas
     */
    private void procesarColocacionNaves(MensajeDTO mensaje) {
        
        if (jugadorAsociado == null) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "No hay jugador asociado"
            ));
            return;
        }

        try {
            if (!gestorPartidas.verificarJugadorPartidaActiva(jugadorAsociado.getId())) {
                enviarMensaje(new MensajeDTO(
                    TipoMensaje.ERROR,
                    "No estás en una partida activa"
                ));
                return;
            }

            // Obtener lista de naves del mensaje
            @SuppressWarnings("unchecked")
            List<NaveDTO> naves = (List<NaveDTO>) mensaje.getDatos();

            System.out.println("[MANEJADOR] Recibidas " + naves.size() + " naves de " + jugadorAsociado.getNombre());

            // Colocar naves en la partida
            PartidaDTO partida = gestorPartidas.colocarNaves(
                    gestorPartidas.obtenerPartidaDeJugador(jugadorAsociado.getId()).getIdPartida(), 
                    jugadorAsociado.getId(), 
                    naves
            );

            if (partida == null) {
                enviarMensaje(new MensajeDTO(
                    TipoMensaje.ERROR,
                    "Error al colocar naves. Verifica que sean válidas."
                ));
                return;
            }

            // Confirmar colocación
            enviarMensaje(new MensajeDTO(
                TipoMensaje.NAVES_COLOCADAS,
                "Naves colocadas correctamente"
            ));

            // Verificar si ambos jugadores ya colocaron sus naves
            if (partida.isAmbosJugadoresListos()) {
                System.out.println("[MANEJADOR] Ambos jugadores listos. Iniciando partida " + partida.getIdPartida());

                // Obtener manejadores de ambos jugadores
                ManejadorCliente manejador1 = gestorJugadores.obtenerManejador(partida.getJugador1().getId());
                ManejadorCliente manejador2 = gestorJugadores.obtenerManejador(partida.getJugador2().getId());

                // Notificar que ambos están listos
                if (manejador1 != null) {
                    manejador1.enviarMensaje(new MensajeDTO(
                        TipoMensaje.AMBOS_LISTOS,
                        "Ambos jugadores han colocado sus naves. ¡Iniciando batalla!"
                    ));
                }

                if (manejador2 != null) {
                    manejador2.enviarMensaje(new MensajeDTO(
                        TipoMensaje.AMBOS_LISTOS,
                        "Ambos jugadores han colocado sus naves. ¡Iniciando batalla!"
                    ));
                }

                // Dar tiempo para que las vistas se inicialicen
                try {
                    Thread.sleep(500); // 500ms de delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Iniciar turno del primer jugador
                String idEnTurno = partida.getIdJugadorEnTurno();
                JugadorDTO jugadorEnTurno = partida.getJugador1().getId().equals(idEnTurno)
                    ? partida.getJugador1() : partida.getJugador2();

                System.out.println("[MANEJADOR] Enviando TURNO_INICIADO a: " + jugadorEnTurno.getNombre() + " (ID: " + idEnTurno + ")");

                // Enviar TURNO_INICIADO solo al jugador que tiene el turno
                if (partida.getJugador1().getId().equals(idEnTurno)) {
                    if (manejador1 != null) {
                        manejador1.enviarMensaje(new MensajeDTO(
                            TipoMensaje.TURNO_INICIADO,
                            "Es tu turno. Realiza un disparo.",
                            jugadorEnTurno
                        ));
                        System.out.println("[MANEJADOR] TURNO_INICIADO enviado a jugador 1");
                    }
                    // Enviar CAMBIO_TURNO al jugador 2
                    if (manejador2 != null) {
                        manejador2.enviarMensaje(new MensajeDTO(
                            TipoMensaje.CAMBIO_TURNO,
                            "Turno de " + jugadorEnTurno.getNombre(),
                            jugadorEnTurno
                        ));
                        System.out.println("[MANEJADOR] CAMBIO_TURNO enviado a jugador 2");
                    }
                } else {
                    if (manejador2 != null) {
                        manejador2.enviarMensaje(new MensajeDTO(
                            TipoMensaje.TURNO_INICIADO,
                            "Es tu turno. Realiza un disparo.",
                            jugadorEnTurno
                        ));
                        System.out.println("[MANEJADOR] TURNO_INICIADO enviado a jugador 2");
                    }
                    // Enviar CAMBIO_TURNO al jugador 1
                    if (manejador1 != null) {
                        manejador1.enviarMensaje(new MensajeDTO(
                            TipoMensaje.CAMBIO_TURNO,
                            "Turno de " + jugadorEnTurno.getNombre(),
                            jugadorEnTurno
                        ));
                        System.out.println("[MANEJADOR] CAMBIO_TURNO enviado a jugador 1");
                    }
                }

                // Enviar tableros iniciales
                enviarTablerosAJugadores(partida);

                // Configurar callback de timeout
                gestorPartidas.establecerRespuestaTiempoAgotado(partida.getIdPartida(), idJugadorTimeout -> {
                    manejarTimeout(partida);
                });

                // Configurar callback de actualización periódica del tiempo
                gestorPartidas.establecerCallbackActualizacionTiempo(partida.getIdPartida(), tiempoRestante -> {
                    enviarActualizacionTiempoAJugadores(partida, tiempoRestante);
                });

                // Iniciar timer del primer turno
                gestorPartidas.iniciarTemporizador(partida.getIdPartida());
            } else {
                // Notificar que está esperando al oponente
                enviarMensaje(new MensajeDTO(
                    TipoMensaje.ESPERANDO_OPONENTE_NAVES,
                    "Esperando a que tu oponente coloque sus naves..."
                ));
            }
        } catch (GestorPartidasException e) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                e.getMessage()
            ));
        }
    }

    /**
     * Procesa un disparo de un jugador
     * @param mensaje Mensaje con las coordenadas del disparo
     */
    private void procesarDisparo(MensajeDTO mensaje) {
        
        if (jugadorAsociado == null) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                "No hay jugador asociado"
            ));
            return;
        }

        try {
            
            // Obtiene el id del jugador asociado
            String idJugador = jugadorAsociado.getId();
            
            // Verifica que se encuentre en una partida activa
            if (!gestorPartidas.verificarJugadorPartidaActiva(idJugador)) {
                enviarMensaje(new MensajeDTO(
                    TipoMensaje.ERROR,
                    "No estás en una partida activa"
                ));
                return;
            }

            // Obtener coordenada del disparo
            CoordenadaDTO coordenada = (CoordenadaDTO) mensaje.getDatos();

            System.out.println("[MANEJADOR] Disparo de " + jugadorAsociado.getNombre() +
                              " en (" + coordenada.getX() + "," + coordenada.getY() + ")");

            // Procesar disparo en la partida
            DisparoDTO disparo = gestorPartidas.procesarDisparo(
                    gestorPartidas.obtenerPartidaDeJugador(idJugador).getIdPartida(), 
                    idJugador, 
                    coordenada
            );
            
            // Verifica que el disparo no sea null
            if (disparo == null) {
                enviarMensaje(new MensajeDTO(
                    TipoMensaje.ERROR,
                    "Coordenada inválida o ya disparada"
                ));
                return;
            }
            
            // Obtiene la partida después de haber procesado el disparo
            PartidaDTO partida = gestorPartidas.obtenerPartidaDeJugador(idJugador);
            
            // Obtener manejadores de ambos jugadores
            ManejadorCliente manejador1 = gestorJugadores.obtenerManejador(partida.getJugador1().getId());
            ManejadorCliente manejador2 = gestorJugadores.obtenerManejador(partida.getJugador2().getId());

            // Enviar resultado del disparo a ambos jugadores
            if (manejador1 != null) {
                manejador1.enviarMensaje(new MensajeDTO(
                    TipoMensaje.RESULTADO_DISPARO,
                    "Resultado del disparo",
                    disparo
                ));
            }

            if (manejador2 != null) {
                manejador2.enviarMensaje(new MensajeDTO(
                    TipoMensaje.RESULTADO_DISPARO,
                    "Resultado del disparo",
                    disparo
                ));
            }

            // Enviar tableros actualizados
            enviarTablerosAJugadores(partida);

            // Verificar si hay ganador
            if (partida.hayGanador()) {
                System.out.println("[MANEJADOR] ¡Partida finalizada! Ganador: " + partida.getGanador().getNombre());

                JugadorDTO ganador = partida.getGanador();
                JugadorDTO perdedor = partida.getJugador1().getId().equals(ganador.getId())
                    ? partida.getJugador2() : partida.getJugador1();

                // --- INICIO: GENERAR ESTADÍSTICAS ---
                //Solucionar Casteo || SI FUNCIONA || pero hay que quitarlo
                IJugador objJugador1 = (IJugador) partida.getJugador1();
                IJugador objJugador2 = (IJugador) partida.getJugador2();

                //Identificar quién es el ganador y quién el perdedor en los objetos Modelo
                IJugador ganadorModel = null;
                IJugador perdedorModel = null;

                if (objJugador1.getId().equals(ganador.getId())) {
                    ganadorModel = objJugador1;
                    perdedorModel = objJugador2;
                } else {
                    ganadorModel = objJugador2;
                    perdedorModel = objJugador1;
                }

                EstadisticaDTO statsGanador = ganadorModel.generarEstadisticas(true, 5);
                EstadisticaDTO statsPerdedor = perdedorModel.generarEstadisticas(false, 0);
                
                List<EstadisticaDTO> reporteFinal = new ArrayList<>();
                reporteFinal.add(statsGanador);
                reporteFinal.add(statsPerdedor);
                
                // Notificar al ganador
                ManejadorCliente manejadorGanador = gestorJugadores.obtenerManejador(ganador.getId());
                if (manejadorGanador != null) {
                    manejadorGanador.enviarMensaje(new MensajeDTO(
                        TipoMensaje.PARTIDA_GANADA,
                        "¡Felicidades! Has ganado la partida",
                        reporteFinal));
                }

                // Notificar al perdedor
                ManejadorCliente manejadorPerdedor = gestorJugadores.obtenerManejador(perdedor.getId());
                if (manejadorPerdedor != null) {
                    manejadorPerdedor.enviarMensaje(new MensajeDTO(
                        TipoMensaje.PARTIDA_PERDIDA,
                        "Has perdido la partida. ¡Mejor suerte la próxima vez!",
                            reporteFinal));
                }

                // Detener timer y limpiar recursos
                gestorPartidas.liberarRecursos(partida.getIdPartida());

                // Marcar jugadores como disponibles
                gestorJugadores.marcarDisponible(partida.getJugador1().getId());
                gestorJugadores.marcarDisponible(partida.getJugador2().getId());

                // Eliminar partida
                gestorPartidas.eliminarPartida(partida.getIdPartida());

            } else {
                // Continuar juego - notificar cambio de turno si cambió
                String idEnTurno = partida.getIdJugadorEnTurno();
                JugadorDTO jugadorEnTurno = partida.getJugador1().getId().equals(idEnTurno)
                    ? partida.getJugador1() : partida.getJugador2();

                // Notificar al jugador en turno
                ManejadorCliente manejadorEnTurno = gestorJugadores.obtenerManejador(idEnTurno);
                if (manejadorEnTurno != null) {
                    manejadorEnTurno.enviarMensaje(new MensajeDTO(
                        TipoMensaje.TURNO_INICIADO,
                        "Es tu turno. Realiza un disparo.",
                        jugadorEnTurno
                    ));
                }

                // Notificar al otro jugador
                String idOtro = partida.getJugador1().getId().equals(idEnTurno)
                    ? partida.getJugador2().getId() : partida.getJugador1().getId();
                ManejadorCliente manejadorOtro = gestorJugadores.obtenerManejador(idOtro);
                if (manejadorOtro != null) {
                    manejadorOtro.enviarMensaje(new MensajeDTO(
                        TipoMensaje.CAMBIO_TURNO,
                        "Turno de " + jugadorEnTurno.getNombre(),
                        jugadorEnTurno
                    ));
                }

                // Reiniciar timer para el nuevo turno
                gestorPartidas.iniciarTemporizador(partida.getIdPartida());
            }
        } catch (GestorPartidasException e) {
            enviarMensaje(new MensajeDTO(
                TipoMensaje.ERROR,
                e.getMessage()
            ));
        }
    }

    /**
     * Maneja el timeout de un turno
     */
    private void manejarTimeout(PartidaDTO partida) {
        // Obtener jugador en turno actual (ya cambió en Partida)
        String idEnTurno = partida.getIdJugadorEnTurno();
        JugadorDTO jugadorEnTurno = partida.getJugador1().getId().equals(idEnTurno)
            ? partida.getJugador1() : partida.getJugador2();

        System.out.println("[MANEJADOR] Timeout - Nuevo turno: " + jugadorEnTurno.getNombre());

        // Obtener manejadores
        ManejadorCliente manejador1 = gestorJugadores.obtenerManejador(partida.getJugador1().getId());
        ManejadorCliente manejador2 = gestorJugadores.obtenerManejador(partida.getJugador2().getId());

        // Notificar timeout a ambos jugadores
        String mensajeTimeout = "Tiempo agotado. Cambio de turno a " + jugadorEnTurno.getNombre();

        if (manejador1 != null) {
            manejador1.enviarMensaje(new MensajeDTO(
                TipoMensaje.TURNO_TIMEOUT,
                mensajeTimeout
            ));
        }

        if (manejador2 != null) {
            manejador2.enviarMensaje(new MensajeDTO(
                TipoMensaje.TURNO_TIMEOUT,
                mensajeTimeout
            ));
        }

        // Notificar al nuevo jugador en turno
        ManejadorCliente manejadorEnTurno = gestorJugadores.obtenerManejador(idEnTurno);
        if (manejadorEnTurno != null) {
            manejadorEnTurno.enviarMensaje(new MensajeDTO(
                TipoMensaje.TURNO_INICIADO,
                "Es tu turno. Realiza un disparo.",
                jugadorEnTurno
            ));
        }

        // Notificar al otro jugador
        String idOtro = partida.getJugador1().getId().equals(idEnTurno)
            ? partida.getJugador2().getId() : partida.getJugador1().getId();
        JugadorDTO otroJugador = partida.getJugador1().getId().equals(idOtro)
            ? partida.getJugador1() : partida.getJugador2();

        ManejadorCliente manejadorOtro = gestorJugadores.obtenerManejador(idOtro);
        if (manejadorOtro != null) {
            manejadorOtro.enviarMensaje(new MensajeDTO(
                TipoMensaje.CAMBIO_TURNO,
                "Turno de " + jugadorEnTurno.getNombre(),
                jugadorEnTurno
            ));
        }

        // Reiniciar timer para el nuevo turno
        gestorPartidas.iniciarTemporizador(partida.getIdPartida());
    }

    /**
     * Envía actualización del tiempo restante a ambos jugadores
     * @param partida La partida actual
     * @param tiempoRestante Tiempo restante en segundos
     */
    private void enviarActualizacionTiempoAJugadores(PartidaDTO partida, int tiempoRestante) {
        ManejadorCliente manejador1 = gestorJugadores.obtenerManejador(partida.getJugador1().getId());
        ManejadorCliente manejador2 = gestorJugadores.obtenerManejador(partida.getJugador2().getId());

        if (manejador1 != null) {
            manejador1.enviarMensaje(new MensajeDTO(
                TipoMensaje.ACTUALIZAR_TIEMPO_TURNO,
                "Tiempo restante: " + tiempoRestante + "s",
                tiempoRestante
            ));
        }

        if (manejador2 != null) {
            manejador2.enviarMensaje(new MensajeDTO(
                TipoMensaje.ACTUALIZAR_TIEMPO_TURNO,
                "Tiempo restante: " + tiempoRestante + "s",
                tiempoRestante
            ));
        }
    }

    /**
     * Envía los tableros actualizados a ambos jugadores
     * @param partida La partida actual
     */
    private void enviarTablerosAJugadores(PartidaDTO partida) {
        System.out.println("[MANEJADOR] ========== ENVIANDO TABLEROS ==========");

        ManejadorCliente manejador1 = gestorJugadores.obtenerManejador(partida.getJugador1().getId());
        ManejadorCliente manejador2 = gestorJugadores.obtenerManejador(partida.getJugador2().getId());

        // Obtener tableros originales
        TableroDTO tablero1Original = partida.getTableroDeJugador(partida.getJugador1().getId());
        TableroDTO tablero2Original = partida.getTableroDeJugador(partida.getJugador2().getId());

        System.out.println("[MANEJADOR] Tablero1 ID: " + tablero1Original.getIdJugador());
        System.out.println("[MANEJADOR] Tablero2 ID: " + tablero2Original.getIdJugador());

        // Contar disparos en cada tablero ANTES de copiar
        int disparosTablero1 = 0;
        int disparosTablero2 = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                EstadoCasilla estado1 = tablero1Original.getCasillas()[i][j];
                EstadoCasilla estado2 = tablero2Original.getCasillas()[i][j];

                if (estado1 != EstadoCasilla.VACIA && estado1 != EstadoCasilla.OCUPADA) {
                    disparosTablero1++;
                    System.out.println("[MANEJADOR] Tablero1[" + i + "," + j + "]: " + estado1);
                }
                if (estado2 != EstadoCasilla.VACIA && estado2 != EstadoCasilla.OCUPADA) {
                    disparosTablero2++;
                    System.out.println("[MANEJADOR] Tablero2[" + i + "," + j + "]: " + estado2);
                }
            }
        }
        System.out.println("[MANEJADOR] Total disparos tablero1: " + disparosTablero1);
        System.out.println("[MANEJADOR] Total disparos tablero2: " + disparosTablero2);

        // CREAR COPIAS PROFUNDAS para enviar
        System.out.println("[MANEJADOR] Creando copias profundas de los tableros...");
        TableroDTO tablero1Copia = tablero1Original.copiarProfundo();
        TableroDTO tablero2Copia = tablero2Original.copiarProfundo();
        System.out.println("[MANEJADOR] Copias creadas exitosamente");

        // Enviar ambos tableros al jugador 1
        if (manejador1 != null) {
            ArrayList<TableroDTO> tableros = new ArrayList<>();
            tableros.add(tablero1Copia);
            tableros.add(tablero2Copia);
            System.out.println("[MANEJADOR] Enviando tableros a jugador 1 (" + partida.getJugador1().getNombre() + ")");
            manejador1.enviarMensaje(new MensajeDTO(
                TipoMensaje.ACTUALIZAR_TABLEROS,
                "Tableros actualizados",
                tableros
            ));
        } else 
            System.err.println("[MANEJADOR] ERROR: manejador1 es null!");

        // Enviar ambos tableros al jugador 2 (crear nuevas copias para evitar problemas de referencia)
        if (manejador2 != null) {
            ArrayList<TableroDTO> tableros = new ArrayList<>();
            tableros.add(tablero1Original.copiarProfundo());
            tableros.add(tablero2Original.copiarProfundo());
            System.out.println("[MANEJADOR] Enviando tableros a jugador 2 (" + partida.getJugador2().getNombre() + ")");
            manejador2.enviarMensaje(new MensajeDTO(
                TipoMensaje.ACTUALIZAR_TABLEROS,
                "Tableros actualizados",
                tableros
            ));
        } else
            System.err.println("[MANEJADOR] ERROR: manejador2 es null!");
        
        System.out.println("[MANEJADOR] ========== TABLEROS ENVIADOS ==========");
    }

    /**
     * Desconecta el cliente y libera recursos
     */
    private void desconectar() {
        try {
            conectado = false;

            if (jugadorAsociado != null) 
                gestorJugadores.eliminarJugador(jugadorAsociado.getId());

            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socket != null && !socket.isClosed()) socket.close();

            System.out.println("[MANEJADOR] Cliente desconectado");

        } catch (IOException e) {
            System.err.println("[MANEJADOR] Error al cerrar conexión: " + e.getMessage());
        }
    }

    /**
     * Obtiene el jugador asociado a este manejador
     * @return JugadorDTO
     */
    public JugadorDTO getJugadorAsociado() {return jugadorAsociado;}
}