package tk.ivybits.jnml;

import tk.ivybits.jnml.ffmpeg.LibAVCodec;
import tk.ivybits.jnml.ffmpeg.LibAVFormat;


public class FFmpeg {
    static LibAVCodec libavcodec = LibAVCodec.INSTANCE;
    static LibAVFormat libavformat = LibAVFormat.INSTANCE;

    static {
        // Register all codecs.
        libavformat.av_register_all();
        libavcodec.avcodec_register_all();
    }
}
