package mx.itson.mappers;

import mx.itson.models.IJugador;
import mx.itson.utils.dtos.JugadorDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public class JugadorMapper {
    
    public static JugadorDTO toDTO(IJugador jugador){
        JugadorDTO jugadorDTO = new JugadorDTO();
        jugadorDTO.setId(jugador.getId());
        jugadorDTO.setNombre(jugador.getNombre());
        // HARDCODEADO POR MIENTRAS
        jugadorDTO.setColor(jugador.getColor());
        jugadorDTO.setEnPartida(jugador.isEnPartida());
        return jugadorDTO;
    }
    
}