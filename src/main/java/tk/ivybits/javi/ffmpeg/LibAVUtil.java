/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.JAVI;
import tk.ivybits.javi.ffmpeg.avutil.AVDictionary;

public class LibAVUtil {
    public static native int avutil_version();

    public static native Pointer av_malloc(int size);

    public static native void av_free(Pointer ram);
    public static native void av_freep(PointerByReference ram);

    public static native int av_log_get_level();

    public static native void av_log_set_level(int level);

    public static native int av_samples_get_buffer_size(IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);

    public static native int av_samples_alloc_array_and_samples(PointerByReference audio_data, IntByReference linesize, int nb_channels, int nb_samples, int sample_fmt, int align);

    public static native AVDictionary.Entry av_dict_get(AVDictionary m, String key, AVDictionary.Entry prev, int flags);

    public static native Pointer av_pix_fmt_desc_get(int pix_fmt);

    public static native int av_get_bits_per_pixel(Pointer pixdesc);

    public static native int av_get_padded_bits_per_pixel(Pointer pixdesc);

    public static native int av_image_fill_linesizes(int[] linesizes, int pix_fmt, int width);

    static {
        JAVI.initialize();
        Native.register(Natives.getPath("avutil-52").getAbsolutePath());
    }
}
