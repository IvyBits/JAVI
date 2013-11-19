package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.LibAVCodec;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.LibAVUtil;
import tk.ivybits.javi.ffmpeg.LibSWScale;

public class FFmpeg {
    public static final LibAVCodec avcodec = LibAVCodec.INSTANCE;
    public static final LibAVFormat avformat = LibAVFormat.INSTANCE;
    public static final LibAVUtil avutil = LibAVUtil.INSTANCE;
    public static final LibSWScale swscale = LibSWScale.INSTANCE;

    static {
        // Register all codecs.
        avformat.av_register_all();
        avcodec.avcodec_register_all();
    }
}
