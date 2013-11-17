package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodec;

public interface LibAVCodec extends Library {
    LibAVCodec INSTANCE = (LibAVCodec) Native.loadLibrary("avcodec-55", LibAVCodec.class);
    int avcodec_version();
    AVCodec avcodec_find_decoder(int id);
    void avcodec_register_all();
}
