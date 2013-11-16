package tk.ivybits.jnml.ffmpeg.libavutil;

public class Macros {
    public static int makeBETag(char a, char b, char c, char d) {
        return ((int) a) | (((int) b) << 8) | (((int) b) << 16) | (((int) b) << 24);
    }

    public static int makeBETag(String str) {
        return makeBETag(str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3));
    }
}
