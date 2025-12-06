package mx.itson.exceptions;

/**
 * Excepción del subsistema GestorPartidas.
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
public class GestorPartidasException extends Exception {

    /**
     * Constructor por defecto.
     */
    public GestorPartidasException() {}

    /**
     * Constructor con mensaje.
     * @param message Mensaje de la excepción.
     */
    public GestorPartidasException(String message) {super(message);}
    /**
     * Constructor con mensaje y causa.
     * @param message Mensaje de la excepción.
     * @param cause Causa de la excepción.
     */
    public GestorPartidasException(String message, Throwable cause) {super(message, cause);}
}