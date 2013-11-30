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

package tk.ivybits.javi.media.ffmedia;

import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.stream.Stream;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.*;

/**
 * Represents an arbitrary stream in a container.
 */
public class FFStream implements Stream {
    public final FFMedia container;
    public final AVStream ffstream;
    public final AVCodec codec;
    protected boolean closed;

    FFStream(FFMedia container, AVStream ffstream) {
        this.container = container;
        this.ffstream = ffstream;
        codec = avcodec_find_decoder(ffstream.codec.codec_id);
        if (codec == null || avcodec_open2(ffstream.codec.getPointer(), codec.getPointer(), null) < 0) {
            throw new StreamException("unsupported " + type() + " codec: " + ffstream.codec.codec_id);
        }
        ffstream.codec.read();
    }

    /**
     * {@inheritDoc}
     */
    public Media container() {
        return container;
    }

    /**
     * {@inheritDoc}
     */
    public Stream.Type type() {
        return Stream.Type.values()[ffstream.codec.codec_type];
    }

    /**
     * {@inheritDoc}
     */
    public int index() {
        return ffstream.index;
    }

    /**
     * {@inheritDoc}
     */
    public String codecName() {
        return ffstream.codec.codec.name;
    }

    /**
     * {@inheritDoc}
     */
    public String longCodecName() {
        return ffstream.codec.codec.long_name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        if (!closed) {
            avcodec_close(ffstream.codec.getPointer());
        }
    }
}
