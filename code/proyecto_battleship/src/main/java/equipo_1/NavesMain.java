package equipo_1;

import controllers.ControlDisparo;
import dtos.TurnoDTO;
import models.Jugador;
import models.Partida;
import views.ContenedorBattleship;
import views.FlujoVista;

/**
 *
 * @author PC WHITE WOLF
 */
public class NavesMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Batalla Naval - Inicializando");
            System.out.println();

            // PASO 1: Crear el modelo (jugadores y partida)
            System.out.println("Creando jugadores...");
            Jugador jugador1 = new Jugador("Leonardo");
            Jugador jugador2 = new Jugador("Messi");

            // PASO 2: Crear partida
            System.out.println("\nCreando partida...");
            Partida partida = new Partida(jugador1, jugador2);
//            partida.iniciarPartida();
            System.out.println("✓ Partida iniciada");

            // PASO 3: Crear controlador (capa intermedia con DTOs)
            System.out.println("\nCreando controlador con DTOs...");
            ControlDisparo controlador = new ControlDisparo(partida);
            System.out.println("✓ Controlador creado");

            // PASO 5: Crear vista (SOLO conoce DTOs y nombres)
            System.out.println("\nCreando vista...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                // La vista recibe SOLO Strings y el controlador
                // NO recibe objetos del modelo
                
                // Se crea el contenedor de las vistas.
                ContenedorBattleship contenedor = new ContenedorBattleship();
                // Se muestra la vista de disparos.
                FlujoVista.mostrarConfigNaves(jugador1.getNombre(), jugador2.getNombre(), controlador);
                // ESTE MÉTODO TIENE QUE IR DESPUÉS DE AGREGAR LA PRIMERA VISTA SÍ O SÍ
                contenedor.pack();
                contenedor.setLocationRelativeTo(null);
                contenedor.setVisible(true);

                System.out.println("✓ Vista creada y mostrada");
                System.out.println("\nSistema listo");
                System.out.println("\n🎮 ¡A jugar!");
            });

        } catch (Exception e) {
            System.err.println("❌ Error al iniciar el juego:");
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Error al iniciar el juego: " + e.getMessage(),
                    "Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
}