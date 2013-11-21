package tk.ivybits.javi;

import com.sun.jna.Native;
import tk.ivybits.javi.api.log.LogLevel;
import tk.ivybits.javi.ffmpeg.*;

public class FFmpeg {
    public static final LibAVCodec avcodec = (LibAVCodec) Native.loadLibrary("avcodec-55", LibAVCodec.class);
    public static final LibAVFormat avformat = (LibAVFormat) Native.loadLibrary("avformat-55", LibAVFormat.class);
    public static final LibAVUtil avutil = (LibAVUtil) Native.loadLibrary("avutil-52", LibAVUtil.class);
    public static final LibSWScale swscale = (LibSWScale) Native.loadLibrary("swscale-2", LibSWScale.class);
    public static final LibSWResample swresample = (LibSWResample) Native.loadLibrary("swresample-0", LibSWResample.class);

    public static void setLogLevel(LogLevel level) {
        avutil.av_log_set_level((level.ordinal() * 8 - 8));
    }

    public static LogLevel getLogLevel() {
        return LogLevel.values()[avutil.av_log_get_level() / 8 + 1];
    }

    static {
        // Register all codecs.
        avformat.av_register_all();
        avcodec.avcodec_register_all();
    }
}
