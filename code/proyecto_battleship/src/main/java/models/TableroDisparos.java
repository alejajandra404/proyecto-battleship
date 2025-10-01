package models;
import exceptions.TableroException;
import java.util.HashSet;
import java.util.Set;
/**
 * TableroDisparos.java
 *
 * Clase entidad que hereda de la clase padre Tablero
 * y representa el tablero de disparos de un jugador.
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
public class TableroDisparos extends Tablero {
    
    private final Set<Disparo> disparos;
    
    public TableroDisparos(Jugador propietario, int tamanio) throws TableroException {
        super(propietario, tamanio);
        this.disparos = new HashSet<>();
    }

    /**
     * Añade un nuevo disparo al registro del tablero
     * @param disparo El disparo a registrar
     * @throws exceptions.TableroException
     */
    public void añadirDisparo(Disparo disparo) throws TableroException{
        validarDisparo(disparo);
        this.disparos.add(disparo);
    }
    
    /**
     * Valida si ya se ha disparado a una coordenada previamente
     * @param disparo
     * @return true si la coordenada está libre, false si ya fue disparada
     * @throws exceptions.TableroException
     */
    public boolean validarDisparo(Disparo disparo) throws TableroException {
        // Si el disparo no existe
        if(disparo == null)
            throw new TableroException("Disparo inválido.");
        // Si la coordenada del disparo no existe
        if(disparo.obtenerCoordenada() == null)
            throw new TableroException("El disparo carece de una coordenada.");
        // Si el resultado del disparo no existe
        if(disparo.obtenerResultado() == null)
            throw new TableroException("El disparo carece de un resultado.");
        // Verifica las coordenadas de cada disparo
        for (Disparo d : disparos) {
            if (d.obtenerCoordenada().equals(disparo.obtenerCoordenada()))
                throw new TableroException("Ya existe otro disparo con la misma coordenada.");   
        }
        // Verdadero si pasa todas las validaciones
        return true;
    }
}