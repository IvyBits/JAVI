package tk.ivybits.javi.media.stream;

import tk.ivybits.javi.media.Media;

import java.io.Closeable;

/**
 * Represents an arbitrary stream in a container.
 */
public interface Stream extends Closeable {
    /**
     * Fetches the parent container.
     *
     * @return Said parent container.
     */
    Media container();

    /**
     * Fetches the type of this stream.
     *
     * @return A Type. Current possible values are STREAM_VIDEO and STREAM_AUDIO.
     */
    Type type();

    /**
     * Fetches the index of this stream in the parent container
     *
     * @return
     */
    int index();

    /**
     * Fetches the name of the codec.
     *
     * @return The name of the codec.
     * @since 1.0
     */
    String codecName();

    /**
     * Fetches a descriptive name of the codec.
     *
     * @return The name; format may differ substantially from codec to codec.
     * @since 1.0
     */
    String longCodecName();

    /**
     * Enum for all possible streams that a container may hold.
     *
     * @version 1.0
     * @since 1.0
     */
    public static enum Type {
        STREAM_VIDEO,
        STREAM_AUDIO,
        STREAM_DATA,
        STREAM_SUBTITLE,
        STREAM_ATTACHMENT
    }
}
