package tk.ivybits.javi.media.handler;

import com.sun.jna.Pointer;
import tk.ivybits.javi.media.stream.Frame;
import tk.ivybits.javi.media.transcoder.SafeByteBuffer;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public abstract class FrameHandler {
    public static final FrameHandler NO_HANDLER = new FrameHandler() {
        @Override
        public void handle(Frame buffer, long time) {
        }
    };

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @param time   The duration of this frames.
     * @since 1.0
     */
    public abstract void handle(Frame buffer, long time);

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
