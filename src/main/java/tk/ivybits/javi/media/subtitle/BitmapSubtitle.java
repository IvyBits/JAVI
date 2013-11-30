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

package tk.ivybits.javi.media.subtitle;

import tk.ivybits.javi.format.SubtitleType;

import java.awt.image.BufferedImage;

/**
 * Class to represent a bitmap-style subtitle.
 * <p/>
 * This class provides a location and image (with transparency) to draw over video.
 * <p/>
 * Note: malformed subtitle do exist whose coordinates are outside of the video.
 *
 * @since 1.0
 */
public class BitmapSubtitle implements Subtitle {
    /**
     * The x-coordinate to draw the subtitle.
     */
    public final int x;

    /**
     * The y-coordinate to draw the subtitle.
     */
    public final int y;

    /**
     * The subtitle image.
     */
    public final BufferedImage image;

    /**
     * Constructs a <code>BitmapSubtitle</code>.
     *
     * @param x     x-coordinate to draw the subtitle.
     * @param y     y-coordinate to draw the subtitle.
     * @param image The subtitle image.
     */
    public BitmapSubtitle(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_BITMAP;
    }
}
