package exceptions;

/**
 *
 * @author PC WHITE WOLF
 */
public class CasillaException extends Exception {

    /**
     * Creates a new instance of <code>CasillaException</code> without detail
     * message.
     */
    public CasillaException() {}

    /**
     * Constructs an instance of <code>CasillaException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CasillaException(String msg) {
        super(msg);
    }
    
    public CasillaException(String message, Throwable cause) {
        super(message, cause);
    }
    
}