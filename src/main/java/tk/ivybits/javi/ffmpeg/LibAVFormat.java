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

public class LibAVFormat {
    public static native int avformat_version();

    public static native void av_register_all();

    public static native int avformat_network_init();

    public static native int avformat_network_deinit();

    public static native int avformat_open_input(PointerByReference ps, String filename, Pointer fmt, PointerByReference options);

    public static native int av_find_stream_info(Pointer ps);

    public static native void av_dump_format(Pointer ps, int index, String url, int is_output);

    public static native int av_read_frame(Pointer s, Pointer pkt);

    public static native int av_seek_frame(Pointer s, int stream_index, long timestamp, int flags);

    public static native void avformat_close_input(PointerByReference s);

    static {
        FFmpeg.ensureInitialized();
        Native.register("avformat-55");
    }
}