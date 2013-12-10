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

package tk.ivybits.javi.media.stream;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/**
 * @version 1.0
 * @since 1.0
 */
public class Frame implements Iterable<Frame.Plane> {
    public static class Plane {
        private final ByteBuffer buffer;
        private final int linesize;

        public Plane(ByteBuffer buffer, int linesize) {
            this.buffer = buffer;
            this.linesize = linesize;
        }

        public ByteBuffer buffer() {
            return buffer;
        }

        public int linesize() {
            return linesize;
        }
    }

    private final Plane[] planes;
    private final int samples;

    public Frame(Plane[] planes) {
        this(planes, 1);
    }

    public Frame(Plane[] planes, int samples) {
        this.planes = planes;
        this.samples = samples;
    }

    public Plane plane(int p) {
        return planes[p];
    }

    public int planes() {
        return planes.length;
    }

    public int samples() {
        return samples;
    }

    @Override
    public Iterator<Plane> iterator() {
        return Collections.unmodifiableList(Arrays.asList(planes)).iterator();
    }
}
