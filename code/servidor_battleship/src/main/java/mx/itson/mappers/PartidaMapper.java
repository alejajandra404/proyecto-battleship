package mx.itson.mappers;

import java.util.ArrayList;
import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.models.IJugador;
import mx.itson.models.IPartida;
import mx.itson.utils.dtos.PartidaDTO;

/**
 * PartidaMapper.java
 *
 * Mapper para convertir entre objetos IPartida del modelo y PartidaDTO.
 * Incluye la conversión de jugadores y tableros asociados.
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
public class PartidaMapper {

    /**
     * Convierte una entidad IPartida del modelo a PartidaDTO.
     * Incluye la conversión de ambos jugadores y sus tableros de naves.
     *
     * @param partida Partida del modelo
     * @return DTO con los datos completos de la partida
     * @throws ModelException Si ocurre un error durante la conversión
     */
    public static PartidaDTO toDTO(IPartida partida) throws ModelException{
        PartidaDTO partidaDTO = new PartidaDTO();
        partidaDTO.setIdPartida(partida.getIdPartida());

        IJugador jugador1 = partida.getJugador1();
        IJugador jugador2 = partida.getJugador2();

        partidaDTO.setJugador1(JugadorMapper.toDTO(jugador1));
        partidaDTO.setJugador2(JugadorMapper.toDTO(jugador2));

        partidaDTO.setTableroJugador1(TableroNavesMapper.toDTO(jugador1.getTableroNaves(), jugador1));
        partidaDTO.setTableroJugador2(TableroNavesMapper.toDTO(jugador2.getTableroNaves(), jugador2));

        partidaDTO.setAmbosJugadoresListos(partida.ambosJugadoresListos());
        partidaDTO.setIdJugadorEnTurno(partida.getIdJugadorEnTurno());
        partidaDTO.setTiempoRestante(partida.getTiempoRestante());
        partidaDTO.setEstadoPartida(partida.getEstadoPartida());
        partidaDTO.setIdGanador(partida.getIdGanador());
        return partidaDTO;
    }

    /**
     * Convierte una lista de IPartida del modelo a una lista de PartidaDTO.
     *
     * @param partidas Lista de partidas del modelo
     * @return Lista de DTOs de partidas
     * @throws ModelException Si ocurre un error durante la conversión
     */
    public static List<PartidaDTO> toDTOList(List<IPartida> partidas) throws ModelException{
        List<PartidaDTO> partidasDTO = new ArrayList<>();
        for(IPartida partida : partidas){
            PartidaDTO partidaDTO = toDTO(partida);
            partidasDTO.add(partidaDTO);
        }
        return partidasDTO;
    }

}