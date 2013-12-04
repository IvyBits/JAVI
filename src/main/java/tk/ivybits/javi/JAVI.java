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

package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.LibAVCodec;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.LibAVUtil;
import tk.ivybits.javi.natives.Natives;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public final class JAVI {
    private JAVI() {
        throw new AssertionError();
    }

    public static final String AVUTIL_VERSION = getVersion(LibAVUtil.avutil_version());
    public static final String AVCODEC_VERSION = getVersion(LibAVCodec.avcodec_version());
    public static final String AVFORMAT_VERSION = getVersion(LibAVFormat.avformat_version());
    public static final String JAVI_VERSION = getVersion();

    private static boolean initialized = false;

    public static void initialize() {
        if (initialized)
            return;
        Natives.unpack();
        initialized = true;
    }

    public static void release() {
        LibAVFormat.avformat_network_deinit();
    }

    private static String getVersion(int version) {
        return String.format("%d.%d.%d", version >> 16, (version >> 8) & 0xFF, version & 0xFF);
    }

    private static String getVersion() {
        try {
            Enumeration<URL> resources = ClassLoader.getSystemClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            if (resources.hasMoreElements()) {
                try {
                    Manifest manifest = new Manifest(resources.nextElement().openStream());
                    Attributes main = manifest.getMainAttributes();
                    return String.format(
                            "%s %s (implementing API %s)",
                            main.getValue("Implementation-Title"),
                            main.getValue("Implementation-Version"),
                            main.getValue("Specification-Version"));
                } catch (IOException E) {

                }
            }
        } catch (Exception e) {

        }
        return "JAVI-unknown";
    }

    static {
        LibAVFormat.avformat_network_init();
        LibAVFormat.av_register_all();
        LibAVCodec.avcodec_register_all();
    }
}
