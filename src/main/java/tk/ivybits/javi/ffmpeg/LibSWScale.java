package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

public class LibSWScale {
    public static native Pointer sws_getContext(int scrW, int srcH, int scrFormat, int dstW, int dstH, int dstFormat, int flags, Pointer srcFilter, Pointer dstFilter, Pointer param);

    public static native int sws_scale(Pointer c, Pointer src, int[] srcStride, int srcSliceY, int srcSliceH, Pointer dst, int[] dstStride);

    static {
        FFmpeg.ensureInitialized();
        Native.register("swscale-2");
    }
}
