package mx.itson.servidor;

import java.io.IOException;
import java.net.*;

/**
 * Servidor UDP para responder a broadcasts de descubrimiento de clientes
 * Corre en un thread separado
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ServidorDescubrimiento implements Runnable {

    private static final int PUERTO_DISCOVERY = 5000;
    private static final String MENSAJE_DISCOVERY = "BATTLESHIP_DISCOVERY";
    private static final String RESPUESTA_DISCOVERY = "BATTLESHIP_SERVER";

    private DatagramSocket socket;
    private boolean ejecutando;

    /**
     * Constructor
     */
    public ServidorDescubrimiento() {
        this.ejecutando = false;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(PUERTO_DISCOVERY);
            socket.setBroadcast(true);
            ejecutando = true;

            System.out.println("[DISCOVERY] Servidor de descubrimiento iniciado en puerto " + PUERTO_DISCOVERY);
            System.out.println("[DISCOVERY] Esperando broadcasts de clientes...");

            byte[] buffer = new byte[1024];

            while (ejecutando) {
                // Esperar mensaje de descubrimiento
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);

                String mensajeRecibido = new String(paquete.getData(), 0, paquete.getLength()).trim();

                System.out.println("[DISCOVERY] Broadcast recibido de: " + paquete.getAddress() +
                                   " - Mensaje: " + mensajeRecibido);

                // Verificar si es un mensaje de descubrimiento válido
                if (MENSAJE_DISCOVERY.equals(mensajeRecibido)) {
                    enviarRespuesta(paquete.getAddress(), paquete.getPort());
                }
            }

        } catch (SocketException e) {
            if (ejecutando) {
                System.err.println("[DISCOVERY] Error al crear socket: " + e.getMessage());
            }
        } catch (IOException e) {
            if (ejecutando) {
                System.err.println("[DISCOVERY] Error de I/O: " + e.getMessage());
            }
        } finally {
            detener();
        }
    }

    /**
     * Envía respuesta al cliente que envió el broadcast
     * @param direccionCliente Dirección del cliente
     * @param puertoCliente Puerto del cliente
     */
    private void enviarRespuesta(InetAddress direccionCliente, int puertoCliente) {
        try {
            // Obtener la dirección IP local del servidor
            String ipServidor = InetAddress.getLocalHost().getHostAddress();

            // Construir respuesta: "BATTLESHIP_SERVER:<IP>:<PUERTO_TCP>"
            String respuesta = RESPUESTA_DISCOVERY + ":" + ipServidor + ":5000";

            byte[] datos = respuesta.getBytes();
            DatagramPacket paqueteRespuesta = new DatagramPacket(
                    datos,
                    datos.length,
                    direccionCliente,
                    puertoCliente
            );

            socket.send(paqueteRespuesta);

            System.out.println("[DISCOVERY] Respuesta enviada a " + direccionCliente +
                               " - Respuesta: " + respuesta);

        } catch (IOException e) {
            System.err.println("[DISCOVERY] Error al enviar respuesta: " + e.getMessage());
        }
    }

    /**
     * Detiene el servidor de descubrimiento
     */
    public void detener() {
        ejecutando = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
            System.out.println("[DISCOVERY] Servidor de descubrimiento detenido");
        }
    }

    /**
     * Verifica si el servidor está ejecutando
     * @return true si está ejecutando
     */
    public boolean isEjecutando() {
        return ejecutando;
    }
}
