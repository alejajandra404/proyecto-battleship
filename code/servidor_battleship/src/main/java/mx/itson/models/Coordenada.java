package mx.itson.models;

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
public class Coordenada {
    
    private final int x;
    private final int y;

    /**
     * Constructor para crear una nueva Coordenada
     * @param x El índice de la fila (usualmente 0 a 9)
     * @param y El índice de la columna (usualmente 0 a 9)
     */
    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // --- Getters --- //
    /**
     * Devuelve el valor de X.
     * @return Valor de X.
     */
    public int obtenerX() {return x;}
    /**
     * Devuelve el valor de Y.
     * @return Valor de Y.
     */
    public int obtenerY() {return y;}
    
    public boolean compararCoordenada(Coordenada coordenada){
        if (this.x != coordenada.x) 
            return false;
        return this.y == coordenada.y;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.x;
        hash = 67 * hash + this.y;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordenada other = (Coordenada) obj;
        if (this.x != other.x) {
            return false;
        }
        return this.y == other.y;
    }

    @Override
    public String toString() {return String.format("%d, %d", obtenerX(), obtenerY());}
}