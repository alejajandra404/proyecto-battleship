package mx.itson.models;

import mx.itson.exceptions.ModelException;
import mx.itson.utils.enums.EstadoCasilla;
import mx.itson.utils.enums.EstadoPartida;
import mx.itson.utils.enums.ResultadoDisparo;
import mx.itson.utils.dtos.JugadorDTO;

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
public class Partida implements IPartida {
    
    private IJugador jugadorTurno;
    private EstadoPartida estado;
    private final IJugador jugador1;
    private final IJugador jugador2;
    private IJugador ganador;
    private String idPartida;
    
    /**
     * Constructor para iniciar una nueva partida
     * @param jugador1 El primer jugador
     * @param jugador2 El segundo jugador
     */
    public Partida(IJugador jugador1, IJugador jugador2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.estado = EstadoPartida.EN_CURSO;
    }
    
    /**
     * Constructor para finalizar una partida
     * @param jugador1 El primer jugador
     * @param jugador2 El segundo jugador
     * @param ganador El ganador de la partida
     */
    public Partida(IJugador jugador1, IJugador jugador2, IJugador ganador) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.ganador = ganador;
        this.estado = EstadoPartida.EN_CURSO;
        this.idPartida = jugador1.getId() + "-" + jugador2.getId();
    }
    
    public IJugador getJugador1() {return jugador1;}

    public IJugador getJugador2() {return jugador2;}
    
    public IJugador ganador(){
        return ganador;
    }

    public EstadoPartida getEstado() {return estado;}

    public void setEstado(EstadoPartida estado) {this.estado = estado;}
    
    @Override
    public void iniciarPartida() throws ModelException {
        this.jugadorTurno = this.jugador1;
    }

    @Override
    public void cambiarTurno() throws ModelException {
        if(jugador1.equals(jugadorTurno))
            jugadorTurno = jugador2;
        else
            jugadorTurno = jugador1;
    }

    @Override
    public boolean validarDisparo(Disparo disparo, IJugador jugador) throws ModelException {
        return jugador.validarDisparo(disparo);
    }

    @Override
    public boolean recibirDisparo(Coordenada coordenada, IJugador jugadorAtacante) throws ModelException {
        
        IJugador jugadorDefensor = (jugadorAtacante == this.jugador1) ? this.jugador2 : this.jugador1;
        
        EstadoCasilla estadoCasilla = jugadorDefensor.recibirDisparo(coordenada);
        
        // Determinar el resultado del disparo
        ResultadoDisparo resultadoDisparo;
        boolean esImpacto;
        
        // Si el estado de la casilla impactada, es averiada o hundida, se actualiza el resultado del Diparo como IMPACTO
        switch (estadoCasilla) {
            case IMPACTADA_AVERIADA -> {
                resultadoDisparo = ResultadoDisparo.IMPACTO_AVERIADA;
                esImpacto = true;
            }
            case IMPACTADA_HUNDIDA -> {
                resultadoDisparo = ResultadoDisparo.IMPACTO_HUNDIDA;
                esImpacto = true;
                // De lo contrario, se actualiza el resultado como AGUA.
            }
            default -> {
                resultadoDisparo = ResultadoDisparo.AGUA;
                esImpacto = false;
            }
        }
        
        // Se crea una nueva instancia de un Disparo, con su resultado anterior.
        Disparo disparoResultante = new Disparo(coordenada, resultadoDisparo, jugadorAtacante);
        
        // Marcar el disparo en el tablero del atacante
        jugadorAtacante.marcarDisparo(disparoResultante);
        
        // Retornar si fue impacto o agua
        return esImpacto;
    }

    @Override
    public boolean verificarJugadorTurno(IJugador jugador) throws ModelException {
        return this.jugadorTurno == jugador;
    }

    @Override
    public void agregarNave(Nave naveNueva, IJugador jugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void quitarNave(Coordenada[] coordenadas, IJugador jugador) throws ModelException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    public IJugador getJugadorModeloPorId(String idBusqueda) {
        if (this.jugador1.getId().equals(idBusqueda)) {
            return this.jugador1;
        }
        if (this.jugador2.getId().equals(idBusqueda)) {
            return this.jugador2;
        }
        return null;
    }
}