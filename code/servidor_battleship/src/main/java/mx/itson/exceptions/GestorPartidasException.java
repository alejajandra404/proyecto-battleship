package mx.itson.exceptions;

/**
 *
 * @author PC WHITE WOLF
 */
public class GestorPartidasException extends Exception {

    /**
     * Creates a new instance of <code>GestorPartidasException</code> without
     * detail message.
     */
    public GestorPartidasException() {}

    /**
     * Constructs an instance of <code>GestorPartidasException</code> with the
     * specified detail message.
     *
     * @param message
     */
    public GestorPartidasException(String message) {
        super(message);
    }

    public GestorPartidasException(String message, Throwable cause) {
        super(message, cause);
    }
}