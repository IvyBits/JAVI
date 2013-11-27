package tk.ivybits.javi.media.stream;

import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.media.MediaHandler;
import tk.ivybits.javi.media.subtitle.Subtitle;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;

/**
 * A media stream.
 * </p>
 * Cannot be instantiated directly: use {@link MediaStream.Builder}.
 *
 * @version 1.0
 * @since 1.0
 */
public interface MediaStream extends Runnable, Closeable {
    /**
     * Sets a video stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream perviously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     */
    VideoStream setVideoStream(VideoStream stream);

    /**
     * Sets a audio stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream perviously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     */
    AudioStream setAudioStream(AudioStream stream);

    public SubtitleStream setSubtitleStream(SubtitleStream stream);

    /**
     * Checks if the stream is playing.
     *
     * @return True if so, false otherwise.
     * @since 1.0
     */
    boolean isPlaying();

    /**
     * Sets the current state of the stream.
     *
     * @param flag If true, the stream will be played. Otherwise, it will be paused.
     * @throws StreamException Thrown if called when called on a stream that is not started.
     * @since 1.0
     */
    void setPlaying(boolean flag);

    /**
     * Sets the current position of the stream, in milliseconds.
     *
     * @param to The position to seek to.
     * @throws IllegalArgumentException Thrown if the seek position is invalid.
     * @throws StreamException          Thrown if the seek failed.
     * @throws StreamException          Thrown if called when called on a stream that is not started.
     * @since 1.0
     */
    void seek(long to);

    @Override
    void close();

    /**
     * Builder for generating valid {@link MediaStream} objects.
     *
     * @since 1.0
     */
    public static interface Builder {
        /**
         * Creates a MediaStream builder for the specified {@link tk.ivybits.javi.media.Media} object.
         *
         * @param media The container designated for streaming.
         * @since 1.0
         */

        /**
         * Specifies the audio stream handler.
         *
         * @param audioHandler The audio handler. Should accept byte arrays of arbitrary size as signed 16-bit PCM audio
         *                     data. Frequency and channels may be obtained from the source media container.
         * @return The current Builder.
         * @since 1.0
         */
        Builder audio(MediaHandler<byte[]> audioHandler);

        /**
         * Specifies the video stream handler.
         *
         * @param videoHandler The video handler. Should accept BufferedImages of arbitrary sizes.
         * @return The current Builder.
         * @since 1.0
         */
        Builder video(MediaHandler<BufferedImage> videoHandler);

        Builder subtitle(MediaHandler<Subtitle> subtitleHandler);

        /**
         * Finalize creation of a {@link MediaStream}.
         *
         * @return The aforementioned stream.
         * @throws IOException           Thrown if a stream could not be established.
         * @throws IllegalStateException Thrown if no stream handlers have been specified.
         * @since 1.0
         */
        MediaStream create() throws IOException;
    }
}
