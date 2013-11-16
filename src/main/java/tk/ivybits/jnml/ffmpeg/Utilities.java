package tk.ivybits.jnml.ffmpeg;

public class Utilities {
    public static final int AV_TIME_BASE = 1000000;

    public static double getSeconds(long ffmpegTime) {
        return ffmpegTime / ((double) AV_TIME_BASE);
    }
}
