package views;

import controllers.ControlDisparo;
import dtos.NaveDTO;
import javax.swing.JPanel;

/**
 * Clase que controla el flujo de vistas del juego.
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
    private static VistaDisparos vistaDisparos;
    private static VistaConfiguracionNaves vistaConfigNaves;
    /**
     * Cambia de vista.
     * @param nuevoPanel Panel a ser mostrado.
     */
    private static void cambiarVista(JPanel nuevoPanel) {
        if (panelContenedor == null) 
            throw new IllegalStateException("El contenedor no ha sido inicializado.");
        
        panelContenedor.removeAll();
        panelContenedor.add(nuevoPanel);
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }
    /**
     * Establece el contenedor de las vistas. Es extremadamente
     * importante que se llame a este método antes de iniciar el
     * flujo de la aplicación.
     * @param contenedor Contenedor de vistas.
     */
    public static void setContenedor(JPanel contenedor) {
        panelContenedor = contenedor;
    }
    /**
     * Muestra la vista de la configuración de las naves.
     * @param nombreJugador Nombre del jugador.
     * @param nombreOponente Nombre del oponente.
     * @param controlador Controlador MVC.
     */
    public static void mostrarConfigNaves(String nombreJugador, String nombreOponente, ControlDisparo controlador){
        if(vistaConfigNaves == null)
            vistaConfigNaves = new VistaConfiguracionNaves(nombreJugador, nombreOponente, controlador);
        cambiarVista(vistaConfigNaves);
    }
    /**
     * Muestra la vista de la configuración de disparos.
     * Si la vista no existía previamente, se crea, y en caso contrario,
     * se resetea con la nueva información.
     * @param nombreJugador Nombre del jugador.
     * @param nombreOponente Nombre del oponente.
     * @param controlador Controlador MVC.
     * @param naves Naves a ser colocadas.
     */
    public static void mostrarConfigDisparos(String nombreJugador, String nombreOponente, ControlDisparo controlador, NaveDTO[] naves){
        if(vistaDisparos == null)
            vistaDisparos = new VistaDisparos(nombreJugador, nombreOponente, controlador, naves);
        else
            vistaDisparos.nuevaPartida(nombreJugador, nombreOponente, naves);
        cambiarVista(vistaDisparos);
    }
    
}