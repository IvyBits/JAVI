package tk.ivybits.jnml.ffmpeg.libavcodec;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class AVCodec extends PointerType {
    public AVCodec(Pointer address) {
        super(address);
    }
    public AVCodec() {
        super();
    }
}/* extends Structure {
    public static class ByReference extends AVCodec implements Structure.ByReference {}
    public static class ByValue extends AVCodec implements Structure.ByValue {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}*/
