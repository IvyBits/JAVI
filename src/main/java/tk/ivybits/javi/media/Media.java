package tk.ivybits.javi.media;

import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.VideoStream;

import java.io.Closeable;
import java.util.List;

/**
 * An object to represent a container.
 * <p/>
 * May contain an arbitrary number of streams of arbitrary types.
 * <p/>
 * <b>Note: currently only one video and audio stream is accessible.</b>
 *
 * @version 1.0
 * @since 1.0
 */
public interface Media extends Closeable {
    public List<? extends VideoStream> videoStreams();

    public List<? extends AudioStream> audioStreams();

    /**
     * Prepares video for streaming.
     *
     * @return A Builder object to configure the way the video will be streamed.
     * @since 1.0
     */
    public MediaStream.Builder stream();

    /**
     * Fetches the length of the video.
     *
     * @return The length of the video in milliseconds.
     * @since 1.0
     */
    public long length();
}
