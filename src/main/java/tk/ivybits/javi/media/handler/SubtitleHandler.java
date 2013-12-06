package tk.ivybits.javi.media.handler;

import tk.ivybits.javi.media.subtitle.Subtitle;

public abstract class SubtitleHandler {
    public static final SubtitleHandler NO_HANDLER = new SubtitleHandler() {
        @Override
        public void handle(Subtitle subtitle, long start, long end) {
        }
    };

    /**
     * Handles a subtitle.
     *
     * @param subtitle The subtitle.
     * @param start    The timestamp measured from the start of the video indicating when the subtitle should be shown.
     * @param end      The timestamp measured from the start of the video indicating when the subtitle should be removed from screen.
     */
    public abstract void handle(Subtitle subtitle, long start, long end);

    /**
     * Signifies the start of a stream.
     *
     * @since 1.0
     */
    public void start() {

    }

    /**
     * Signifies the end of a stream.
     *
     * @since 1.0
     */
    public void end() {

    }
}
