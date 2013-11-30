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

    public void end() {

    }

    public void start() {

    }
}
