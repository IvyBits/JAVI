package tk.ivybits.javi.media;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;

import javax.sound.sampled.AudioFormat;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;

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
    final AVCodecContext videoContext, audioContext;
    AVStream videoStream, audioStream;
    protected AudioFormat audioFormat;

    /**
     * Creates a Media object sourced from a <code>File</code>.
     *
     * @param source The media source.
     * @throws IOException Thrown if the source could not be opened (or doesn't exist)
     * @version 1.0
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
     * @version 1.0
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
            switch (StreamType.values()[stream.codec.codec_type]) {
                case STREAM_VIDEO:
                    videoStream = stream;
                    break;
                case STREAM_AUDIO:
                    audioStream = stream;
                    break;
            }
        }
        videoContext = videoStream != null ? videoStream.codec : null;
        if (audioStream != null) {
            audioContext = audioStream.codec;
            audioFormat = new AudioFormat(audioStream.codec.sample_rate, 16, audioStream.codec.channels, true, false);
        } else
            audioContext = null;
    }

    /**
     * Checks if this container contains a stream of a given type.
     *
     * @param type The stream type to search for.
     * @return true if a stream does exist, false otherwise.
     * @version 1.0
     * @since 1.0
     */
    public boolean hasStream(StreamType type) {
        switch (type) {
            case STREAM_AUDIO:
                return audioStream != null;
            case STREAM_VIDEO:
                return videoStream != null;
        }
        return false; // Never happens
    }

    /**
     * Fetches the audio stream's format.
     *
     * @return The stream's format.
     * @throws IllegalArgumentException Thrown if the container does not have an audio stream.
     * @version 1.0
     * @since 1.0
     */
    public AudioFormat audioFormat() {
        if (!hasStream(StreamType.STREAM_AUDIO))
            throw new IllegalArgumentException("media does not have audio");
        return audioFormat;
    }

    /**
     * Fetches video stream width.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video width.
     * @version 1.0
     * @since 1.0
     */
    public int width() {
        return videoStream.codec.width;
    }

    /**
     * Fetches video stream height.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video height.
     * @version 1.0
     * @since 1.0
     */
    public int height() {
        return videoStream.codec.height;
    }

    /**
     * Prepares video for streaming.
     *
     * @return A Builder object to configure the way the video will be streamed.
     * @version 1.0
     * @since 1.0
     */
    public MediaStream.Builder stream() {
        return new MediaStream.Builder(this);
    }
}
