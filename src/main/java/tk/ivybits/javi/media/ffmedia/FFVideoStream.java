package tk.ivybits.javi.media.ffmedia;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.stream.VideoStream;

public class FFVideoStream extends FFStream implements VideoStream {
    FFVideoStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * Fetches video stream width.
     * <p/>
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video width.
     * @since 1.0
     */
    public int width() {
        return ffstream.codec.width;
    }

    /**
     * Fetches video stream height.
     * <p/>
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video height.
     * @since 1.0
     */
    public int height() {
        return ffstream.codec.height;
    }
}
