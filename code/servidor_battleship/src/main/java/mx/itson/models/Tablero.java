package mx.itson.models;

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
public abstract class Tablero {
    
    private final int tamanio;
    /**
     * 
     * @param tamanio
     * @throws ModelException 
     */
    public Tablero(int tamanio) throws ModelException{
        validarTamanio(tamanio);
        this.tamanio = tamanio;
    }
    /**
     * 
     * @param coordenada
     * @return
     * @throws ModelException 
     */
    public boolean validarCoordenada(Coordenada coordenada) throws ModelException{
        int x = coordenada.obtenerX();
        int y = coordenada.obtenerY();
        return (x < tamanio && x >= 0) && (y < tamanio && y >= 0);
    }
    /**
     * 
     * @param tamanio
     * @throws ModelException 
     */
    private void validarTamanio(int tamanio) throws ModelException{
        if(tamanio < 0)
            throw new ModelException("Solo se aceptan tamaños positivos.");
    }
    /**
     * 
     * @return 
     */
    public int getTamanio() {return tamanio;}
}