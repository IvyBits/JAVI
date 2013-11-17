package tk.ivybits.jnml.ffmpeg.libavutil;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVRational extends Structure {
    public static class ByReference extends AVRational implements Structure.ByReference {
    }

    public static class ByValue extends AVRational implements Structure.ByValue {
    }

    public int num;
    public int den;

    public AVRational() {
        super();
    }

    public AVRational(Pointer address) {
        super(address);
        read();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("num", "den");
    }
}