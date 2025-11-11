package mx.itson.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import mx.itson.models.IJugador;
import mx.itson.models.ITableroDisparos;
import mx.itson.models.ITableroNaves;
import mx.itson.models.JugadorServidor;

/**
 *
 * @author Leonardo Flores Leyva
 * ID: 00000252390
 * @author Yuri Germán García López
 * ID: 00000252583
 * @author Alejandra García Preciado
 * ID: 00000252444
 * @author Jesús Ernesto López Ibarra
 * ID: 00000252663
 * @author Daniel Miramontes Iribe
 * ID: 00000252801
 */
public class Servidor_battleship {

    private static final int PUERTO = 3060;
    
    public static void main(String[] args) {
        
        JugadorServidor jugador;
        
        System.out.println("SERVIDOR: Iniciado. Esperando jugadores...");
        
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            
            // 1. Espera al primer cliente
            Socket clienteSocket1 = serverSocket.accept();
            System.out.println("SERVIDOR: Jugador 1 conectado: " + clienteSocket1.getInetAddress());
            
            // 2. Espera al segundo cliente
            Socket clienteSocket2 = serverSocket.accept();
            System.out.println("SERVIDOR: Jugador 2 conectado: " + clienteSocket2.getInetAddress());
            
            // 3. Crea las entidades del Modelo (Esto se cambiará para tomarlos desde la vista)
            String nombre1 = "Jugador1";
            String nombre2 = "Jugador2";

            // 4. Crea la Partida
            //IJugador jugador1 = new JugadorServidor(nombre1, tableroNaves1, tableroDisparos1);
            //IJugador jugador2 = new JugadorServidor(nombre2, tableroNaves2, tableroDisparos2);

        } catch (IOException e) {
            System.err.println("SERVIDOR: Error al iniciar. " + e.getMessage());
        }
    }
}