package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibAVUtil extends Library {
    LibAVUtil INSTANCE = (LibAVUtil) Native.loadLibrary("avutil-52", LibAVUtil.class);

    int avutil_version();

    Pointer av_malloc(int size);

    void av_free(Pointer ram);

    int av_log_get_level();

    void av_log_set_level(int level);

    int av_samples_get_buffer_size(Pointer linesize, int nb_channels, int nb_samples, int sample_fmt, int align);
}
