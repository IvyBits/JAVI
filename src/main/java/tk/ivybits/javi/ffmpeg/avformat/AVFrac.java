package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * Tudor
 * 16/11/13
 */
public class AVFrac extends Structure {
    public static class ByReference extends AVFrac implements Structure.ByReference {
    }

    public static class ByValue extends AVFrac implements Structure.ByValue {
    }

    public long val;
    public long num;
    public long den;

    public AVFrac(Pointer address) {
        super(address);
        read();
    }

    public AVFrac() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("val", "num", "den");
    }
}
