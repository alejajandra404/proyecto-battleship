package mx.itson.mappers;

import mx.itson.exceptions.ModelException;
import mx.itson.models.Casilla;
import mx.itson.models.IJugador;
import mx.itson.models.ITableroNaves;
import mx.itson.utils.dtos.TableroDTO;
import mx.itson.utils.enums.EstadoCasilla;

/**
 *
 * @author PC WHITE WOLF
 */
public class TableroNavesMapper {
    
    public static TableroDTO toDTO(ITableroNaves tableroNaves, IJugador jugadorAsociado) throws ModelException{
        TableroDTO tableroDTO = new TableroDTO();
        tableroDTO.setIdJugador(jugadorAsociado.getId());
        tableroDTO.setNombreJugador(jugadorAsociado.getNombre());
        tableroDTO.setNavesHundidas(tableroNaves.getNavesHundidas());
        tableroDTO.setTotalNaves(tableroNaves.getTotalNaves());
        tableroDTO.setNaves(NaveMapper.toDTOList(tableroNaves.getNaves()));
        
        Casilla[][] casillasTablero = tableroNaves.getCasillas();
        
        int filasCasillas = casillasTablero.length;
        int columnasCasillas = casillasTablero[0].length;
        
        EstadoCasilla[][] casillas = new EstadoCasilla[filasCasillas][columnasCasillas];
        
        for (int i = 0; i < filasCasillas; i++)
            for (int j = 0; j < columnasCasillas; j++) 
                casillas[i][j] = casillasTablero[i][j].obtenerEstado();
        
        tableroDTO.setCasillas(casillas);
        
        return tableroDTO;
    }
    
}