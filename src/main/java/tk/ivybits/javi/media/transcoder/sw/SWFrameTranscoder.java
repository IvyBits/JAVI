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
import java.util.Arrays;
import java.util.List;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_alloc;
import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_fill;
import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_get_size;
import static tk.ivybits.javi.ffmpeg.LibC.memcpy;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.*;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWFrameTranscoder extends FrameTranscoder {
    private Pointer swsContext;
    private AVPicture dstPicture = new AVPicture(), srcPicture = new AVPicture();
    private ByteBuffer destination;
    private Pointer pDestination;
    private int[] lineSizes = new int[4];

    public SWFrameTranscoder(int srcWidth, int srcHeight, PixelFormat srcPixelFormat,
                             int dstWidth, int dstHeight, PixelFormat dstPixelFormat,
                             List<Filter> filter) {
        super(srcWidth, srcHeight, srcPixelFormat, dstWidth, dstHeight, dstPixelFormat, filter);
        swsContext = sws_getContext(
                srcWidth, srcHeight, srcPixelFormat.id,
                dstWidth, dstHeight, dstPixelFormat.id,
                0, null, null, null);
        destination = ByteBuffer.allocateDirect((int) getBufferSize());
        pDestination = Native.getDirectBufferPointer(destination);
        av_image_fill_linesizes(lineSizes, dstPixelFormat.id, dstWidth);
        System.out.println(Arrays.toString(lineSizes));
    }

    @Override
    public ByteBuffer transcode(ByteBuffer buffer) {
        destination.position(0);
        avpicture_fill(dstPicture.getPointer(), Native.getDirectBufferPointer(destination), dstPixelFormat.id, dstWidth, dstHeight);
        dstPicture.read();
        avpicture_fill(srcPicture.getPointer(), Native.getDirectBufferPointer(buffer), srcPixelFormat.id, srcWidth, srcHeight);
        srcPicture.read();
        System.out.println("<<<"+Arrays.toString(srcPicture.linesize));
        sws_scale(swsContext, srcPicture.getPointer(), srcPicture.linesize, 0, srcHeight, dstPicture.getPointer(), dstPicture.linesize);

        for (Filter f : filters)
            f.apply(destination);
        return destination;
    }
    /*
        public void transcode(Pointer buffers, int[] lineSizes, SafeByteBuffer buffer) {
        avpicture_fill(picture.getPointer(), buffer.pointer(), dstPixelFormat.id, dstWidth, dstHeight);
        picture.read();
        sws_scale(swsContext, buffers, lineSizes, 0, srcHeight, picture.getPointer(), picture.linesize);

        for (Filter f : filters)
            f.apply(buffer.get());
    }
     */

    @Override
    public long getBufferSize() {
        return avpicture_get_size(dstPixelFormat.id, dstWidth, dstHeight);
    }
}
