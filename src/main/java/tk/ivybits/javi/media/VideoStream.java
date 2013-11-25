package tk.ivybits.javi.media;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;

public class VideoStream extends Stream {
    VideoStream(Media container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * Fetches video stream width.
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
