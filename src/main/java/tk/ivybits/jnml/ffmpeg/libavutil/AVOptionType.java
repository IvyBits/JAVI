package tk.ivybits.jnml.ffmpeg.libavutil;

import static tk.ivybits.jnml.ffmpeg.libavutil.Macros.*;

public class AVOptionType {
    public static final int AV_OPT_TYPE_FLAGS = 0;
    public static final int AV_OPT_TYPE_INT = 1;
    public static final int AV_OPT_TYPE_INT64 = 2;
    public static final int AV_OPT_TYPE_DOUBLE = 3;
    public static final int AV_OPT_TYPE_FLOAT = 4;
    public static final int AV_OPT_TYPE_STRING = 5;
    public static final int AV_OPT_TYPE_RATIONAL = 6;
    public static final int AV_OPT_TYPE_BINARY = 7;
    public static final int AV_OPT_TYPE_CONST = 128;
    public static final int AV_OPT_TYPE_IMAGE_SIZE     = makeBETag("SIZE");
    public static final int AV_OPT_TYPE_PIXEL_FMT      = makeBETag("PFMT");
    public static final int AV_OPT_TYPE_SAMPLE_FMT     = makeBETag("SFMT");
    public static final int AV_OPT_TYPE_VIDEO_RATE     = makeBETag("VRAT");
    public static final int AV_OPT_TYPE_DURATION       = makeBETag("DUR ");
    public static final int AV_OPT_TYPE_COLOR          = makeBETag("COLR");
    public static final int AV_OPT_TYPE_CHANNEL_LAYOUT = makeBETag("CHLA");
}