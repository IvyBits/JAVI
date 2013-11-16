package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVProgram extends PointerType {
    public AVProgram(Pointer address) {
        super(address);
    }
    public AVProgram() {
        super();
    }
} /*extends Structure {
    public static class ByReference extends AVProgram implements Structure.ByReference {}
    public static class ByValue extends AVProgram implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}*/
