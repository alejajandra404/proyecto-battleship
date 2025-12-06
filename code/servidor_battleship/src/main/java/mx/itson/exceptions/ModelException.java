package mx.itson.exceptions;

/**
 * Excepción del dominio.
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
public class ModelException extends Exception {

    /**
     * Constructor por defecto.
     */
    public ModelException() {}

    /**
     * Constructor con mensaje.
     * @param msg Mensaje de la excepción.
     */
    public ModelException(String msg) {super(msg);}
}