package mx.itson.mappers;

import java.util.ArrayList;
import java.util.List;
import mx.itson.exceptions.ModelException;
import mx.itson.models.Nave;
import mx.itson.utils.dtos.NaveDTO;

/**
 *
 * @author PC WHITE WOLF
 */
public class NaveMapper {
    
    public static Nave toEntity(NaveDTO naveDTO) throws ModelException{
        Nave nave = new Nave(
                naveDTO.getTipo(), 
                naveDTO.getOrientacion(), 
                CoordenadaMapper.toEntityArray(naveDTO.getCoordenadas())
        );
        return nave;
    }
    
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