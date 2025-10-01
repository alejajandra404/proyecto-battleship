package controllers;
import exceptions.CasillaException;
import exceptions.TableroException;
import models.Coordenada;
import models.Jugador;
import models.Partida;
/**
 * ControlDispar.java
 *
 * Clase que representa el controlador del caso de uso
 * principal
 * puede realizar un jugador durante la partida.
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
 *
 */
public class ControlDisparo {
   
    private Partida partidaActual;
    
    public ControlDisparo(Partida partida){
        this.partidaActual = partida;
    }
    
    public String procesarDisparo(Coordenada coordenada, Jugador jugador) 
            throws TableroException, CasillaException{
        
        // Primero verificar la expiración del turno (Flujo Alternativo - Tiempo Agotado)
//        if(verificarExpiracion()){
//            notificarResultado("Tiempo agotado");
//            actualizarTurno();
//            partidaActual.reiniciarTemporizador();
//            partidaActual.cambiarTurno();
//            return "Tiempo agotado";
//        }
        
        partidaActual.pausarTemporizador();
        
        if(!partidaActual.verificarJugadorTurno(jugador)){
            return "No es tu turno de atacar";
        }
        
        if(!partidaActual.validarDisparo(coordenada, jugador)){
            partidaActual.reanudarTemporizador();
            return "Coordenada invalida";
        }
        
        boolean impacto = partidaActual.recibirDisparo(coordenada, jugador);
 
        partidaActual.reiniciarTemporizador();
        
        if(impacto){
            return "Impacto";
        } else {
            return "Agua";
        }
    }
    
    public void actualizarTurno(){
        partidaActual.cambiarTurno();
    }
    
    public void notificarResultado(String mensaje){
        System.out.println(mensaje);
    }
}