package mx.itson.mappers;

import java.util.ArrayList;
import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.models.IJugador;
import mx.itson.models.IPartida;
import mx.itson.utils.dtos.PartidaDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public class PartidaMapper {
    
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
    
    public static List<PartidaDTO> toDTOList(List<IPartida> partidas) throws ModelException{
        List<PartidaDTO> partidasDTO = new ArrayList<>();
        for(IPartida partida : partidas){
            PartidaDTO partidaDTO = toDTO(partida);
            partidasDTO.add(partidaDTO);
        }
        return partidasDTO;
    }
    
}