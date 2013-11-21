package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibSWScale extends Library {
    Pointer sws_getContext(int scrW, int srcH, int scrFormat, int dstW, int dstH, int dstFormat, int flags, Pointer srcFilter, Pointer dstFilter, Pointer param);

    int sws_scale(Pointer c, Pointer[] src, int[] srcStride, int srcSliceY, int srcSliceH, Pointer[] dst, int[] dstStride);
}
