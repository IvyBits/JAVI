/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package tk.ivybits.javi.media.stream;

import tk.ivybits.javi.media.handler.AudioHandler;
import tk.ivybits.javi.media.handler.FrameHandler;
import tk.ivybits.javi.media.handler.SubtitleHandler;

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
     * @return The stream previously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     * @since 1.0
     */
    VideoStream setVideoStream(VideoStream stream);

    /**
     * Sets a audio stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream previously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     * @since 1.0
     */
    AudioStream setAudioStream(AudioStream stream);

    /**
     * Sets a subtitle stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream previously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     * @since 1.0
     */
    SubtitleStream setSubtitleStream(SubtitleStream stream);

    /**
     * Fetches current audio stream.
     *
     * @return The {@code AudioStream} currently in use, or null if none.
     * @since 1.0
     */
    AudioStream getAudioStream();

    /**
     * Fetches current video stream.
     *
     * @return The {@code VideoStream} currently in use, or null if none.
     * @since 1.0
     */
    VideoStream getVideoStream();

    /**
     * Fetches current subtitle stream.
     *
     * @return The {@code SubtitleStream} currently in use, or null if none.
     */
    SubtitleStream getSubtitleStream();

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
     * @throws StreamException          Thrown if the seek failed or if called when called on a stream that is not started.
     * @since 1.0
     */
    void seek(long to);

    /**
     * Fetches the current time of the media, in milliseconds.
     *
     * @return The current time.
     * @since 1.0
     */
    long position();

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
        Builder audio(AudioHandler audioHandler);

        /**
         * Specifies the video stream handler.
         *
         * @param videoHandler The video handler. Should accept BufferedImages of arbitrary sizes.
         * @return The current Builder.
         * @since 1.0
         */
        Builder video(FrameHandler videoHandler);

        /**
         * Specifies the subtitle stream handler.
         *
         * @param subtitleHandler The subtitle handler. Should accept objects that implement
         *                        {@link tk.ivybits.javi.media.subtitle.Subtitle}.
         * @return The current Builder.
         * @since 1.0
         */
        Builder subtitle(SubtitleHandler subtitleHandler);

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
