package tk.ivybits.jnml.ffmpeg.libavutil;

public interface AVMediaType {
    int AVMEDIA_TYPE_UNKNOWN    = -1;
    int AVMEDIA_TYPE_VIDEO      = 0;
    int AVMEDIA_TYPE_AUDIO      = 1;
    int AVMEDIA_TYPE_DATA       = 2;
    int AVMEDIA_TYPE_SUBTITLE   = 3;
    int AVMEDIA_TYPE_ATTACHMENT = 4;
    int AVMEDIA_TYPE_NB         = 5;
}
