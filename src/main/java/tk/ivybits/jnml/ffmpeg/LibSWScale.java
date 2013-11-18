package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibSWScale extends Library {
    LibSWScale INSTANCE = (LibSWScale) Native.loadLibrary("swscale-2", LibSWScale.class);

    Pointer sws_getContext(int scrW, int srcH, int scrFormat, int dstW, int dstH, int dstFormat, int flags, Pointer srcFilter, Pointer dstFilter, Pointer param);

    int sws_scale(Pointer c, byte[] src, int[] srcStride, int srcSliceY, int srcSliceH, byte[] dst, int[] dstStride);
}
