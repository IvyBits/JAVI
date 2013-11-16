package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavformat.AVInputFormat;

public interface LibAVFormat extends Library {
    LibAVFormat INSTANCE = (LibAVFormat) Native.loadLibrary("avformat-55", LibAVFormat.class);

    int avformat_version();

    void av_register_all();
    int avformat_network_init();
    int avformat_network_deinit();

    int avformat_open_input(PointerByReference ps, String filename, AVInputFormat.ByReference fmt, PointerByReference options);
}
