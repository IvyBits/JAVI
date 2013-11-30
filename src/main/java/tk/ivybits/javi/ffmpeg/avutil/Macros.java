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

package tk.ivybits.javi.ffmpeg.avutil;

public class Macros {
    public static int makeBETag(char a, char b, char c, char d) {
        return ((int) a & 0xFF) | (((int) b & 0xFF) << 8) | (((int) c & 0xFF) << 16) | (((int) d & 0xFF) << 24);
    }

    public static int makeBETag(CharSequence str) {
        if (str.length() != 4)
            throw new IllegalArgumentException("invalid string length for 32 bit tag");
        return makeBETag(str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3));
    }
}
