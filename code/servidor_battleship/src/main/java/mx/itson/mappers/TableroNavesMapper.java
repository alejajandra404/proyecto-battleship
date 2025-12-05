package mx.itson.mappers;

import mx.itson.exceptions.ModelException;
import mx.itson.models.Casilla;
import mx.itson.models.IJugador;
import mx.itson.models.ITableroNaves;
import mx.itson.utils.dtos.TableroDTO;
import mx.itson.utils.enums.EstadoCasilla;

/**
 * TableroNavesMapper.java
 *
 * Mapper para convertir entre objetos ITableroNaves del modelo y TableroDTO.
 * Incluye la conversión de casillas, naves y estadísticas del tablero.
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
public class TableroNavesMapper {

    /**
     * Convierte una entidad ITableroNaves del modelo a TableroDTO.
     * Incluye la conversión de todas las casillas, naves y estadísticas.
     *
     * @param tableroNaves Tablero de naves del modelo
     * @param jugadorAsociado Jugador dueño del tablero
     * @return DTO con los datos completos del tablero
     * @throws ModelException Si ocurre un error durante la conversión
     */
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