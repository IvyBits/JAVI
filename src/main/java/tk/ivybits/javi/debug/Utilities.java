package tk.ivybits.javi.debug;

public class Utilities {
    public static final int AV_TIME_BASE = 1000000;

    public static double getSeconds(long ffmpegTime) {
        if (ffmpegTime == Long.MIN_VALUE)
            return 0;
        return ffmpegTime / ((double) AV_TIME_BASE);
    }

    public static String getVersion(int version) {
        return String.format("%d.%d.%d", version >> 16, (version >> 8) & 0xFF, version & 0xFF);
    }
}
