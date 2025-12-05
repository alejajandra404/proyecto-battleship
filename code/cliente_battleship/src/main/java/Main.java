import views.ContenedorBattleship;
import views.FlujoVista;

/**
 * Clase principal del cliente Battleship
 *
 * ARQUITECTURA: BAJO ACOPLAMIENTO - ALTA COHESIÓN
 *
 * Esta implementación demuestra:
 * - Separación clara de responsabilidades
 * - Vista desacoplada del modelo (usa solo DTOs)
 * - Preparación para comunicación en red
 * - Buenas prácticas de arquitectura de software
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("═══════════════════════════════════════════════════");
            System.out.println("       CLIENTE BATTLESHIP - BATALLA NAVAL         ");
            System.out.println("═══════════════════════════════════════════════════");
            System.out.println();

            // INICIAR LA APLICACIÓN CON LA VISTA DE CONFIGURACIÓN
            System.out.println("Iniciando aplicación cliente...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                // Se crea el contenedor de las vistas.
                ContenedorBattleship contenedor = new ContenedorBattleship();

                // Se muestra la vista de configuración del jugador como primera pantalla
                FlujoVista.mostrarConfigurarJugador();

                // ESTE MÉTODO TIENE QUE IR DESPUÉS DE AGREGAR LA PRIMERA VISTA SÍ O SÍ
                contenedor.pack();
                contenedor.setLocationRelativeTo(null);
                contenedor.setVisible(true);

                System.out.println("✓ Vista de configuración mostrada");
                System.out.println("\n🎮 ¡Bienvenido a Battleship!");
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
