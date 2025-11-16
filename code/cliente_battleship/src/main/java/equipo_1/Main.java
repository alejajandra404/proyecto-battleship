package equipo_1;

import controllers.ControlDisparo;
import dtos.*;
import enums.*;
import exceptions.*;
import models.*;
import utils.MapperDTO;
import views.ContenedorBattleship;
import views.FlujoVista;

/**
 * Clase principal para probar el caso de uso Realizar Disparo
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

    /**
     * Método para iniciar partida de prueba (conservado para testing) Este
     * método NO se usa en el flujo normal, pero se conserva para pruebas
     */
    public static void mainPruebas(String[] args) {
        try {
            System.out.println("Batalla Naval - Inicializando");
            System.out.println();

            // PASO 1: Crear el modelo (jugadores y partida)
            System.out.println("Creando jugadores...");
            Jugador jugador1 = crearJugadorConNaves("Jugador 1");
            Jugador jugador2 = crearJugadorConNaves("Jugador 2");

            System.out.println("✓ Jugador 1 creado con 11 naves");
            System.out.println("✓ Jugador 2 creado con 11 naves");

            // PASO 2: Crear partida
            System.out.println("\nCreando partida...");
            Partida partida = new Partida(jugador1, jugador2);
            partida.iniciarPartida();
            System.out.println("✓ Partida iniciada");

            // PASO 3: Crear controlador (capa intermedia con DTOs)
            System.out.println("\nCreando controlador con DTOs...");
            ControlDisparo controlador = new ControlDisparo(partida);
            System.out.println("✓ Controlador creado");

            // PASO 4: Convertir naves del modelo a DTOs para la vista
            System.out.println("\nConvirtiendo naves a DTOs...");
            NaveDTO[] navesDTO1 = convertirNavesADTOs(jugador1);
            NaveDTO[] navesDTO2 = convertirNavesADTOs(jugador2);
            System.out.println("✓ Naves convertidas a DTOs");

            // PASO 5: Crear vista (SOLO conoce DTOs y nombres)
            System.out.println("\nCreando vista...");
            javax.swing.SwingUtilities.invokeLater(() -> {
                // La vista recibe SOLO Strings y el controlador
                // NO recibe objetos del modelo

                // Obtener turno inicial como DTO
                TurnoDTO turnoInicial = controlador.obtenerTurnoActual();;
                // Se crea el contenedor de las vistas.
                ContenedorBattleship contenedor = new ContenedorBattleship();
                // Se muestra la vista de disparos.
                FlujoVista.mostrarConfigDisparos(jugador1.getNombre(), jugador2.getNombre(), controlador, navesDTO1);
                // ESTE MÉTODO TIENE QUE IR DESPUÉS DE AGREGAR LA PRIMERA VISTA SÍ O SÍ
                contenedor.pack();
                contenedor.setLocationRelativeTo(null);
                contenedor.setVisible(true);

                System.out.println("✓ Vista creada y mostrada");
                System.out.println("\nSistema listo");
                System.out.println("Turno inicial: " + turnoInicial.getNombreJugadorTurno());
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

    /**
     * Crea un jugador con naves hardcodeadas Este método trabaja con el MODELO
     */
    private static Jugador crearJugadorConNaves(String nombre)
            throws TableroException, NaveException, CasillaException {

        Jugador jugador = new Jugador(nombre);
        TableroNaves tableroNaves = jugador.getTableroNaves();
        tableroNaves.inicializarCasillas();

        // PORTAAVIONES 1 - Horizontal
        Coordenada[] coordsPorta1 = {
            new Coordenada(0, 0),
            new Coordenada(0, 1),
            new Coordenada(0, 2),
            new Coordenada(0, 3)
        };
        Nave portaaviones1 = new Nave(TipoNave.PORTAAVIONES, OrientacionNave.HORIZONTAL, coordsPorta1);
        tableroNaves.añadirNave(portaaviones1);

        // PORTAAVIONES 2 - Vertical
        Coordenada[] coordsPorta2 = {
            new Coordenada(2, 9),
            new Coordenada(3, 9),
            new Coordenada(4, 9),
            new Coordenada(5, 9)
        };
        Nave portaaviones2 = new Nave(TipoNave.PORTAAVIONES, OrientacionNave.VERICAL, coordsPorta2);
        tableroNaves.añadirNave(portaaviones2);

        // CRUCERO 1 - Horizontal
        Coordenada[] coordsCrucero1 = {
            new Coordenada(2, 1),
            new Coordenada(2, 2),
            new Coordenada(2, 3)
        };
        Nave crucero1 = new Nave(TipoNave.CRUCERO, OrientacionNave.HORIZONTAL, coordsCrucero1);
        tableroNaves.añadirNave(crucero1);

        // CRUCERO 2 - Vertical
        Coordenada[] coordsCrucero2 = {
            new Coordenada(5, 5),
            new Coordenada(6, 5),
            new Coordenada(7, 5)
        };
        Nave crucero2 = new Nave(TipoNave.CRUCERO, OrientacionNave.VERICAL, coordsCrucero2);
        tableroNaves.añadirNave(crucero2);

        // SUBMARINO 1
        Coordenada[] coordsSub1 = {
            new Coordenada(4, 0),
            new Coordenada(4, 1)
        };
        Nave submarino1 = new Nave(TipoNave.SUBMARINO, OrientacionNave.HORIZONTAL, coordsSub1);
        tableroNaves.añadirNave(submarino1);

        // SUBMARINO 2
        Coordenada[] coordsSub2 = {
            new Coordenada(7, 7),
            new Coordenada(8, 7)
        };
        Nave submarino2 = new Nave(TipoNave.SUBMARINO, OrientacionNave.VERICAL, coordsSub2);
        tableroNaves.añadirNave(submarino2);

        // SUBMARINO 3
        Coordenada[] coordsSub3 = {
            new Coordenada(9, 0),
            new Coordenada(9, 1)
        };
        Nave submarino3 = new Nave(TipoNave.SUBMARINO, OrientacionNave.HORIZONTAL, coordsSub3);
        tableroNaves.añadirNave(submarino3);

        // SUBMARINO 4
        Coordenada[] coordsSub4 = {
            new Coordenada(6, 2),
            new Coordenada(6, 3)
        };
        Nave submarino4 = new Nave(TipoNave.SUBMARINO, OrientacionNave.HORIZONTAL, coordsSub4);
        tableroNaves.añadirNave(submarino4);

        // BARCO 1
        Coordenada[] coordsBarco1 = {new Coordenada(4, 4)};
        Nave barco1 = new Nave(TipoNave.BARCO, OrientacionNave.HORIZONTAL, coordsBarco1);
        tableroNaves.añadirNave(barco1);

        // BARCO 2
        Coordenada[] coordsBarco2 = {new Coordenada(8, 2)};
        Nave barco2 = new Nave(TipoNave.BARCO, OrientacionNave.HORIZONTAL, coordsBarco2);
        tableroNaves.añadirNave(barco2);

        // BARCO 3
        Coordenada[] coordsBarco3 = {new Coordenada(9, 9)};
        Nave barco3 = new Nave(TipoNave.BARCO, OrientacionNave.HORIZONTAL, coordsBarco3);
        tableroNaves.añadirNave(barco3);

        return jugador;
    }

    /**
     * Convierte las naves del modelo a DTOs
     *
     * Transforma objetos del modelo a DTOs para que la vista no tenga que
     * conocer el modelo
     */
    private static NaveDTO[] convertirNavesADTOs(Jugador jugador) {
        TableroNaves tableroNaves = jugador.getTableroNaves();
        java.util.Set<Nave> naves = tableroNaves.getNaves();

        NaveDTO[] navesDTO = new NaveDTO[naves.size()];
        int index = 0;

        for (Nave nave : naves) {
            navesDTO[index] = MapperDTO.toDTO(nave);
            index++;
        }

        return navesDTO;
    }

}
