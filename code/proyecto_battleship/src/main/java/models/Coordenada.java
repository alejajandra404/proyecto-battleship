 package models;
/**
 * Coordenada.java
 *
 * Clase entidad que representa la coordenada 
 * de una casilla dentro del tablero.
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
public class Coordenada {
    
    private int x;
    private int y;

    /**
     * Constructor para crear una nueva Coordenada
     * @param x El índice de la fila (usualmente 0 a 9)
     * @param y El índice de la columna (usualmente 0 a 9)
     */
    public Coordenada(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Valida si la coordenada está dentro de los límites de un tablero estándar (10x10)
     * @return true si la coordenada es válida, false en caso contrario
     */
    public boolean validar() {
        if (x >= 0 && x < 10 && y >= 0 && y < 10) {
            return true;
        } else {
            return false;
        }
    }

    // --- Getters --- //
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
    public String toString() {
        return "Coordenada{" + "x=" + x + ", y=" + y + '}';
    }
}
