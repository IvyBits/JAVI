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

package tk.ivybits.javi.media.ff;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.format.SampleFormat;
import tk.ivybits.javi.media.stream.AudioStream;

import static tk.ivybits.javi.format.SampleFormat.ChannelLayout;
import static tk.ivybits.javi.format.SampleFormat.Encoding;

/**
 * FFmpeg-backed VideoStream.
 */
public class FFAudioStream extends FFStream implements AudioStream {
    private final SampleFormat sampleFormat;

    FFAudioStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
        sampleFormat = new SampleFormat(
                Encoding.values()[ffstream.codec.sample_fmt],
                ChannelLayout.values()[(int) (ffstream.codec.channel_layout - 1)],
                ffstream.codec.sample_rate, ffstream.codec.channels);
    }

    @Override
    public SampleFormat audioFormat() {
        return sampleFormat;
    }
}
