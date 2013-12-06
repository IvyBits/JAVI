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

package tk.ivybits.javi.media;

import tk.ivybits.javi.media.ff.FFMedia;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class MediaFactory {
    private MediaFactory() {
        throw new AssertionError();
    }

    public static Media open(URI uri) throws IOException {
        return new FFMedia(uri);
    }

    public static Media open(File file) throws IOException {
        return new FFMedia(file);
    }
}
