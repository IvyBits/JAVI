package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVStream extends PointerType {
    public AVStream(Pointer address) {
        super(address);
    }
    public AVStream() {
        super();
    }
}/* extends Structure {
    public static class ByReference extends AVStream implements Structure.ByReference {}
    public static class ByValue extends AVStream implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}
*/