package tk.ivybits.jnml.ffmpeg.libavutil;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class AVRational extends PointerType {
    public AVRational(Pointer address) {
        super(address);
    }
    public AVRational() {
        super();
    }
}