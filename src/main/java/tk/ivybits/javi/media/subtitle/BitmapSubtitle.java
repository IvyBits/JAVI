package tk.ivybits.javi.media.subtitle;

import tk.ivybits.javi.format.SubtitleType;

import java.awt.image.BufferedImage;

public class BitmapSubtitle implements Subtitle {
    public final int x;
    public final int y;
    public final int w;
    public final int h;
    public final BufferedImage image;

    public BitmapSubtitle(int x, int y, int w, int h, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.image = image;
    }

    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_BITMAP;
    }
}
