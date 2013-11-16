package tk.ivybits.jnml.ffmpeg.libavutil;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.List;

public class AVDictionary extends PointerType {
    public AVDictionary(Pointer address) {
        super(address);
    }

    public AVDictionary() {
        super();
    }
}