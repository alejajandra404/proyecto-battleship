package mx.itson.mappers;

import mx.itson.models.Disparo;
import mx.itson.utils.dtos.DisparoDTO;

/**
 * DisparoMapper.java
 *
 * Mapper para convertir entre objetos Disparo del modelo y DisparoDTO.
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
public class DisparoMapper {

    /**
     * Convierte un DisparoDTO a una entidad Disparo del modelo.
     *
     * @param disparoDTO DTO con los datos del disparo
     * @return Disparo del modelo
     */
    public static Disparo toEntity(DisparoDTO disparoDTO){
        return new Disparo(CoordenadaMapper.toEntity(disparoDTO.getCoordenada()));
    }

}