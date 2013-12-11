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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.format.SampleFormat;
import tk.ivybits.javi.media.stream.Frame;
import tk.ivybits.javi.media.transcoder.Filter;
import tk.ivybits.javi.media.transcoder.Transcoder;

import java.util.ArrayList;

import static tk.ivybits.javi.ffmpeg.LibAVUtil.*;
import static tk.ivybits.javi.ffmpeg.LibSWResample.*;
import static tk.ivybits.javi.format.SampleFormat.Encoding.isPlanar;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWAudioTranscoder implements Transcoder {
    private Pointer swrContext;
    private PointerByReference dstData = new PointerByReference();
    private IntByReference dstLinesize = new IntByReference();
    private int maxSamples = 0;
    protected final SampleFormat from;
    protected final SampleFormat to;
    protected final ArrayList<Filter> filters;

    public SWAudioTranscoder(SampleFormat from, SampleFormat to, ArrayList<Filter> filters) {
        this.from = from;
        this.to = to;
        this.filters = filters;
        swrContext = swr_alloc_set_opts(
                null,
                to.channelLayout().ordinal() + 1, to.encoding().ordinal(), to.frequency(),
                from.channelLayout().ordinal() + 1, from.encoding().ordinal(), from.frequency(),
                0, null);
        swr_init(swrContext);
    }

    @Override
    public Frame transcode(Frame buffer) {
        int err;
        if (maxSamples < buffer.samples()) {
            close();
            err = av_samples_alloc_array_and_samples(dstData, dstLinesize, to.channels(), buffer.samples(), to.encoding().ordinal(), 0);
            maxSamples = buffer.samples();
            if (err < 0) {
                throw new StreamException("failed to allocate destination buffer: " + err, err);
            }
        }

        Pointer extended_data = av_malloc(buffer.planes() * Pointer.SIZE);
        for (int i = 0; i != buffer.planes(); i++) {
            extended_data.setPointer(Native.POINTER_SIZE * i, Native.getDirectBufferPointer(buffer.plane(i).buffer()));
        }

        int length = dstLinesize.getValue();
        err = swr_convert(swrContext, dstData.getValue(), length, extended_data, buffer.samples());

        av_free(extended_data);
        if (err < 0)
            throw new StreamException("failed to transcode audio: " + err, err);

        Frame.Plane[] planes = new Frame.Plane[isPlanar(to.encoding()) ? to.channels() : 1];

        for (int p = 0; p != planes.length; p++) {
            planes[p] = new Frame.Plane(dstData.getValue().getPointer(p * Pointer.SIZE).getByteBuffer(0, length), length);
        }

        Frame result = new Frame(planes, buffer.samples());
        for (Filter f : filters)
            f.apply(result);
        return result;
    }

    @Override
    public void close() {
        if (dstData.getValue() != null) {
            av_freep(new PointerByReference(dstData.getValue().getPointer(0)));
            av_freep(dstData);
        }
    }
}
