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

/**
 * A stream handler.
 *
 * @param <T> The type of buffer this handler supports.
 * @version 1.0
 * @since 1.0
 */
public abstract class MediaHandler<T> {
    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @since 1.0
     */
    public void handle(T buffer) {
    }

    /**
     * Handles a frame.
     *
     * @param buffer The buffer to handle.
     * @param time   The time since the last frame.
     * @since 1.0
     */
    public void handle(T buffer, long time) {
    }

    public void handle(T buffer, long start, long end) {
    }

    /**
     * Called to signify that the stream has ended.
     * <p/>
     * Cleanup (if any) should be done here.
     *
     * @since 1.0
     */
    public void end() {

    }
}