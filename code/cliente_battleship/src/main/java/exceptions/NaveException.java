package exceptions;

/**
 *
 * @author PC WHITE WOLF
 */
public class NaveException extends Exception {

    /**
     * Creates a new instance of <code>NaveException</code> without detail
     * message.
     */
    public NaveException() {
    }

    /**
     * Constructs an instance of <code>NaveException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NaveException(String msg) {
        super(msg);
    }

    public NaveException(String message, Throwable cause) {
        super(message, cause);
    }
}