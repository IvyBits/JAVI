package tk.ivybits.javi.media;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
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
public class Media {
    final AVFormatContext formatContext;
    protected ArrayList<VideoStream> videoStreams = new ArrayList<>();
    protected ArrayList<AudioStream> audioStreams = new ArrayList<>();
    protected ArrayList<Stream> subtitleStreams = new ArrayList<>();

    /**
     * Creates a Media object sourced from a <code>File</code>.
     *
     * @param source The media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @since 1.0
     */
    public Media(File source) throws IOException {
        this(source.getAbsolutePath());
    }

    /**
     * Creates a Media object sourced from a pointing <code>URL</code>.
     *
     * @param source The URL of the media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @since 1.0
     */
    public Media(URI source) throws IOException {
        this(source.toASCIIString());
    }

    private Media(String source) throws IOException {
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
                    videoStreams.add(new VideoStream(this, stream));
                    break;
                case STREAM_AUDIO:
                    audioStreams.add(new AudioStream(this, stream));
                    break;
                case STREAM_SUBTITLE:
                    // subtitleStreams.add(str);
                    break;
            }
        }
    }

    public List<VideoStream> videoStreams() {
        return Collections.unmodifiableList(videoStreams);
    }

    public List<AudioStream> audioStreams() {
        return Collections.unmodifiableList(audioStreams);
    }

    /**
     * Prepares video for streaming.
     *
     * @return A Builder object to configure the way the video will be streamed.
     * @since 1.0
     */
    public MediaStream.Builder stream() {
        return new MediaStream.Builder(this);
    }

    /**
     * Fetches the length of the video.
     *
     * @return The length of the video in milliseconds.
     * @since 1.0
     */
    public long length() {
        if (formatContext.duration == Long.MIN_VALUE)
            return 0;
        return formatContext.duration / 1000;
    }
}
