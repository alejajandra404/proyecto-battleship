package services;

import mx.itson.utils.dtos.MensajeDTO;
import java.io.ObjectInputStream;

/**
 * Thread que escucha mensajes del servidor en segundo plano
 * Permite recibir notificaciones asíncronas del servidor
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ListenerServidor extends Thread {

    private static int contadorInstancias = 0;
    private final int idInstancia;
    private final ObjectInputStream entrada;
    private final ICallbackMensaje callback;
    private volatile boolean ejecutando;

    /**
     * Interfaz para callback de mensajes recibidos
     */
    public interface ICallbackMensaje {
        void onMensajeRecibido(MensajeDTO mensaje);
        void onError(Exception e);
        void onDesconexion();
    }

    /**
     * Constructor
     * @param entrada Stream de entrada del servidor
     * @param callback Callback para procesar mensajes
     */
    public ListenerServidor(ObjectInputStream entrada, ICallbackMensaje callback) {
        super("ListenerServidor");
        this.idInstancia = ++contadorInstancias;
        this.entrada = entrada;
        this.callback = callback;
        this.ejecutando = true;
        setDaemon(true); // Thread daemon para que no impida el cierre de la app
        System.out.println("[LISTENER #" + idInstancia + "] CREADO - Total de instancias creadas: " + contadorInstancias);
    }

    @Override
    public void run() {
        System.out.println("[LISTENER #" + idInstancia + "] INICIANDO escucha de mensajes del servidor...");

        try {
            while (ejecutando) {
                // Leer mensaje del servidor
                System.out.println("[LISTENER #" + idInstancia + "] Esperando mensaje...");
                Object objetoRecibido = entrada.readObject();

                System.out.println("[LISTENER #" + idInstancia + "] Objeto recibido - Tipo: " +
                    (objetoRecibido != null ? objetoRecibido.getClass().getName() : "null"));

                if (!(objetoRecibido instanceof MensajeDTO)) {
                    System.err.println("[LISTENER #" + idInstancia + "] ERROR: Se esperaba MensajeDTO pero se recibió: " +
                        objetoRecibido.getClass().getName());
                    System.err.println("[LISTENER #" + idInstancia + "] Contenido: " + objetoRecibido);
                    throw new ClassCastException("Se esperaba MensajeDTO pero se recibió " +
                        objetoRecibido.getClass().getName());
                }

                MensajeDTO mensaje = (MensajeDTO) objetoRecibido;

                System.out.println("[LISTENER #" + idInstancia + "] Mensaje recibido: " + mensaje.getTipo());

                // Notificar al callback
                if (callback != null) {
                    callback.onMensajeRecibido(mensaje);
                }
            }
        } catch (Exception e) {
            if (ejecutando) {
                System.err.println("[LISTENER #" + idInstancia + "] Error al recibir mensaje: " + e.getMessage());
                e.printStackTrace();
                if (callback != null) {
                    callback.onError(e);
                }
            }
        } finally {
            System.out.println("[LISTENER #" + idInstancia + "] DETENIDO - Finalizando escucha");
            if (callback != null) {
                callback.onDesconexion();
            }
        }
    }

    /**
     * Detiene el listener
     */
    public void detener() {
        System.out.println("[LISTENER #" + idInstancia + "] DETENIENDO listener...");
        ejecutando = false;
        interrupt();
    }

    /**
     * Verifica si está ejecutando
     * @return true si está ejecutando
     */
    public boolean isEjecutando() {
        return ejecutando;
    }
}
