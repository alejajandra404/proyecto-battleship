package exceptions;

/**
 *
 * @author PC WHITE WOLF
 */
public class TableroException extends Exception {

    /**
     * Creates a new instance of <code>TableroException</code> without detail
     * message.
     */
    public TableroException() {
    }

    /**
     * Constructs an instance of <code>TableroException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TableroException(String msg) {
        super(msg);
    }

    public TableroException(String message, Throwable cause) {
        super(message, cause);
    }
}
