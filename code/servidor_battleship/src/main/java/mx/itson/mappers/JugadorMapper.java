package mx.itson.mappers;

import mx.itson.models.IJugador;
import mx.itson.utils.dtos.JugadorDTO;

/**
 * JugadorMapper.java
 *
 * Mapper para convertir entre objetos IJugador del modelo y JugadorDTO.
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
public class JugadorMapper {

    /**
     * Convierte una entidad IJugador del modelo a JugadorDTO.
     *
     * @param jugador Jugador del modelo
     * @return DTO con los datos del jugador
     */
    public static JugadorDTO toDTO(IJugador jugador){
        JugadorDTO jugadorDTO = new JugadorDTO();
        jugadorDTO.setId(jugador.getId());
        jugadorDTO.setNombre(jugador.getNombre());
        jugadorDTO.setColor(jugador.getColor());
        jugadorDTO.setEnPartida(jugador.isEnPartida());
        return jugadorDTO;
    }

}