package tk.ivybits.javi.media.handler;

public abstract class AudioHandler {
    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @since 1.0
     */
    public abstract void handle(byte[] buffer);

    public void end() {

    }
}
