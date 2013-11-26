package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVSubtitle extends Structure {
    public short format;
    public int start_display_time;
    public int end_display_time;
    public int num_rects;
    public Pointer rects;
    public long pts;

    public AVSubtitle(Pointer address) {
        super(address);
        read();
    }

    public AVSubtitle() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("format", "start_display_time", "end_display_time", "num_rects", "rects", "pts");
    }
}
