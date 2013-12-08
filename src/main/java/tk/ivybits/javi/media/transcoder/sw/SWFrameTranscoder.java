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
import tk.ivybits.javi.ffmpeg.LibAVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVPicture;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.format.PixelFormat;
import tk.ivybits.javi.media.transcoder.Filter;
import tk.ivybits.javi.media.transcoder.FrameTranscoder;
import tk.ivybits.javi.media.transcoder.SafeByteBuffer;

import java.nio.ByteBuffer;
import java.util.List;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_alloc;
import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_fill;
import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_get_size;
import static tk.ivybits.javi.ffmpeg.LibC.memcpy;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWFrameTranscoder extends FrameTranscoder {
    private Pointer swsContext;
    private AVPicture picture = new AVPicture();

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
    public void transcode(Pointer buffers, int[] lineSizes, SafeByteBuffer buffer) {
        avpicture_fill(picture.getPointer(), buffer.pointer(), dstPixelFormat.id, dstWidth, dstHeight);
        picture.read();
        sws_scale(swsContext, buffers, lineSizes, 0, srcHeight, picture.getPointer(), picture.linesize);

        for (Filter f : filters)
            f.apply(buffer.get());
    }
}
