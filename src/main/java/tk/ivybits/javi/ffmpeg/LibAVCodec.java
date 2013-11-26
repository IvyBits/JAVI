package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
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

    static {
        FFmpeg.ensureInitialized();
        Native.register("avcodec-55");
    }
}