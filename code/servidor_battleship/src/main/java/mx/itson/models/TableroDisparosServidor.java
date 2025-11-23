package mx.itson.models;

import java.util.HashSet;
import java.util.Set;
import mx.itson.exceptions.ModelException;

/**
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
 */
public class TableroDisparosServidor extends Tablero implements ITableroDisparos {
    
    private final Set<Disparo> disparos;
    
    public TableroDisparosServidor(int tamanio) throws ModelException {
        super(tamanio);
        this.disparos = new HashSet<>();
    }

    /**
     * Añade un nuevo disparo al registro del tablero
     * @param disparo El disparo a registrar
     * @throws ModelException
     */
    @Override
    public void añadirDisparo(Disparo disparo) throws ModelException{
        validarDisparo(disparo);
        this.disparos.add(disparo);
    }
    
    /**
     * Valida si ya se ha disparado a una coordenada previamente
     * @param disparo Disparo a validar
     * @return true si la coordenada está libre, false si ya fue disparada
     * @throws ModelException
     */
    @Override
    public boolean validarDisparo(Disparo disparo) throws ModelException {
        // Si el disparo no existe
        if(disparo == null)
            throw new ModelException("Disparo inválido.");
        // Si la coordenada del disparo no existe
        if(disparo.obtenerCoordenada() == null)
            throw new ModelException("El disparo carece de una coordenada.");
        // Si el resultado del disparo no existe
        if(disparo.obtenerResultado() == null)
            throw new ModelException("El disparo carece de un resultado.");
        // Verifica las coordenadas de cada disparo
        for (Disparo d : disparos) {
            if (d.obtenerCoordenada().equals(disparo.obtenerCoordenada()))
                throw new ModelException("Ya existe otro disparo con la misma coordenada.");   
        }
        // Verdadero si pasa todas las validaciones
        return true;
    }
    
    /**
     * Verifica si ya se disparó en una coordenada 
     * 
     * @param coordenada
     * @return 
     */
    @Override
    public boolean yaDisparado(Coordenada coordenada) {
        for (Disparo disparo : disparos) {
            if (disparo.obtenerCoordenada().equals(coordenada)) 
                return true;
        }
        return false;
    }
    
    @Override
    public Set<Disparo> getDisparosRealizados() {
        return this.disparos;
    }
}