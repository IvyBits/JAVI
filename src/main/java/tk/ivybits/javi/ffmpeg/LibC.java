package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;

public class LibC {
    public static native Pointer memcpy(Pointer destination, Pointer source, long num);

    static {
        Native.register(Platform.isWindows() ? "msvcrt" : "c");
    }
}
