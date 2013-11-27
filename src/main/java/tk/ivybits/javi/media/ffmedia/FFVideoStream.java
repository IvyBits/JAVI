package tk.ivybits.javi.media.ffmedia;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.stream.VideoStream;

/**
 * FFmpeg-backed VideoStream.
 */
public class FFVideoStream extends FFStream implements VideoStream {
    FFVideoStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * {@inheritDoc}
     */
    public int width() {
        return ffstream.codec.width;
    }

    /**
     * {@inheritDoc}
     */
    public int height() {
        return ffstream.codec.height;
    }
}
