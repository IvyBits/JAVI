package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVSubtitleRect extends Structure {
    public int x;
    public int y;
    public int w;
    public int h;
    public int nb_colors;
    public AVPicture pict;
    public int type;
    public Pointer text;
    public Pointer ass; // In places not needing to be C compatible, it's called donkey
    public int flags;

    public AVSubtitleRect(Pointer address) {
        super(address);
        read();
    }

    public AVSubtitleRect() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("x", "y", "w", "h", "nb_colors", "pict", "type", "text", "ass", "flags");
    }
}
