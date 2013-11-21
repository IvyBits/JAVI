package tk.ivybits.javi.ffmpeg.avutil;

public class Macros {
    public static int makeBETag(char a, char b, char c, char d) {
        return ((int) a & 0xFF) | (((int) b & 0xFF) << 8) | (((int) c & 0xFF) << 16) | (((int) d & 0xFF) << 24);
    }

    public static int makeBETag(String str) {
        if(str.length() != 4)
            throw new IllegalArgumentException("invalid string length for 32 bit tag");
        return makeBETag(str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3));
    }
}
