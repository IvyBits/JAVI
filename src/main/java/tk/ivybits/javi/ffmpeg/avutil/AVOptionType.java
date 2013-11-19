package tk.ivybits.javi.ffmpeg.avutil;

import static tk.ivybits.javi.ffmpeg.avutil.Macros.*;

public interface AVOptionType {
    int AV_OPT_TYPE_FLAGS = 0;
    int AV_OPT_TYPE_INT = 1;
    int AV_OPT_TYPE_INT64 = 2;
    int AV_OPT_TYPE_DOUBLE = 3;
    int AV_OPT_TYPE_FLOAT = 4;
    int AV_OPT_TYPE_STRING = 5;
    int AV_OPT_TYPE_RATIONAL = 6;
    int AV_OPT_TYPE_BINARY = 7;
    int AV_OPT_TYPE_CONST = 128;
    int AV_OPT_TYPE_IMAGE_SIZE     = makeBETag("SIZE");
    int AV_OPT_TYPE_PIXEL_FMT      = makeBETag("PFMT");
    int AV_OPT_TYPE_SAMPLE_FMT     = makeBETag("SFMT");
    int AV_OPT_TYPE_VIDEO_RATE     = makeBETag("VRAT");
    int AV_OPT_TYPE_DURATION       = makeBETag("DUR ");
    int AV_OPT_TYPE_COLOR          = makeBETag("COLR");
    int AV_OPT_TYPE_CHANNEL_LAYOUT = makeBETag("CHLA");
}