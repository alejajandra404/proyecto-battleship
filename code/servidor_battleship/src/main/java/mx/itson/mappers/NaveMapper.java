package mx.itson.mappers;

import java.util.ArrayList;
import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.models.Nave;
import mx.itson.utils.dtos.NaveDTO;

/**
 * NaveMapper.java
 *
 * Mapper para convertir entre objetos Nave del modelo y NaveDTO.
 * Proporciona métodos para conversión individual y en listas.
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
public class NaveMapper {

    /**
     * Convierte un NaveDTO a una entidad Nave del modelo.
     *
     * @param naveDTO DTO con los datos de la nave
     * @return Nave del modelo
     * @throws ModelException Si los datos de la nave son inválidos
     */
    public static Nave toEntity(NaveDTO naveDTO) throws ModelException{
        Nave nave = new Nave(
                naveDTO.getTipo(),
                naveDTO.getOrientacion(),
                CoordenadaMapper.toEntityArray(naveDTO.getCoordenadas())
        );
        return nave;
    }

    /**
     * Convierte una entidad Nave del modelo a NaveDTO.
     *
     * @param nave Nave del modelo
     * @return DTO con los datos de la nave
     * @throws ModelException Si ocurre un error durante la conversión
     */
    public static NaveDTO toDTO(Nave nave) throws ModelException{
        NaveDTO naveDTO = new NaveDTO(
                nave.getTipo(),
                nave.getEstado(),
                nave.obtenerOrientacion(),
                CoordenadaMapper.toDTOArray(nave.obtenerCoordenadas()),
                nave.getImpactosRecibidos()
        );
        return naveDTO;
    }

    /**
     * Convierte una lista de NaveDTO a una lista de Nave del modelo.
     *
     * @param navesDTO Lista de DTOs de naves
     * @return Lista de naves del modelo
     * @throws ModelException Si alguna nave tiene datos inválidos
     */
    public static List<Nave> toEntityList(List<NaveDTO> navesDTO) throws ModelException{
        List<Nave> naves = new ArrayList<>();
        if(navesDTO != null){
            for(NaveDTO naveDTO: navesDTO){
                Nave nave = toEntity(naveDTO);
                naves.add(nave);
            }
        }
        return naves;
    }

    /**
     * Convierte una lista de Nave del modelo a una lista de NaveDTO.
     *
     * @param naves Lista de naves del modelo
     * @return Lista de DTOs de naves
     * @throws ModelException Si ocurre un error durante la conversión
     */
    public static List<NaveDTO> toDTOList(List<Nave> naves) throws ModelException{
        List<NaveDTO> navesDTO = new ArrayList<>();
        if(naves != null){
            for(Nave nave: naves){
                NaveDTO naveDTO = toDTO(nave);
                navesDTO.add(naveDTO);
            }
        }
        return navesDTO;
    }

}