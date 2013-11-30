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

public class AVSubtitleRect extends Structure {
    public int x;
    public int y;
    public int w;
    public int h;
    public int nb_colors;
    public AVPicture pict;
    public int type;
    public Pointer text;
    public Pointer ass; // In places not needing to be C compatible, it's called donkey
    public int flags;

    public AVSubtitleRect(Pointer address) {
        super(address);
        read();
    }

    public AVSubtitleRect() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("x", "y", "w", "h", "nb_colors", "pict", "type", "text", "ass", "flags");
    }
}
