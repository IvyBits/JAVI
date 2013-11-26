package tk.ivybits.javi.ffmpeg;

import static tk.ivybits.javi.debug.Utilities.getVersion;

public final class FFmpeg {
    private FFmpeg() {
        throw new AssertionError();
    }

    public static final String AVCODEC_VERSION = getVersion(LibAVCodec.avcodec_version());
    public static final String AVFORMAT_VERSION = getVersion(LibAVFormat.avformat_version());
    public static final String AVUTIL_VERSION = getVersion(LibAVUtil.avutil_version());

    static void ensureInitialized() {
        // Ensures one call to static block
    }

    public static void release() {
        LibAVFormat.avformat_network_deinit();
    }

    static {
        LibAVFormat.avformat_network_init();
        LibAVFormat.av_register_all();
        LibAVCodec.avcodec_register_all();
    }
}
