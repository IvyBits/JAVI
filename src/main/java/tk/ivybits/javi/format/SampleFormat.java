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

package tk.ivybits.javi.format;

import com.sun.jna.Native;

import javax.sound.sampled.AudioFormat;

/**
 * Sample formats.
 *
 * @version 1.0
 * @since 1.0
 */
public class SampleFormat {
    private final Encoding format;
    private final ChannelLayout layout;
    private final int frequency;

    public SampleFormat(Encoding format, ChannelLayout layout, int frequency) {
        this.format = format;
        this.layout = layout;
        this.frequency = frequency;
    }

    public Encoding encoding() {
        return format;
    }

    public ChannelLayout channelLayout() {
        return layout;
    }

    public int frequency() {
        return frequency;
    }

    @Override
    public String toString() {
        return format + "(" + layout + ") @ " + frequency + "Hz";
    }

    public static enum ChannelLayout {
        LEFT,
        RIGHT,
        STEREO
    }

    public static enum Encoding {
        UNSIGNED_8BIT(8),
        SIGNED_16BIT(16),
        SIGNED_32BIT(32),
        SIGNED_FLOAT(Native.getNativeSize(float.class) * 8),
        SIGNED_DOUBLE(Native.getNativeSize(double.class) * 8),
        SIGNED_8BIT_PLANAR(8),
        SIGNED_16BIT_PLANAR(16),
        SIGNED_32BIT_PLANAR(32),
        SIGNED_FLOAT_PLANAR(Native.getNativeSize(float.class) * 8),
        SIGNED_DOUBLE_PLANAR(Native.getNativeSize(double.class) * 8);
        private final int bps;

        Encoding(int bps) {
            this.bps = bps;
        }

        public int bitsPerSample() {
            return bps;
        }
    }
}
