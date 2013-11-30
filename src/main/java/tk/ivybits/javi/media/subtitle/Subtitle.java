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
 * Interface for a general subtitles.
 * <p/>
 * There are many possible subtitle implementations that must be dealt separately, in general,
 * they come in two forms: bitmap and text. Users of this interface should support the two
 * major concrete implementations: {@link tk.ivybits.javi.media.subtitle.BitmapSubtitle} and
 * {@link tk.ivybits.javi.media.subtitle.DonkeySubtitle}.
 *
 * @since 1.0
 */
public interface Subtitle {
    /**
     * Gets the type of subtitle.
     *
     * @return the type of subtitle.
     */
    SubtitleType type();
}
