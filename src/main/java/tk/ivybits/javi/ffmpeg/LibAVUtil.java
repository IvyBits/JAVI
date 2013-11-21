package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibAVUtil extends Library {
    int avutil_version();

    Pointer av_malloc(int size);

    void av_free(Pointer ram);

    int av_log_get_level();

    void av_log_set_level(int level);

    int av_samples_get_buffer_size(IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);

    int av_samples_alloc_array_and_samples(PointerByReference audio_data, IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);
}
