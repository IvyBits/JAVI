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

package tk.ivybits.javi.ffmpeg.media.stream;

import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.ffmpeg.avutil.AVDictionary;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.stream.Stream;

import java.util.HashMap;
import java.util.Locale;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.*;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_dict_get;

/**
 * Represents an arbitrary stream in a container.
 */
public class FFStream implements Stream {
    public final FFMedia container;
    public final AVStream ffstream;
    public final AVCodec codec;
    public final Locale language;
    protected boolean closed;
    private static final HashMap<String, Locale> ISO_3 = new HashMap<String, Locale>();

    static {
        String[] languages = Locale.getISOLanguages();
        for (String language : languages) {
            Locale locale = new Locale(language);
            ISO_3.put(locale.getISO3Language(), locale);
        }
        // Aliases... Why can't people just be consistent
        ISO_3.put("alb", ISO_3.get("sqi"));
        ISO_3.put("arm", ISO_3.get("hye"));
        ISO_3.put("baq", ISO_3.get("eus"));
        ISO_3.put("tib", ISO_3.get("bod"));
        ISO_3.put("bur", ISO_3.get("mya"));
        ISO_3.put("cze", ISO_3.get("ces"));
        ISO_3.put("chi", ISO_3.get("zho"));
        ISO_3.put("wel", ISO_3.get("cym"));
        ISO_3.put("cze", ISO_3.get("ces"));
        ISO_3.put("ger", ISO_3.get("deu"));
        ISO_3.put("dut", ISO_3.get("nld"));
        ISO_3.put("gre", ISO_3.get("ell"));
        ISO_3.put("baq", ISO_3.get("eus"));
        ISO_3.put("per", ISO_3.get("fas"));
        ISO_3.put("fre", ISO_3.get("fra"));
        ISO_3.put("fre", ISO_3.get("fra"));
        ISO_3.put("geo", ISO_3.get("kat"));
        ISO_3.put("ger", ISO_3.get("deu"));
        ISO_3.put("gre", ISO_3.get("ell"));
        ISO_3.put("arm", ISO_3.get("hye"));
        ISO_3.put("ice", ISO_3.get("isl"));
        ISO_3.put("geo", ISO_3.get("kat"));
        ISO_3.put("mac", ISO_3.get("mkd"));
        ISO_3.put("mao", ISO_3.get("mri"));
        ISO_3.put("may", ISO_3.get("msa"));
        ISO_3.put("mac", ISO_3.get("mkd"));
        ISO_3.put("mao", ISO_3.get("mri"));
        ISO_3.put("may", ISO_3.get("msa"));
        ISO_3.put("bur", ISO_3.get("mya"));
        ISO_3.put("dut", ISO_3.get("nld"));
        ISO_3.put("per", ISO_3.get("fas"));
        ISO_3.put("rum", ISO_3.get("ron"));
        ISO_3.put("rum", ISO_3.get("ron"));
        ISO_3.put("slo", ISO_3.get("slk"));
        ISO_3.put("slo", ISO_3.get("slk"));
        ISO_3.put("alb", ISO_3.get("sqi"));
        ISO_3.put("tib", ISO_3.get("bod"));
        ISO_3.put("wel", ISO_3.get("cym"));
        ISO_3.put("chi", ISO_3.get("zho"));
    }

    FFStream(FFMedia container, AVStream ffstream) {
        this.container = container;
        this.ffstream = ffstream;
        codec = avcodec_find_decoder(ffstream.codec.codec_id);
        if (codec == null || avcodec_open2(ffstream.codec.getPointer(), codec.getPointer(), null) < 0) {
            throw new IllegalArgumentException("unsupported " + type() + " codec: " + ffstream.codec.codec_id);
        }
        ffstream.codec.read();
        AVDictionary.Entry entry = av_dict_get(ffstream.metadata, "language", null, 0);
        language = entry != null ? ISO_3.get(entry.value) : null;
    }

    @Override
    public Media container() {
        return container;
    }

    @Override
    public Stream.Type type() {
        return Stream.Type.values()[ffstream.codec.codec_type];
    }

    @Override
    public Locale language() {
        return language;
    }

    @Override
    public int index() {
        return ffstream.index;
    }

    @Override
    public String codecName() {
        return ffstream.codec.codec.name;
    }

    @Override
    public String longCodecName() {
        return ffstream.codec.codec.long_name;
    }

    @Override
    public void close() {
        if (!closed) {
            avcodec_close(ffstream.codec.getPointer());
        }
    }
}
