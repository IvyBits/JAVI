package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface LibAVCodec extends Library {
    LibAVCodec INSTANCE = (LibAVCodec) Native.loadLibrary("avcodec-55", LibAVCodec.class);
    int avcodec_version();
}
