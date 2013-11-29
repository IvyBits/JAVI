package tk.ivybits.javi.media.ffmedia;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.*;
import tk.ivybits.javi.media.stream.*;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.Stream;
import tk.ivybits.javi.media.stream.VideoStream;

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
    public ArrayList<FFVideoStream> videoStreams = new ArrayList<>();
    public ArrayList<FFAudioStream> audioStreams = new ArrayList<>();
    public ArrayList<FFSubtitleStream> subtitleStreams = new ArrayList<>();

    /**
     * Creates a FFMedia object sourced from a <code>File</code>.
     *
     * @param source The media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @since 1.0
     */
    public FFMedia(File source) throws IOException {
        this(source.getAbsolutePath());
    }

    /**
     * Creates a FFMedia object sourced from a pointing <code>URL</code>.
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

    /**
     * {@inheritDoc}
     */
    public List<? extends VideoStream> videoStreams() {
        return Collections.unmodifiableList(videoStreams);
    }

    /**
     * {@inheritDoc}
     */
    public List<? extends AudioStream> audioStreams() {
        return Collections.unmodifiableList(audioStreams);
    }

    public List<? extends SubtitleStream> subtitleStreams() {
        return Collections.unmodifiableList(subtitleStreams);
    }

    /**
     * {@inheritDoc}
     */
    public MediaStream.Builder stream() {
        return new FFMediaStream.Builder(this);
    }

    /**
     * {@inheritDoc}
     */
    public long length() {
        if (formatContext.duration == Long.MIN_VALUE)
            return 0;
        return formatContext.duration / 1000;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (formatContext != null) {
            avformat_close_input(new PointerByReference(formatContext.getPointer()));
            formatContext = null;
        }
    }
}
