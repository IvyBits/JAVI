package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodec;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodecContext;
import tk.ivybits.jnml.ffmpeg.libavutil.AVFrame;

public interface LibAVCodec extends Library {
    LibAVCodec INSTANCE = (LibAVCodec) Native.loadLibrary("avcodec-55", LibAVCodec.class);

    int avcodec_version();

    AVCodec avcodec_find_decoder(int id);

    void avcodec_register_all();

    int avcodec_open2(Pointer avctx, Pointer codec, Pointer options);

    AVFrame.ByReference avcodec_alloc_frame();

    int avpicture_get_size(int format, int width, int height);

    int avpicture_fill(Pointer picture, Pointer ptr, int pix_fmt, int width, int height);

    int  avcodec_decode_video2(Pointer avctx, Pointer picture, IntByReference frameFinished, Pointer pkt);

    void av_init_packet(Pointer pointer);
}