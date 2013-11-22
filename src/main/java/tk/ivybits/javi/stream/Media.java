package tk.ivybits.javi.stream;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;

import javax.sound.sampled.AudioFormat;
import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;

public class Media implements Closeable {
    final AVFormatContext formatContext;
    final AVCodecContext videoContext, audioContext;
    AVStream videoStream, audioStream;
    protected AudioFormat audioFormat;

    public Media(File source) throws IOException {
        this(source.getAbsolutePath());
    }

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

    public boolean hasStream(StreamType type) {
        switch (type) {
            case STREAM_AUDIO:
                return audioStream != null;
            case STREAM_VIDEO:
                return videoStream != null;
        }
        return false; // Never happens
    }

    public AudioFormat audioFormat() {
        if (!hasStream(StreamType.STREAM_AUDIO))
            throw new IllegalArgumentException("media does not have audio");
        return audioFormat;
    }

    public Dimension dimensions() {
        return new Dimension(videoStream.codec.width, videoStream.codec.height);
    }

    public int width() {
        return videoStream.codec.width;
    }

    public int height() {
        return videoStream.codec.height;
    }

    public MediaStream.Builder stream() {
        return new MediaStream.Builder(this);
    }

    @Override
    public void close() {

    }
}
