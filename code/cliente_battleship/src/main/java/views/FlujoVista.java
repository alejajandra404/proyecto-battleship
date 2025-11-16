package views;

import controllers.ControlDisparo;
import controllers.ControladorJuego;
import controllers.ControladorListaJugadores;
import dtos.NaveDTO;
import javax.swing.JPanel;
import mx.itson.utils.dtos.JugadorDTO;
import services.ServicioConexion;

/**
 * Clase que controla el flujo de vistas del juego.
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class FlujoVista {

    // Panel contenedor
    private static JPanel panelContenedor;
    // Vistas.
    private static VistaConfigurarJugador vistaConfigurarJugador;
    private static VistaListaJugadores vistaListaJugadores;
    private static VistaDisparos vistaDisparos;
    private static VistaConfiguracionNaves vistaConfigNaves;
    private static VistaColocacionNavesVisual vistaColocacionNaves;
    private static VistaJuegoMultiplayer vistaJuego;

    /**
     * Cambia de vista.
     *
     * @param nuevoPanel Panel a ser mostrado.
     */
    private static void cambiarVista(JPanel nuevoPanel) {
        if (panelContenedor == null) {
            throw new IllegalStateException("El contenedor no ha sido inicializado.");
        }

        panelContenedor.removeAll();
        panelContenedor.add(nuevoPanel);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    /**
     * Establece el contenedor de las vistas. Es extremadamente importante que
     * se llame a este método antes de iniciar el flujo de la aplicación.
     *
     * @param contenedor Contenedor de vistas.
     */
    public static void setContenedor(JPanel contenedor) {
        panelContenedor = contenedor;
    }

    /**
     * Muestra la vista de configuración del jugador.
     */
    public static void mostrarConfigurarJugador() {
        if (vistaConfigurarJugador == null) {
            vistaConfigurarJugador = new VistaConfigurarJugador();
        }
        cambiarVista(vistaConfigurarJugador);
    }

    /**
     * Muestra la vista de lista de jugadores disponibles.
     *
     * @param servicioConexion Servicio de conexión activo
     * @param jugadorLocal Jugador local registrado
     */
    public static void mostrarListaJugadores(ServicioConexion servicioConexion, JugadorDTO jugadorLocal) {
        // Limpiar vista anterior si existe
        if (vistaListaJugadores != null) {
            vistaListaJugadores.limpiar();
        }

        // Crear controlador y vista
        ControladorListaJugadores controlador = new ControladorListaJugadores(servicioConexion, jugadorLocal);
        vistaListaJugadores = new VistaListaJugadores(controlador);
        cambiarVista(vistaListaJugadores);
    }

    /**
     * Muestra la vista de la configuración de las naves.
     *
     * @param nombreJugador Nombre del jugador.
     * @param nombreOponente Nombre del oponente.
     * @param controlador Controlador MVC.
     */
    public static void mostrarConfigNaves(String nombreJugador, String nombreOponente, ControlDisparo controlador) {
        if (vistaConfigNaves == null) {
            vistaConfigNaves = new VistaConfiguracionNaves(nombreJugador, nombreOponente, controlador);
        }
        cambiarVista(vistaConfigNaves);
    }

    /**
     * Muestra la vista de la configuración de disparos. Si la vista no existía
     * previamente, se crea, y en caso contrario, se resetea con la nueva
     * información.
     *
     * @param nombreJugador Nombre del jugador.
     * @param nombreOponente Nombre del oponente.
     * @param controlador Controlador MVC.
     * @param naves Naves a ser colocadas.
     */
    public static void mostrarConfigDisparos(String nombreJugador, String nombreOponente, ControlDisparo controlador, NaveDTO[] naves) {
        if (vistaDisparos == null) {
            vistaDisparos = new VistaDisparos(nombreJugador, nombreOponente, controlador, naves);
        } else {
            vistaDisparos.nuevaPartida(nombreJugador, nombreOponente, naves);
        }
        cambiarVista(vistaDisparos);
    }

    /**
     * Muestra la vista de colocación de naves para juego multijugador
     *
     * @param servicioConexion Servicio de conexión
     * @param jugadorLocal Jugador local
     * @param oponente Oponente
     * @param controladorJuego Controlador del juego
     */
    public static void mostrarColocacionNaves(ServicioConexion servicioConexion,
            JugadorDTO jugadorLocal,
            JugadorDTO oponente,
            ControladorJuego controladorJuego) {
        vistaColocacionNaves = new VistaColocacionNavesVisual(
                jugadorLocal,
                oponente,
                controladorJuego
        );
        cambiarVista(vistaColocacionNaves);
    }

    /**
     * Muestra la vista del juego multijugador
     *
     * @param jugadorLocal Jugador local
     * @param oponente Oponente
     * @param controladorJuego Controlador del juego
     */
    public static void mostrarJuegoMultiplayer(JugadorDTO jugadorLocal,
            JugadorDTO oponente,
            ControladorJuego controladorJuego) {
        vistaJuego = new VistaJuegoMultiplayer(
                jugadorLocal,
                oponente,
                controladorJuego
        );
        cambiarVista(vistaJuego);
    }

}
