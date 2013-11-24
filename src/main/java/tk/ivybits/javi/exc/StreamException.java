package tk.ivybits.javi.exc;

/**
 * Signals that an error has occured while streaming.
 *
 * @since 1.0
 */
public class StreamException extends RuntimeException {
    /**
     * Constructs an StreamException with no detail message.
     * A detail message is a String that describes this particular
     * exception.
     */
    public StreamException() {
        super();
    }

    /**
     * Constructs an StreamException with the specified detail
     * message.  A detail message is a String that describes this particular
     * exception.
     *
     * @param err the String that contains a detailed message
     */
    public StreamException(String err) {
        super(err);
    }
}
