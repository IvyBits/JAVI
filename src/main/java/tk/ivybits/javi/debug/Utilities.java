package tk.ivybits.javi.debug;

public class Utilities {
    public static final int AV_TIME_BASE = 1000000;

    public static double getSeconds(long ffmpegTime) {
        if (ffmpegTime == Long.MIN_VALUE)
            return 0;
        return ffmpegTime / ((double) AV_TIME_BASE);
    }
}
