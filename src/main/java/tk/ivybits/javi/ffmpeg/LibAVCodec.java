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
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;

public class LibAVCodec {
    public static native int avcodec_version();

    public static native AVCodec avcodec_find_decoder(int id);

    public static native void avcodec_register_all();

    public static native int avcodec_open2(Pointer avctx, Pointer codec, Pointer options);

    public static native AVFrame.ByReference avcodec_alloc_frame();

    public static native int avpicture_get_size(int format, int width, int height);

    public static native int avpicture_fill(Pointer picture, Pointer ptr, int pix_fmt, int width, int height);

    public static native int avcodec_decode_video2(Pointer avctx, Pointer picture, IntByReference frameFinished, Pointer pkt);

    public static native int avcodec_decode_audio4(Pointer avctx, Pointer frame, IntByReference frameFinished, Pointer pkt);

    public static native int avcodec_decode_subtitle2(Pointer avctx, Pointer sub, IntByReference got_sub_ptr, Pointer pkt);

    public static native void av_init_packet(Pointer pointer);

    public static native void av_free_packet(Pointer pointer);

    public static native int avcodec_close(Pointer avctx);

    public static native void avcodec_free_frame(PointerByReference frame);

    public static native void avsubtitle_free(Pointer sub);

    public static native int avpicture_alloc(Pointer picture, int pix_fmt, int width, int height);

    public static native void avpicture_free(Pointer picture);

    static {
        JAVI.initialize();
        Native.register("avcodec-55");
    }
}