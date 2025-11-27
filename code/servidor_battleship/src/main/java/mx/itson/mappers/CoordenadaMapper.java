package mx.itson.mappers;

import mx.itson.models.Coordenada;
import mx.itson.utils.dtos.CoordenadaDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public class CoordenadaMapper {
    
    public static Coordenada toEntity(CoordenadaDTO coordenadaDTO){
        return new Coordenada(coordenadaDTO.getX(), coordenadaDTO.getY());
    }
    
    public static CoordenadaDTO toDTO(Coordenada coordenada){
        return new CoordenadaDTO(coordenada.obtenerX(), coordenada.obtenerY());
    }
    
    public static Coordenada[] toEntityArray(CoordenadaDTO[] coordenadasDTO){
        Coordenada[] coordenadas = new Coordenada[coordenadasDTO.length];
        for(int i = 0; i < coordenadasDTO.length; i++)
            coordenadas[i] = new Coordenada(coordenadasDTO[i].getX(), coordenadasDTO[i].getY());
        return coordenadas;
    }
    
    public static CoordenadaDTO[] toDTOArray(Coordenada[] coordenadas){
        CoordenadaDTO[] coordenadasDTO = new CoordenadaDTO[coordenadas.length];
        for(int i = 0; i < coordenadas.length; i++)
            coordenadasDTO[i] = new CoordenadaDTO(coordenadas[i].obtenerX(), coordenadas[i].obtenerY());
        return coordenadasDTO;
    }
    
}