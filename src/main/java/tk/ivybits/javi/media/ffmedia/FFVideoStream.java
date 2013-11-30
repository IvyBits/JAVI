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

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.stream.VideoStream;

/**
 * FFmpeg-backed VideoStream.
 */
public class FFVideoStream extends FFStream implements VideoStream {
    FFVideoStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * {@inheritDoc}
     */
    public int width() {
        return ffstream.codec.width;
    }

    /**
     * {@inheritDoc}
     */
    public int height() {
        return ffstream.codec.height;
    }

    @Override
    public double framerate() {
        return ffstream.r_frame_rate.num / (double)ffstream.r_frame_rate.den;
    }
}
