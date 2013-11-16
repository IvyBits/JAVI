package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVChapter extends PointerType {
    public AVChapter(Pointer address) {
        super(address);
    }
    public AVChapter() {
        super();
    }
}/* extends Structure {
    public static class ByReference extends AVChapter implements Structure.ByReference {}
    public static class ByValue extends AVChapter implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}*/
