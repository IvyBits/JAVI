package tk.ivybits.jnml;

import tk.ivybits.jnml.ffmpeg.LibAVCodec;
import tk.ivybits.jnml.ffmpeg.LibAVFormat;
import tk.ivybits.jnml.ffmpeg.LibAVUtil;


public class FFmpeg {
    public static final LibAVCodec avcodec = LibAVCodec.INSTANCE;
    public static final LibAVFormat avformat = LibAVFormat.INSTANCE;
    public static final LibAVUtil avutil = LibAVUtil.INSTANCE;

    static {
        // Register all codecs.
        avformat.av_register_all();
        avcodec.avcodec_register_all();
    }
}
