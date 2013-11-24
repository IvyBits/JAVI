package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avformat.AVInputFormat;

public class LibAVFormat {
    public static native int avformat_version();

    public static native void av_register_all();

    public static native int avformat_network_init();

    public static native int avformat_network_deinit();

    public static native int avformat_open_input(PointerByReference ps, String filename, AVInputFormat.ByReference fmt, PointerByReference options);

    public static native int av_find_stream_info(Pointer ps);

    public static native void av_dump_format(Pointer ps, int index, String url, int is_output);

    public static native int av_read_frame(Pointer s, Pointer pkt);

    public static native int av_seek_frame(Pointer s, int stream_index, long timestamp, int flags);

    static {
        Native.register("avformat-55");
    }
}