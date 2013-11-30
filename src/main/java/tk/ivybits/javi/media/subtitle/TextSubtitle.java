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

/**
 * Class to represent a plain text subtitle exposed by FFmpeg.
 */
public class TextSubtitle implements Subtitle {
    /**
     * Subtitle in plain text.
     */
    public final String text;

    /**
     * Creates a plain text subtitle line.
     *
     * @param text Subtitle in plain text.
     */
    public TextSubtitle(String text) {
        this.text = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_TEXT;
    }
}
