package tk.ivybits.javi.media.handler;

import java.nio.ByteBuffer;

public abstract class AudioHandler {
    public static final AudioHandler NO_HANDLER = new AudioHandler() {
        @Override
        public void handle(ByteBuffer buffer) {
        }
    };

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @since 1.0
     */
    public abstract void handle(ByteBuffer buffer);

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
