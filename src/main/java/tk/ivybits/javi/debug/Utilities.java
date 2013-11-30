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

package tk.ivybits.javi.debug;

public class Utilities {
    public static final int AV_TIME_BASE = 1000000;

    public static double getSeconds(long ffmpegTime) {
        if (ffmpegTime == Long.MIN_VALUE)
            return 0;
        return ffmpegTime / ((double) AV_TIME_BASE);
    }

    public static String getVersion(int version) {
        return String.format("%d.%d.%d", version >> 16, (version >> 8) & 0xFF, version & 0xFF);
    }
}
