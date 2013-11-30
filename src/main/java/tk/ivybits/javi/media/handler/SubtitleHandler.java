package tk.ivybits.javi.media.handler;

import tk.ivybits.javi.media.subtitle.Subtitle;

public abstract class SubtitleHandler {
    public abstract void handle(Subtitle subtitle, long start, long end);

    public void end() {

    }
}
