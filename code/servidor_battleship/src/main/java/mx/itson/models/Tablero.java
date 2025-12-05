package mx.itson.models;

import mx.itson.exceptions.ModelException;

/**
 * Tablero.java
 *
 * Clase abstracta base para tableros del juego.
 * Proporciona funcionalidad común para validación de coordenadas y tamaño.
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

    protected final int tamanio;

    /**
     * Constructor de la clase Tablero.
     * Valida e inicializa el tamaño del tablero.
     *
     * @param tamanio Tamaño del tablero (número de filas y columnas)
     * @throws ModelException Si el tamaño es inválido
     */
    public Tablero(int tamanio) throws ModelException{
        validarTamanio(tamanio);
        this.tamanio = tamanio;
    }

    /**
     * Valida si una coordenada está dentro de los límites del tablero.
     *
     * @param coordenada Coordenada a validar
     * @return true si la coordenada está dentro del tablero
     * @throws ModelException Si ocurre un error durante la validación
     */
    public boolean validarCoordenada(Coordenada coordenada) throws ModelException{
        int x = coordenada.obtenerX();
        int y = coordenada.obtenerY();
        return (x < tamanio && x >= 0) && (y < tamanio && y >= 0);
    }

    /**
     * Valida que el tamaño del tablero sea válido (mayor o igual a 0).
     *
     * @param tamanio Tamaño a validar
     * @throws ModelException Si el tamaño es negativo
     */
    private void validarTamanio(int tamanio) throws ModelException{
        if(tamanio < 0) throw new ModelException("Solo se aceptan tamaños positivos.");
    }

    /**
     * Obtiene el tamaño del tablero.
     *
     * @return Tamaño del tablero
     */
    public int getTamanio() {return tamanio;}
}