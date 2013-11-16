package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVPacketList extends PointerType {
    public AVPacketList(Pointer address) {
        super(address);
    }
    public AVPacketList() {
        super();
    }
}/* extends Structure {
    public static class ByReference extends AVPacketList implements Structure.ByReference {}
    public static class ByValue extends AVPacketList implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}
*/