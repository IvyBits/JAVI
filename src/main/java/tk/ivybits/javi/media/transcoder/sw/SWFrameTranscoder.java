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
import tk.ivybits.javi.ffmpeg.avcodec.AVPicture;
import tk.ivybits.javi.format.PixelFormat;
import tk.ivybits.javi.media.stream.Frame;
import tk.ivybits.javi.media.transcoder.Filter;
import tk.ivybits.javi.media.transcoder.Transcoder;

import java.nio.ByteBuffer;
import java.util.List;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_fill;
import static tk.ivybits.javi.ffmpeg.LibAVCodec.avpicture_get_size;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_free;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_malloc;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWFrameTranscoder implements Transcoder {
    private Pointer swsContext;
    private AVPicture dstPicture = new AVPicture();
    private ByteBuffer destination;
    private Pointer pDestination;
    protected final int srcWidth;
    protected final int srcHeight;
    protected final PixelFormat srcPixelFormat;
    protected final int dstWidth;
    protected final int dstHeight;
    protected final PixelFormat dstPixelFormat;
    protected final List<Filter> filters;

    public SWFrameTranscoder(int srcWidth, int srcHeight, PixelFormat srcPixelFormat,
                             int dstWidth, int dstHeight, PixelFormat dstPixelFormat,
                             List<Filter> filters) {
        this.srcWidth = srcWidth;
        this.srcHeight = srcHeight;
        this.srcPixelFormat = srcPixelFormat;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;
        this.dstPixelFormat = dstPixelFormat;
        this.filters = filters;

        swsContext = sws_getContext(
                srcWidth, srcHeight, srcPixelFormat.id,
                dstWidth, dstHeight, dstPixelFormat.id,
                0, null, null, null);
        destination = ByteBuffer.allocateDirect(avpicture_get_size(dstPixelFormat.id, dstWidth, dstHeight));
        pDestination = Native.getDirectBufferPointer(destination);
    }

    @Override
    public Frame transcode(Frame buffer) {
        destination.position(0);
        avpicture_fill(dstPicture.getPointer(), pDestination, dstPixelFormat.id, dstWidth, dstHeight);
        dstPicture.read();

        Pointer pointers = av_malloc(Native.POINTER_SIZE * 8);
        pointers.clear(Native.POINTER_SIZE * 8);
        int[] lineSizes = new int[8];

        int i = 0;
        for (Frame.Plane plane : buffer) {
            pointers.setPointer(Native.POINTER_SIZE * i, Native.getDirectBufferPointer(plane.buffer()));
            lineSizes[i++] = plane.linesize();
        }

        sws_scale(swsContext, pointers, lineSizes, 0, srcHeight, dstPicture.getPointer(), dstPicture.linesize);
        av_free(pointers);

        i = 0;
        for (; i < dstPicture.linesize.length && dstPicture.linesize[i] != 0; ++i) ;
        Frame.Plane[] planes = new Frame.Plane[i];
        for (int p = 0; p != i; p++) {
            int l = dstPicture.linesize[p];
            planes[p] = new Frame.Plane(Native.getDirectByteBuffer(Pointer.nativeValue(dstPicture.data[p]), l * dstHeight), l);
        }
        Frame result = new Frame(planes);
        for (Filter f : filters)
            f.apply(result);
        return result;
    }

    @Override
    public void close() {

    }
}
