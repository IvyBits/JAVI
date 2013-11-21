package tk.ivybits.javi;

import com.sun.jna.Native;
import tk.ivybits.javi.ffmpeg.*;

public class FFmpeg {
    public static final LibAVCodec avcodec = (LibAVCodec) Native.loadLibrary("avcodec-55", LibAVCodec.class);
    public static final LibAVFormat avformat = (LibAVFormat) Native.loadLibrary("avformat-55", LibAVFormat.class);
    public static final LibAVUtil avutil = (LibAVUtil) Native.loadLibrary("avutil-52", LibAVUtil.class);
    public static final LibSWScale swscale = (LibSWScale) Native.loadLibrary("swscale-2", LibSWScale.class);
    public static final LibSWResample swresample = (LibSWResample) Native.loadLibrary("swresample-0", LibSWResample.class);

    static {
        // Register all codecs.
        avformat.av_register_all();
        avcodec.avcodec_register_all();
    }
}
