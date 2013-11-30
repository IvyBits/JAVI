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

public class DonkeySubtitle implements Subtitle {
    public final DonkeyParser parser;
    public final DonkeyParser.Style style;
    public final long start;
    public final long end;
    public final String line;

    public DonkeySubtitle(DonkeyParser parser, DonkeyParser.Style style, long start, long end, String line) {
        this.parser = parser;
        this.style = style;
        this.start = start;
        this.end = end;
        this.line = line;
    }

    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_DONKEY;
    }
}
