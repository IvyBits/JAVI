package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * Tudor
 * 2013-11-17
 */
public interface LibAVUtil extends Library {
    LibAVUtil INSTANCE = (LibAVUtil) Native.loadLibrary("avutil-52", LibAVUtil.class);

    int avutil_version();

    Pointer av_malloc(int size);
}
