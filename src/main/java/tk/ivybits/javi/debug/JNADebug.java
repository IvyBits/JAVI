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

package tk.ivybits.javi.debug;

import com.sun.jna.Structure;

public class JNADebug {
    public static final String[] classes = {
            "avcodec.AVCodec",
            "avcodec.AVCodecContext",
            "avcodec.AVPacket",
            "avcodec.AVPicture",
            "avcodec.AVSubtitle",
            "avcodec.AVSubtitleRect",
            "avformat.AVFormatContext",
            "avformat.AVFrac",
            "avformat.AVProbeData",
            "avformat.AVStream",
            "avutil.AVFrame",
            "avutil.AVRational",
    };

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (String clazz : classes) {
            try {
                Class<?> struct = Class.forName("tk.ivybits.javi.ffmpeg." + clazz);
                Structure victim = (Structure) struct.newInstance();
                StructureUtilities.printFields(victim);
                System.out.println();
            } catch (Exception ignored) {
            }
        }
    }
}
