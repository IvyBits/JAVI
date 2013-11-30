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

public class AVPicture extends Structure {
    public Pointer[] data = new Pointer[AV_NUM_DATA_POINTERS];
    public int[] linesize = new int[AV_NUM_DATA_POINTERS];

    public static final int AV_NUM_DATA_POINTERS = 8;

    public AVPicture(Pointer address) {
        super(address);
        read();
    }

    public AVPicture() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("data", "linesize");
    }
}
