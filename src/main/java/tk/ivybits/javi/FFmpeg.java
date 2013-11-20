package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.*;

public class FFmpeg {
    public static final LibAVCodec avcodec = LibAVCodec.INSTANCE;
    public static final LibAVFormat avformat = LibAVFormat.INSTANCE;
    public static final LibAVUtil avutil = LibAVUtil.INSTANCE;
    public static final LibSWScale swscale = LibSWScale.INSTANCE;
    public static final LibSWResample swresample = LibSWResample.INSTANCE;

    static {
        // Register all codecs.
        avformat.av_register_all();
        avcodec.avcodec_register_all();
    }
}
