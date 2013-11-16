package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVIOContext extends PointerType {
    public AVIOContext(Pointer address) {
        super(address);
    }
    public AVIOContext() {
        super();
    }
} /*extends Structure {
    public static class ByReference extends AVIOContext implements Structure.ByReference {}
    public static class ByValue extends AVIOContext implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}
*/