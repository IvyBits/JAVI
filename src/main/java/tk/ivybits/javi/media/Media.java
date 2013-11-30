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

package tk.ivybits.javi.media;

import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.SubtitleStream;
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

    public List<? extends SubtitleStream> subtitleStreams();

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
