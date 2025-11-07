package mx.itson.exceptions;

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
public class ServidorException extends Exception {

    /**
     * Creates a new instance of <code>ServidorException</code> without detail
     * message.
     */
    public ServidorException() {}

    /**
     * Constructs an instance of <code>ServidorException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ServidorException(String msg) {
        super(msg);
    }
}