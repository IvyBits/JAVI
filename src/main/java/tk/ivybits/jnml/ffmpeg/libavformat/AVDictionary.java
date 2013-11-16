package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVDictionary extends PointerType {
    public AVDictionary(Pointer address) {
        super(address);
    }
    public AVDictionary() {
        super();
    }
}/* extends Structure {
    public static class ByReference extends AVDictionary implements Structure.ByReference {}
    public static class ByValue extends AVDictionary implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}
*/