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

package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.LongByReference;
import tk.ivybits.javi.ffmpeg.avutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVCodec extends Structure {
    public static class ByReference extends AVCodec implements Structure.ByReference {
    }

    public static class ByValue extends AVCodec implements Structure.ByValue {
    }

    public String name;

    public String long_name;
    public int type;
    public int id;

    public int capabilities;
    public AVRational.ByReference supported_framerates;
    public Pointer pix_fmts;

    public Pointer supported_samplerates;
    public Pointer sample_fmts;
    public LongByReference channel_layouts;
    public byte max_lowres;
    public Pointer /* AVClass.ByReference */ priv_class;
    public Pointer /* AVProfile.ByReference */ profiles;

    public AVCodec(Pointer address) {
        super(address);
        read();
    }

    public AVCodec() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("name", "long_name", "type", "id", "capabilities",
                "supported_framerates", "pix_fmts", "supported_samplerates",
                "sample_fmts", "channel_layouts", "max_lowres", "priv_class", "profiles");
    }
}
