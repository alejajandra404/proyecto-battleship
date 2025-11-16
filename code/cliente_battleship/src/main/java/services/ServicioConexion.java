package services;

import mx.itson.utils.dtos.*;
import java.io.*;
import java.net.*;
import java.awt.Color;
import mx.itson.utils.enums.TipoMensaje;

/**
 * Servicio que maneja la conexión con el servidor
 * Incluye descubrimiento UDP y comunicación TCP
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ServicioConexion {

    private static final int PUERTO_DISCOVERY = 5000;
    private static final int TIMEOUT_DISCOVERY = 3000; // 3 segundos por intento
    private static final int MAX_INTENTOS_DISCOVERY = 3; // Número máximo de intentos
    private static final int DELAY_ENTRE_INTENTOS = 1000; // 1 segundo entre intentos
    private static final String MENSAJE_DISCOVERY = "BATTLESHIP_DISCOVERY";

    private Socket socketTCP;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String ipServidor;
    private int puertoTCP;
    private JugadorDTO jugadorLocal;

    /**
     * Descubre el servidor mediante broadcast UDP con reintentos automáticos
     * @return true si se encontró el servidor
     * @throws IOException si hay error en la comunicación
     */
    public boolean descubrirServidor() throws IOException {
        System.out.println("[CLIENTE] Iniciando descubrimiento de servidor...");

        for (int intento = 1; intento <= MAX_INTENTOS_DISCOVERY; intento++) {
            System.out.println("[CLIENTE] Intento " + intento + " de " + MAX_INTENTOS_DISCOVERY);

            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);
                socket.setSoTimeout(TIMEOUT_DISCOVERY);

                // Enviar broadcast
                byte[] buffer = MENSAJE_DISCOVERY.getBytes();
                InetAddress addressBroadcast = InetAddress.getByName("255.255.255.255");
                DatagramPacket paqueteEnvio = new DatagramPacket(
                    buffer,
                    buffer.length,
                    addressBroadcast,
                    PUERTO_DISCOVERY
                );

                socket.send(paqueteEnvio);
                System.out.println("[CLIENTE] Broadcast enviado");

                // Esperar respuesta
                byte[] bufferRespuesta = new byte[1024];
                DatagramPacket paqueteRespuesta = new DatagramPacket(bufferRespuesta, bufferRespuesta.length);

                socket.receive(paqueteRespuesta);

                String respuesta = new String(paqueteRespuesta.getData(), 0, paqueteRespuesta.getLength()).trim();
                System.out.println("[CLIENTE] Respuesta recibida: " + respuesta);

                // Parsear respuesta: "BATTLESHIP_SERVER:<IP>:<PUERTO>"
                if (respuesta.startsWith("BATTLESHIP_SERVER:")) {
                    String[] partes = respuesta.split(":");
                    if (partes.length == 3) {
                        ipServidor = partes[1];
                        puertoTCP = Integer.parseInt(partes[2]);
                        System.out.println("[CLIENTE] Servidor encontrado en " + ipServidor + ":" + puertoTCP);
                        return true;
                    }
                }

            } catch (SocketTimeoutException e) {
                System.err.println("[CLIENTE] Timeout en intento " + intento);

                // Si no es el último intento, esperar antes de reintentar
                if (intento < MAX_INTENTOS_DISCOVERY) {
                    try {
                        System.out.println("[CLIENTE] Reintentando en " + DELAY_ENTRE_INTENTOS + "ms...");
                        Thread.sleep(DELAY_ENTRE_INTENTOS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        System.err.println("[CLIENTE] Búsqueda interrumpida");
                        return false;
                    }
                }
            }
        }

        System.err.println("[CLIENTE] No se encontró servidor después de " + MAX_INTENTOS_DISCOVERY + " intentos");
        return false;
    }

    /**
     * Conecta al servidor TCP
     * @return true si la conexión fue exitosa
     */
    public boolean conectarServidor() {
        if (ipServidor == null) {
            System.err.println("[CLIENTE] Error: Primero debe descubrir el servidor");
            return false;
        }

        try {
            System.out.println("[CLIENTE] Conectando a " + ipServidor + ":" + puertoTCP);
            socketTCP = new Socket(ipServidor, puertoTCP);

            // Configurar streams
            salida = new ObjectOutputStream(socketTCP.getOutputStream());
            salida.flush();
            entrada = new ObjectInputStream(socketTCP.getInputStream());

            System.out.println("[CLIENTE] Conexión TCP establecida");
            return true;

        } catch (IOException e) {
            System.err.println("[CLIENTE] Error al conectar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra un jugador en el servidor
     * @param nombre Nombre del jugador
     * @param color Color del jugador
     * @return MensajeDTO con la respuesta del servidor
     * @throws IOException si hay error en la comunicación
     */
    public MensajeDTO registrarJugador(String nombre, Color color) throws IOException, ClassNotFoundException {
        // Crear DTO de registro
        RegistroJugadorDTO registro = new RegistroJugadorDTO(nombre, color);

        // Crear mensaje
        MensajeDTO mensaje = new MensajeDTO(
            TipoMensaje.REGISTRO_JUGADOR,
            "Registro de jugador",
            registro
        );

        // Enviar mensaje
        System.out.println("[CLIENTE] Enviando registro: " + nombre);
        salida.writeObject(mensaje);
        salida.flush();

        // Esperar respuesta
        MensajeDTO respuesta = (MensajeDTO) entrada.readObject();
        System.out.println("[CLIENTE] Respuesta recibida: " + respuesta.getTipo());

        // Si el registro fue exitoso, guardar el jugador
        if (respuesta.getTipo() == TipoMensaje.REGISTRO_EXITOSO) {
            jugadorLocal = (JugadorDTO) respuesta.getDatos();
            System.out.println("[CLIENTE] Jugador registrado con ID: " + jugadorLocal.getId());
        }

        return respuesta;
    }

    /**
     * Solicita la lista de jugadores disponibles
     * La respuesta será manejada por el ListenerServidor de forma asíncrona
     * @throws IOException si hay error en la comunicación
     */
    public void solicitarJugadores() throws IOException {
        MensajeDTO mensaje = new MensajeDTO(
            TipoMensaje.SOLICITAR_JUGADORES,
            "Solicitud de jugadores disponibles"
        );

        salida.writeObject(mensaje);
        salida.flush();
    }

    /**
     * Desconecta del servidor
     */
    public void desconectar() {
        try {
            if (salida != null && socketTCP != null && !socketTCP.isClosed()) {
                // Enviar mensaje de desconexión
                MensajeDTO mensaje = new MensajeDTO(
                    TipoMensaje.DESCONEXION,
                    "Desconexión del cliente"
                );
                salida.writeObject(mensaje);
                salida.flush();
            }

            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socketTCP != null) socketTCP.close();

            System.out.println("[CLIENTE] Desconectado del servidor");

        } catch (IOException e) {
            System.err.println("[CLIENTE] Error al desconectar: " + e.getMessage());
        }
    }

    /**
     * Verifica si está conectado al servidor
     * @return true si está conectado
     */
    public boolean isConectado() {
        return socketTCP != null && !socketTCP.isClosed() && socketTCP.isConnected();
    }

    /**
     * Obtiene el jugador local registrado
     * @return JugadorDTO
     */
    public JugadorDTO getJugadorLocal() {
        return jugadorLocal;
    }

    /**
     * Obtiene el ObjectInputStream para recibir mensajes
     * @return ObjectInputStream
     */
    public ObjectInputStream getEntrada() {
        return entrada;
    }

    /**
     * Envía un mensaje genérico al servidor
     * @param mensaje Mensaje a enviar
     * @throws IOException si hay error al enviar
     */
    public void enviarMensaje(MensajeDTO mensaje) throws IOException {
        if (salida != null) {
            salida.writeObject(mensaje);
            salida.flush();
        }
    }
}
