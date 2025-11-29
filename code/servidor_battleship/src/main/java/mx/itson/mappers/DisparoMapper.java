package mx.itson.mappers;

import mx.itson.models.Disparo;
import mx.itson.utils.dtos.DisparoDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public class DisparoMapper {
    
    public static Disparo toEntity(DisparoDTO disparoDTO){
        return new Disparo(CoordenadaMapper.toEntity(disparoDTO.getCoordenada()));
    }
    
}