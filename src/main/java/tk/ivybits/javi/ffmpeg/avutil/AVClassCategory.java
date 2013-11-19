package tk.ivybits.javi.ffmpeg.avutil;

public interface AVClassCategory {
    int AV_CLASS_CATEGORY_NA               = 1;
    int AV_CLASS_CATEGORY_INPUT            = 2;
    int AV_CLASS_CATEGORY_OUTPUT           = 3;
    int AV_CLASS_CATEGORY_MUXER            = 4;
    int AV_CLASS_CATEGORY_DEMUXER          = 5;
    int AV_CLASS_CATEGORY_ENCODER          = 6;
    int AV_CLASS_CATEGORY_DECODER          = 7;
    int AV_CLASS_CATEGORY_FILTER           = 8;
    int AV_CLASS_CATEGORY_BITSTREAM_FILTER = 9;
    int AV_CLASS_CATEGORY_SWSCALER         = 10;
    int AV_CLASS_CATEGORY_SWRESAMPLER      = 11;
    int AV_CLASS_CATEGORY_NB               = 12;
}
