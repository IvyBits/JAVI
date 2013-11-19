package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVIOInterruptCB extends Structure {
    public static class ByReference extends AVIOInterruptCB implements Structure.ByReference {
    }

    public static class ByValue extends AVIOInterruptCB implements Structure.ByValue {
    }

    public AVIOInterruptCB.AVIOInterrupt callback;
    public Pointer opaque;

    public interface AVIOInterrupt extends Callback {
        int apply(Pointer voidPtr1);
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("callback", "opaque");
    }
}
