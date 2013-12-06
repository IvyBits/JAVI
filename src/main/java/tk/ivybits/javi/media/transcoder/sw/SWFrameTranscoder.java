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

package tk.ivybits.javi.media.transcoder.sw;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import tk.ivybits.javi.format.PixelFormat;
import tk.ivybits.javi.media.transcoder.Filter;
import tk.ivybits.javi.media.transcoder.FrameTranscoder;

import java.nio.ByteBuffer;
import java.util.List;

import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWFrameTranscoder extends FrameTranscoder {
    private Pointer swsContext;

    public SWFrameTranscoder(int srcWidth, int srcHeight, PixelFormat srcPixelFormat,
                             int dstWidth, int dstHeight, PixelFormat dstPixelFormat,
                             List<Filter> filter) {
        super(srcWidth, srcHeight, srcPixelFormat, dstWidth, dstHeight, dstPixelFormat, filter);
        swsContext = sws_getContext(
                srcWidth, srcHeight, srcPixelFormat.id,
                dstWidth, dstHeight, dstPixelFormat.id,
                0, null, null, null);
    }

    @Override
    public ByteBuffer transcode(ByteBuffer buffer) {
        Pointer ptr = Native.getDirectBufferPointer(buffer);
        sws_scale(swsContext, ptr,
                new int[]{srcWidth * srcPixelFormat.bpp(), 0, 0, 0, 0, 0, 0, 0},
                0, srcHeight, ptr,
                new int[]{dstWidth * dstPixelFormat.bpp(), 0, 0, 0, 0, 0, 0, 0});

        for (Filter f : filters)
            f.apply(buffer);
    }
}
