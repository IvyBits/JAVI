package tk.ivybits.javi.media.subtitle;

import tk.ivybits.javi.format.SubtitleType;

public class DonkeySubtitle implements Subtitle {
    public final DonkeyParser parser;
    public final DonkeyParser.Style style;
    public final long start;
    public final long end;
    public final String line;

    public DonkeySubtitle(DonkeyParser parser, DonkeyParser.Style style, long start, long end, String line) {
        this.parser = parser;
        this.style = style;
        this.start = start;
        this.end = end;
        this.line = line;
    }

    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_DONKEY;
    }
}
