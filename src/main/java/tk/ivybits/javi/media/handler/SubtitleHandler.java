package tk.ivybits.javi.media.handler;

import tk.ivybits.javi.media.subtitle.Subtitle;

public abstract class SubtitleHandler {
    public static final SubtitleHandler NO_HANDLER = new SubtitleHandler() {
        @Override
        public void handle(Subtitle subtitle, long start, long end) {
        }
    };

    public abstract void handle(Subtitle subtitle, long start, long end);

    public void end() {

    }

    public void start() {

    }
}
