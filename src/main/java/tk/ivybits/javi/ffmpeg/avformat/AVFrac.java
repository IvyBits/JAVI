package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVFrac extends Structure {
    public static class ByReference extends AVFrac implements Structure.ByReference {
    }

    public long val;
    public long num;
    public long den;

    public AVFrac() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("val", "num", "den");
    }
}
