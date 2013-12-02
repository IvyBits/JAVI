/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import tk.ivybits.javi.JAVI;

public class LibSWScale {
    public static native Pointer sws_getContext(int scrW, int srcH, int scrFormat, int dstW, int dstH, int dstFormat, int flags, Pointer srcFilter, Pointer dstFilter, Pointer param);

    public static native int sws_scale(Pointer c, Pointer src, int[] srcStride, int srcSliceY, int srcSliceH, Pointer dst, int[] dstStride);

    public static native void sws_freeContext(Pointer swsContext);

    static {
        JAVI.initialize();
        Native.register("swscale-2");
    }
}
