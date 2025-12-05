package mx.itson.mappers;

import mx.itson.models.Coordenada;
import mx.itson.utils.dtos.CoordenadaDTO;

/**
 * CoordenadaMapper.java
 *
 * Mapper para convertir entre objetos Coordenada del modelo y CoordenadaDTO.
 * Proporciona métodos para conversión individual y en arreglos.
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
public class CoordenadaMapper {

    /**
     * Convierte un CoordenadaDTO a una entidad Coordenada del modelo.
     *
     * @param coordenadaDTO DTO con los datos de la coordenada
     * @return Coordenada del modelo
     */
    public static Coordenada toEntity(CoordenadaDTO coordenadaDTO){
        return new Coordenada(coordenadaDTO.getX(), coordenadaDTO.getY());
    }

    /**
     * Convierte una entidad Coordenada del modelo a CoordenadaDTO.
     *
     * @param coordenada Coordenada del modelo
     * @return DTO con los datos de la coordenada
     */
    public static CoordenadaDTO toDTO(Coordenada coordenada){
        return new CoordenadaDTO(coordenada.obtenerX(), coordenada.obtenerY());
    }

    /**
     * Convierte un arreglo de CoordenadaDTO a un arreglo de Coordenada del modelo.
     *
     * @param coordenadasDTO Arreglo de DTOs de coordenadas
     * @return Arreglo de coordenadas del modelo
     */
    public static Coordenada[] toEntityArray(CoordenadaDTO[] coordenadasDTO){
        Coordenada[] coordenadas = new Coordenada[coordenadasDTO.length];
        for(int i = 0; i < coordenadasDTO.length; i++)
            coordenadas[i] = new Coordenada(coordenadasDTO[i].getX(), coordenadasDTO[i].getY());
        return coordenadas;
    }

    /**
     * Convierte un arreglo de Coordenada del modelo a un arreglo de CoordenadaDTO.
     *
     * @param coordenadas Arreglo de coordenadas del modelo
     * @return Arreglo de DTOs de coordenadas
     */
    public static CoordenadaDTO[] toDTOArray(Coordenada[] coordenadas){
        CoordenadaDTO[] coordenadasDTO = new CoordenadaDTO[coordenadas.length];
        for(int i = 0; i < coordenadas.length; i++)
            coordenadasDTO[i] = new CoordenadaDTO(coordenadas[i].obtenerX(), coordenadas[i].obtenerY());
        return coordenadasDTO;
    }

}