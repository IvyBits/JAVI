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

package tk.ivybits.javi.ffmpeg;

import static tk.ivybits.javi.debug.Utilities.getVersion;

public final class FFmpeg {
    private FFmpeg() {
        throw new AssertionError();
    }

    public static final String AVCODEC_VERSION = getVersion(LibAVCodec.avcodec_version());
    public static final String AVFORMAT_VERSION = getVersion(LibAVFormat.avformat_version());
    public static final String AVUTIL_VERSION = getVersion(LibAVUtil.avutil_version());

    static void ensureInitialized() {
        // Ensures one call to static block
    }

    public static void release() {
        LibAVFormat.avformat_network_deinit();
    }

    static {
        LibAVFormat.avformat_network_init();
        LibAVFormat.av_register_all();
        LibAVCodec.avcodec_register_all();
    }
}
