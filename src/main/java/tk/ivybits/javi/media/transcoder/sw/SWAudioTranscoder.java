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

import com.sun.jna.Pointer;
import tk.ivybits.javi.format.SampleFormat;
import tk.ivybits.javi.media.transcoder.AudioTranscoder;
import tk.ivybits.javi.media.transcoder.Filter;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static tk.ivybits.javi.ffmpeg.LibSWResample.swr_alloc_set_opts;
import static tk.ivybits.javi.ffmpeg.LibSWResample.swr_init;

/**
 * @version 1.0
 * @since 1.0
 */
public class SWAudioTranscoder extends AudioTranscoder {
    private Pointer swrContext;

    public SWAudioTranscoder(SampleFormat from, SampleFormat to, ArrayList<Filter> filters) {
        super(from, to, filters);
        swrContext = swr_alloc_set_opts(
                null,
                to.channelLayout().ordinal() + 1, to.encoding().ordinal(), to.frequency(),
                from.channelLayout().ordinal() + 1, from.encoding().ordinal(), from.frequency(),
                0, null);
        swr_init(swrContext);
    }

    @Override
    public void transcode(ByteBuffer buffer) {

        for (Filter f : filters)
            f.apply(buffer);
    }
}
