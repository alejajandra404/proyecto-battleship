package controllers;

import mx.itson.utils.dtos.*;
import services.ServicioConexion;
import java.awt.Color;
import javax.swing.SwingWorker;

/**
 * Controlador MVC para la configuración del jugador
 * Coordina la comunicación entre la vista y el servicio de conexión
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class ControladorConfigurarJugador {

    private final ServicioConexion servicioConexion;
    private IVistaConfigurarJugador vista;
    private JugadorDTO jugadorRegistrado;

    /**
     * Interfaz que debe implementar la vista
     */
    public interface IVistaConfigurarJugador {
        void mostrarMensaje(String mensaje);
        void mostrarError(String error);
        void habilitarFormulario(boolean habilitar);
        void mostrarProgreso(String mensaje);
        void registroExitoso(String nombreJugador, String idJugador);
        void nombreDuplicado(String mensaje);
    }

    /**
     * Constructor
     */
    public ControladorConfigurarJugador() {
        this.servicioConexion = new ServicioConexion();
    }

    /**
     * Establece la vista asociada a este controlador
     * @param vista Vista que implementa IVistaConfigurarJugador
     */
    public void setVista(IVistaConfigurarJugador vista) {
        this.vista = vista;
    }

    /**
     * Inicia el proceso de conexión y registro
     * Este método se ejecuta en un thread separado para no bloquear la UI
     * @param nombre Nombre del jugador
     * @param color Color seleccionado
     */
    public void iniciarConexionYRegistro(String nombre, Color color) {
        // Validar entrada
        if (nombre == null || nombre.trim().isEmpty()) {
            vista.mostrarError("El nombre no puede estar vacío");
            return;
        }

        if (color == null) {
            vista.mostrarError("Debe seleccionar un color");
            return;
        }

        // Deshabilitar formulario mientras se procesa
        vista.habilitarFormulario(false);

        // Ejecutar en background thread
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // PASO 1: Descubrir servidor
                    publish("Buscando servidor...");
                    boolean servidorEncontrado = servicioConexion.descubrirServidor();

                    if (!servidorEncontrado) {
                        throw new Exception("No se pudo encontrar el servidor. Asegúrese de que esté en ejecución.");
                    }

                    // PASO 2: Conectar TCP
                    publish("Conectando al servidor...");
                    boolean conectado = servicioConexion.conectarServidor();

                    if (!conectado) {
                        throw new Exception("No se pudo establecer conexión con el servidor");
                    }

                    // PASO 3: Registrar jugador
                    publish("Registrando jugador...");
                    MensajeDTO respuesta = servicioConexion.registrarJugador(nombre, color);

                    // PASO 4: Procesar respuesta
                    procesarRespuestaRegistro(respuesta);

                } catch (Exception e) {
                    // Propagar excepción para manejarla en done()
                    throw new RuntimeException(e.getMessage(), e);
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                // Actualizar UI con mensajes de progreso
                for (String mensaje : chunks) {
                    vista.mostrarProgreso(mensaje);
                }
            }

            @Override
            protected void done() {
                try {
                    get(); // Esto lanzará la excepción si hubo algún error
                } catch (Exception e) {
                    vista.mostrarError(e.getMessage());
                    vista.habilitarFormulario(true);
                }
            }
        };

        worker.execute();
    }

    /**
     * Procesa la respuesta del servidor al registro
     * @param respuesta MensajeDTO con la respuesta
     */
    private void procesarRespuestaRegistro(MensajeDTO respuesta) {
        switch (respuesta.getTipo()) {
            case REGISTRO_EXITOSO:
                JugadorDTO jugador = (JugadorDTO) respuesta.getDatos();
                this.jugadorRegistrado = jugador; // Guardar en el controlador
                System.out.println("[CONTROLADOR] Registro exitoso: " + jugador.getNombre());
                vista.registroExitoso(jugador.getNombre(), jugador.getId());
                break;

            case NOMBRE_DUPLICADO:
                String mensaje = (String) respuesta.getDatos();
                System.out.println("[CONTROLADOR] Nombre duplicado: " + mensaje);
                vista.nombreDuplicado(mensaje);
                vista.habilitarFormulario(true);
                break;

            case ERROR:
                String error = (String) respuesta.getDatos();
                vista.mostrarError(error);
                vista.habilitarFormulario(true);
                break;

            default:
                vista.mostrarError("Respuesta inesperada del servidor: " + respuesta.getTipo());
                vista.habilitarFormulario(true);
        }
    }

    /**
     * Obtiene el servicio de conexión
     * @return ServicioConexion
     */
    public ServicioConexion getServicioConexion() {
        return servicioConexion;
    }

    /**
     * Obtiene el jugador registrado
     * @return JugadorDTO del jugador registrado
     */
    public JugadorDTO getJugadorRegistrado() {
        return jugadorRegistrado;
    }

    /**
     * Limpia recursos al cerrar
     */
    public void limpiar() {
        if (servicioConexion != null) {
            servicioConexion.desconectar();
        }
    }
}
