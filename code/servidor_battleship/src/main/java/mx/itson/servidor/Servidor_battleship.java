package mx.itson.servidor;

import java.util.Scanner;

/**
 * Clase principal del Servidor Battleship Inicia los servicios de
 * descubrimiento UDP y conexiones TCP
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class Servidor_battleship {

    private static ServidorDescubrimiento servidorDiscovery;
    private static ServidorTCP servidorTCP;
    private static GestorJugadores gestorJugadores;
    private static GestorPartidas gestorPartidas;

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("       SERVIDOR BATTLESHIP - BATALLA NAVAL         ");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();

        try {
            iniciarServidor();
            mostrarMenu();
        } catch (Exception e) {
            System.err.println("Error fatal al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
            detenerServidor();
        }
    }

    /**
     * Inicia todos los servicios del servidor
     */
    private static void iniciarServidor() {
        System.out.println("[SERVIDOR] Iniciando servicios...");

        // 1. Crear gestor de jugadores
        gestorJugadores = new GestorJugadores();
        System.out.println("[SERVIDOR] ✓ Gestor de jugadores creado");

        // 2. Crear gestor de partidas
        gestorPartidas = new GestorPartidas();
        System.out.println("[SERVIDOR] ✓ Gestor de partidas creado");

        // 3. Iniciar servidor UDP de descubrimiento
        servidorDiscovery = new ServidorDescubrimiento();
        Thread hiloDiscovery = new Thread(servidorDiscovery, "Thread-Discovery");
        hiloDiscovery.start();
        System.out.println("[SERVIDOR] ✓ Servidor de descubrimiento UDP iniciado");

        // 4. Iniciar servidor TCP para conexiones
        servidorTCP = new ServidorTCP(gestorJugadores, gestorPartidas);
        Thread hiloTCP = new Thread(servidorTCP, "Thread-TCP");
        hiloTCP.start();
        System.out.println("[SERVIDOR] ✓ Servidor TCP iniciado");

        // Esperar un momento para que los servidores se inicien
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println();
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("    SERVIDOR BATTLESHIP EJECUTÁNDOSE          ");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("   Puerto UDP (Discovery): 5000");
        System.out.println("   Puerto TCP (Juego):     5000");
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println();
    }

    /**
     * Muestra el menú de administración del servidor
     */
    private static void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n--- MENÚ SERVIDOR ---");
            System.out.println("1. Ver estadísticas");
            System.out.println("2. Ver jugadores conectados");
            System.out.println("3. Detener servidor");
            System.out.print("Opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        mostrarEstadisticas();
                        break;
                    case 2:
                        mostrarJugadores();
                        break;
                    case 3:
                        System.out.println("\n¿Está seguro de detener el servidor? (s/n): ");
                        String confirmacion = scanner.nextLine();
                        if (confirmacion.equalsIgnoreCase("s")) {
                            ejecutando = false;
                        }
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
            }
        }

        detenerServidor();
        scanner.close();
    }

    /**
     * Muestra estadísticas del servidor
     */
    private static void mostrarEstadisticas() {
        System.out.println("\n═══ ESTADÍSTICAS DEL SERVIDOR ═══");
        System.out.println("Jugadores conectados: " + gestorJugadores.cantidadJugadores());
        System.out.println("Jugadores disponibles: " + gestorJugadores.obtenerJugadoresDisponibles().size());
        System.out.println("Clientes TCP activos: " + servidorTCP.getClientesConectados());
        System.out.println("═════════════════════════════════");
    }

    /**
     * Muestra la lista de jugadores conectados
     */
    private static void mostrarJugadores() {
        System.out.println("\n═══ JUGADORES CONECTADOS ═══");

        var jugadores = gestorJugadores.obtenerJugadoresDisponibles();

        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores conectados");
        } else {
            int i = 1;
            for (var jugador : jugadores) {
                System.out.printf("%d. %s (ID: %s) - %s\n",
                        i++,
                        jugador.getNombre(),
                        jugador.getId().substring(0, 8) + "...",
                        jugador.isEnPartida() ? "En partida" : "Disponible"
                );
            }
        }
        System.out.println("════════════════════════════");
    }

    /**
     * Detiene todos los servicios del servidor
     */
    private static void detenerServidor() {
        System.out.println("\n[SERVIDOR] Deteniendo servicios...");

        if (servidorDiscovery != null) {
            servidorDiscovery.detener();
        }

        if (servidorTCP != null) {
            servidorTCP.detener();
        }

        System.out.println("[SERVIDOR] ✓ Servicios detenidos");
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("     SERVIDOR BATTLESHIP FINALIZADO                ");
        System.out.println("═══════════════════════════════════════════════════");
    }
}
