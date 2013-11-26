package tk.ivybits.javi.media.stream;

public interface VideoStream extends Stream {
    /**
     * Fetches video stream width.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video width.
     * @since 1.0
     */
    int width();

    /**
     * Fetches video stream height.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video height.
     * @since 1.0
     */
    int height();
}
