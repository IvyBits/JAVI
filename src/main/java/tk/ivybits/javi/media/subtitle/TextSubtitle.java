package tk.ivybits.javi.media.subtitle;

import tk.ivybits.javi.format.SubtitleType;

public class TextSubtitle implements Subtitle {
    public final String text;

    public TextSubtitle(String text) {
        this.text = text;
    }

    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_TEXT;
    }
}
