package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibSWResample extends Library {
    Pointer swr_alloc();

    int swr_init(Pointer s);

    Pointer swr_alloc_set_opts(Pointer s, long out_ch_layout, int out_sample_fmt, int out_sample_rate, long in_ch_layout, int in_sample_format, int in_sample_rate, int log_offset, Pointer log_ctx);

    void swr_free(PointerByReference s);

    int swr_convert(Pointer s, Pointer out, int out_count, Pointer in, int in_count);
}
