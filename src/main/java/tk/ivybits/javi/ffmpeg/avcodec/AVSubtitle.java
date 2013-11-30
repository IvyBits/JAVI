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

import java.util.Arrays;
import java.util.List;

public class AVSubtitle extends Structure {
    public short format;
    public int start_display_time;
    public int end_display_time;
    public int num_rects;
    public Pointer rects;
    public long pts;

    public AVSubtitle(Pointer address) {
        super(address);
        read();
    }

    public AVSubtitle() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("format", "start_display_time", "end_display_time", "num_rects", "rects", "pts");
    }
}
