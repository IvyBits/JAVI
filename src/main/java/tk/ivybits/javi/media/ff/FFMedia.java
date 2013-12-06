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

package tk.ivybits.javi.media.ff;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.stream.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static tk.ivybits.javi.ffmpeg.LibAVFormat.avformat_close_input;

/**
 * FFmpeg-backed media container.
 *
 * @version 1.0
 * @since 1.0
 */
public class FFMedia implements Media {
    public AVFormatContext formatContext;
    public ArrayList<FFVideoStream> videoStreams = new ArrayList<FFVideoStream>();
    public ArrayList<FFAudioStream> audioStreams = new ArrayList<FFAudioStream>();
    public ArrayList<FFSubtitleStream> subtitleStreams = new ArrayList<FFSubtitleStream>();

    /**
     * Creates a FFMedia object sourced from a {@code File}.
     *
     * @param source The media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @since 1.0
     */
    public FFMedia(File source) throws IOException {
        this(source.getAbsolutePath());
    }

    /**
     * Creates a FFMedia object sourced from a pointing {@code URL}.
     *
     * @param source The URL of the media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @since 1.0
     */
    public FFMedia(URI source) throws IOException {
        this(source.toASCIIString());
    }

    private FFMedia(String source) throws IOException {
        PointerByReference ppFormatCtx = new PointerByReference();

        source = URLDecoder.decode(source, "UTF-8");
        if (LibAVFormat.avformat_open_input(ppFormatCtx, source, null, null) != 0)
            throw new IOException("failed to open video file: " + source);

        formatContext = new AVFormatContext(ppFormatCtx.getValue());
        if (LibAVFormat.av_find_stream_info(formatContext.getPointer()) < 0)
            throw new IOException("failed to find stream info");
        formatContext.read();

        for (int i = 0; i < formatContext.nb_streams; ++i) {
            AVStream stream = new AVStream(formatContext.streams.getPointer(i * Pointer.SIZE));
            Stream.Type type = Stream.Type.values()[stream.codec.codec_type];

            switch (type) {
                case STREAM_VIDEO:
                    videoStreams.add(new FFVideoStream(this, stream));
                    break;
                case STREAM_AUDIO:
                    audioStreams.add(new FFAudioStream(this, stream));
                    break;
                case STREAM_SUBTITLE:
                    subtitleStreams.add(new FFSubtitleStream(this, stream));
                    break;
            }
        }
    }

    @Override
    public List<? extends VideoStream> videoStreams() {
        return Collections.unmodifiableList(videoStreams);
    }

    @Override
    public List<? extends AudioStream> audioStreams() {
        return Collections.unmodifiableList(audioStreams);
    }

    @Override
    public List<? extends SubtitleStream> subtitleStreams() {
        return Collections.unmodifiableList(subtitleStreams);
    }

    @Override
    public MediaStream.Builder stream() {
        return new FFMediaStream.Builder(this);
    }

    @Override
    public long length() {
        if (formatContext.duration == Long.MIN_VALUE)
            return 0;
        return formatContext.duration / 1000;
    }

    @Override
    public void close() {
        if (formatContext != null) {
            avformat_close_input(new PointerByReference(formatContext.getPointer()));
        }
    }
}
