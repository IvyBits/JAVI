package tk.ivybits.jnml.ffmpeg.libavutil;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

public class AVDictionary extends PointerType {
    public AVDictionary(Pointer address) {
        super(address);
    }

    public AVDictionary() {
        super();
    }
}