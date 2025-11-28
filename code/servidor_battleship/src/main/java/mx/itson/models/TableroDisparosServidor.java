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
        // Verifica que el disparo no sea null
        if(disparo == null) throw new ModelException("Disparo inválido");
        // Valida el disparo (podría ser innecesario si es validado por la partida o el subsistema)
        validarDisparo(disparo.obtenerCoordenada());
        // Añade el disparo
        this.disparos.add(disparo);
    }
    
    @Override
    public boolean validarDisparo(Coordenada coordenada) throws ModelException {
        // Si el disparo no existe
        if(coordenada == null) throw new ModelException("Disparo inválido.");
        
        // Si la coordenada está fuera de los límites del tablero
        if(!super.validarCoordenada(coordenada)) throw new ModelException("Coordenada del disparo fuera de los límites del tablero");
        
        // Verifica las coordenadas de cada disparo
        for (Disparo d : disparos) 
            if (d.obtenerCoordenada().equals(coordenada)) throw new ModelException("Ya existe otro disparo con la misma coordenada.");
        
        // Verdadero si pasa todas las validaciones
        return true;
    }
    
    @Override
    public boolean yaDisparado(Coordenada coordenada) {
        for (Disparo disparo : disparos) 
            if (disparo.obtenerCoordenada().equals(coordenada)) return true;
        return false;
    }
}