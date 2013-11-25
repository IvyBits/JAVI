package tk.ivybits.javi.ffmpeg.avutil;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVRational extends Structure {
    public static class ByReference extends AVRational implements Structure.ByReference {
    }

    public int num;
    public int den;

    public AVRational() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("num", "den");
    }
}