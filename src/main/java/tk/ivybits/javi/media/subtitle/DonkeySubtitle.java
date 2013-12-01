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
 * This class represents a dialogue line of an Advanced SubStation Alpha format subtitle,
 * euphemistically called "Donkey".
 * <p/>
 * Drawing this subtitle is hard. There is a helper class
 * {@link tk.ivybits.javi.media.subtitle.DonkeyParser.DrawHelper} to aid the drawing of the subtitle.
 */
public class DonkeySubtitle implements Subtitle {
    /**
     * The parser that generated this subtitle.
     * <p/>
     * Guaranteed to be different if came from different streams.
     */
    public final DonkeyParser parser;

    /**
     * The style used by this line of subtitle.
     */
    public final DonkeyParser.Style style;

    /**
     * The time in milliseconds, relative to the start of the video, to start showing this line.
     */
    public final long start;

    /**
     * The time in milliseconds, relative to the start of the video, to stop showing this line.
     */
    public final long end;

    /**
     * The line of subtitle, in text.
     */
    public final String line;

    /**
     * Constructs a line of Advanced SubStation Alpha format subtitle.
     *
     * @param parser The parser that generated this subtitle.
     * @param style  The style used by this line of subtitle.
     * @param start  The time in milliseconds, relative to the start of the video, to start showing this line.
     * @param end    The time in milliseconds, relative to the start of the video, to stop showing this line.
     * @param line   The line of subtitle, in text.
     */
    public DonkeySubtitle(DonkeyParser parser, DonkeyParser.Style style, long start, long end, String line) {
        this.parser = parser;
        this.style = style;
        this.start = start;
        this.end = end;
        this.line = line;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SubtitleType type() {
        return SubtitleType.SUBTITLE_DONKEY;
    }
}
