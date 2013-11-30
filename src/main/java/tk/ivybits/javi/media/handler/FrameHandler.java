package tk.ivybits.javi.media.handler;

import java.awt.image.BufferedImage;

public abstract class FrameHandler {
    public static final FrameHandler NO_HANDLER = new FrameHandler() {
        @Override
        public void handle(BufferedImage buffer, long time) {
        }
    };

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @param time   The time since the last frame.
     * @since 1.0
     */
    public abstract void handle(BufferedImage buffer, long time);

    public void end() {

    }

    public void start() {

    }
}
