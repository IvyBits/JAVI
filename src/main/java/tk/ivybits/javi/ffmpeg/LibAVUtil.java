package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class LibAVUtil {
    public static native int avutil_version();

    public static native Pointer av_malloc(int size);

    public static native void av_free(Pointer ram);

    public static native int av_log_get_level();

    public static native void av_log_set_level(int level);

    public static native int av_samples_get_buffer_size(IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);

    public static native int av_samples_alloc_array_and_samples(PointerByReference audio_data, IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);

    static {
        FFmpeg.ensureInitialized();
        Native.register("avutil-52");
    }
}
