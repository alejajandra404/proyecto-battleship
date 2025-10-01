/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.io.Serializable;

/**
 * CoordenadaDTO - Data Transfer Object para Coordenada
 *
 * Versión de Coordenada para transferencia de datos
 *
 * @author Leonardo Flores Leyva ID: 00000252390
 * @author Yuri Germán García López ID: 00000252583
 * @author Alejandra García Preciado ID: 00000252444
 * @author Jesús Ernesto López Ibarra ID: 00000252663
 * @author Daniel Miramontes Iribe ID: 00000252801
 */
public class CoordenadaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int x;
    private int y;

    /**
     * Constructor vacío
     */
    public CoordenadaDTO() {
    }

    /**
     * Constructor con parámetros
     *
     * @param x
     * @param y
     */
    public CoordenadaDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters y Setters
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Convierte a formato de texto (ej: "A1", "B3")
     *
     * @return
     */
    public String toStringCoord() {
        char letra = (char) ('A' + y);
        return letra + String.valueOf(x + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CoordenadaDTO that = (CoordenadaDTO) obj;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
