package mx.itson.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor TCP que acepta conexiones de clientes
 * Crea un thread (ManejadorCliente) por cada cliente conectado
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ServidorTCP implements Runnable {

    private static final int PUERTO_TCP = 5000;

    private ServerSocket serverSocket;
    private final GestorJugadores gestorJugadores;
    private final GestorPartidas gestorPartidas;
    private final List<Thread> hilosClientes;
    private boolean ejecutando;

    /**
     * Constructor
     * @param gestorJugadores Gestor de jugadores compartido
     * @param gestorPartidas Gestor de partidas compartido
     */
    public ServidorTCP(GestorJugadores gestorJugadores, GestorPartidas gestorPartidas) {
        this.gestorJugadores = gestorJugadores;
        this.gestorPartidas = gestorPartidas;
        this.hilosClientes = new ArrayList<>();
        this.ejecutando = false;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PUERTO_TCP);
            ejecutando = true;

            System.out.println("[TCP] Servidor TCP iniciado en puerto " + PUERTO_TCP);
            System.out.println("[TCP] Esperando conexiones de clientes...");

            while (ejecutando) {
                // Aceptar conexión de cliente
                Socket socketCliente = serverSocket.accept();

                System.out.println("[TCP] Nueva conexión aceptada: " + socketCliente.getInetAddress());

                // Crear manejador para este cliente
                ManejadorCliente manejador = new ManejadorCliente(socketCliente, gestorJugadores, gestorPartidas);

                // Iniciar thread para este cliente
                Thread hiloCliente = new Thread(manejador);
                hilosClientes.add(hiloCliente);
                hiloCliente.start();

                System.out.println("[TCP] Thread iniciado para cliente. Total threads activos: " + hilosClientes.size());
            }

        } catch (IOException e) {
            if (ejecutando) {
                System.err.println("[TCP] Error al aceptar conexiones: " + e.getMessage());
            }
        } finally {
            detener();
        }
    }

    /**
     * Detiene el servidor TCP y todos los threads de clientes
     */
    public void detener() {
        ejecutando = false;

        // Cerrar server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[TCP] ServerSocket cerrado");
            }
        } catch (IOException e) {
            System.err.println("[TCP] Error al cerrar ServerSocket: " + e.getMessage());
        }

        // Interrumpir threads de clientes
        for (Thread hilo : hilosClientes) {
            if (hilo.isAlive()) {
                hilo.interrupt();
            }
        }

        hilosClientes.clear();
        System.out.println("[TCP] Servidor TCP detenido");
    }

    /**
     * Verifica si el servidor está ejecutando
     * @return true si está ejecutando
     */
    public boolean isEjecutando() {
        return ejecutando;
    }

    /**
     * Obtiene el número de clientes conectados
     * @return Número de threads activos
     */
    public int getClientesConectados() {
        return (int) hilosClientes.stream().filter(Thread::isAlive).count();
    }
}
