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
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.JAVI;

public class LibSWResample {
    public static native Pointer swr_alloc();

    public static native int swr_init(Pointer s);

    public static native Pointer swr_alloc_set_opts(Pointer s, long out_ch_layout, int out_sample_fmt, int out_sample_rate, long in_ch_layout, int in_sample_format, int in_sample_rate, int log_offset, Pointer log_ctx);

    public static native void swr_free(PointerByReference s);

    public static native int swr_convert(Pointer s, Pointer out, int out_count, Pointer in, int in_count);

    static {
        JAVI.initialize();
        Native.register(Natives.getPath("swresample-0").getAbsolutePath());
    }
}
