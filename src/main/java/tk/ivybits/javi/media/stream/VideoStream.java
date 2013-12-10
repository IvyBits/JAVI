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

import tk.ivybits.javi.format.PixelFormat;

public interface VideoStream extends Stream {
    /**
     * Fetches video stream width.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video width.
     * @since 1.0
     */
    int width();

    /**
     * Fetches video stream height.
     * <b>Note: some codecs support the arbitrary changing of frame size, so the value returned
     * from this function may not be correct for the entire video.</b>
     *
     * @return Video height.
     * @since 1.0
     */
    int height();

    double framerate();

    PixelFormat pixelFormat();
}
