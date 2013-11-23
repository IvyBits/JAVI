package tk.ivybits.javi.media;

/**
 * A stream handler.
 *
 * @param <T> The type of buffer this handler supports.
 * @version 1.0
 * @since 1.0
 */
public abstract class MediaHandler<T> {
    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @since 1.0
     */
    public void handle(T buffer) {
    }

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @param time   The time since the last frame.
     * @since 1.0
     */
    public void handle(T buffer, long time) {
    }

    /**
     * Called to signify that the stream has ended.
     * <p/>
     * Cleanup (if any) should be done here.
     *
     * @since 1.0
     */
    public void end() {

    }
}