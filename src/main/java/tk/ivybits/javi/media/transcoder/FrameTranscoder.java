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

package tk.ivybits.javi.media.transcoder;

import tk.ivybits.javi.format.PixelFormat;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class FrameTranscoder {
    protected final int srcWidth;
    protected final int srcHeight;
    protected final PixelFormat srcPixelFormat;
    protected final int dstWidth;
    protected final int dstHeight;
    protected final PixelFormat dstPixelFormat;
    protected final List<Filter> filters;

    public FrameTranscoder(int srcWidth, int srcHeight, PixelFormat srcPixelFormat,
                           int dstWidth, int dstHeight, PixelFormat dstPixelFormat,
                           List<Filter> filters) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.srcPixelFormat = srcPixelFormat;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;
        this.dstPixelFormat = dstPixelFormat;
        this.filters = filters;
    }

    public abstract ByteBuffer transcode(ByteBuffer buffer);
}
