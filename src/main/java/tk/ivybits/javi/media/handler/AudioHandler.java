package tk.ivybits.javi.media.handler;

public abstract class AudioHandler {
    public static final AudioHandler NO_HANDLER = new AudioHandler() {
        @Override
        public void handle(byte[] buffer) {
        }
    };

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @since 1.0
     */
    public abstract void handle(byte[] buffer);

    /**
     * Signifies the start of a stream.
     *
     * @since 1.0
     */
    public void start() {

    }

    /**
     * Signifies the end of a stream.
     *
     * @since 1.0
     */
    public void end() {

    }
}
