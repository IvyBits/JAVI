package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class LibSWResample {
    public static native Pointer swr_alloc();

    public static native int swr_init(Pointer s);

    public static native Pointer swr_alloc_set_opts(Pointer s, long out_ch_layout, int out_sample_fmt, int out_sample_rate, long in_ch_layout, int in_sample_format, int in_sample_rate, int log_offset, Pointer log_ctx);

    public static native void swr_free(PointerByReference s);

    public static native int swr_convert(Pointer s, Pointer out, int out_count, Pointer in, int in_count);

    static {
        FFmpeg.ensureInitialized();
        Native.register("swresample-0");
    }
}
