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

package tk.ivybits.javi.media.transcoder;

import tk.ivybits.javi.format.PixelFormat;
import tk.ivybits.javi.format.SampleFormat;
import tk.ivybits.javi.ffmpeg.media.transcoder.SWAudioTranscoder;
import tk.ivybits.javi.ffmpeg.media.transcoder.SWFrameTranscoder;

import java.util.ArrayList;
import java.util.Arrays;

public class TranscoderFactory {
    public static class VideoTranscoderBuilder {
        private int srcWidth = 0, srcHeight = 0;
        private int dstWidth = 0, dstHeight = 0;
        private PixelFormat srcPixelFormat, dstPixelFormat;
        private ArrayList<Filter> filters = new ArrayList<Filter>();

        public VideoTranscoderBuilder from(int srcWidth, int srcHeight, PixelFormat srcPixelFormat) {
            this.srcWidth = srcWidth;
            this.srcHeight = srcHeight;
            if (dstWidth == 0)
                dstWidth = srcWidth;
            if (dstHeight == 0)
                dstHeight = srcHeight;
            this.srcPixelFormat = srcPixelFormat;
            return this;
        }

        public VideoTranscoderBuilder to(PixelFormat dstPixelFormat) {
            this.dstPixelFormat = dstPixelFormat;
            return this;
        }

        public VideoTranscoderBuilder rescale(int dstWidth, int dstHeight) {
            this.dstWidth = dstWidth;
            this.dstHeight = dstHeight;
            return this;
        }

        public VideoTranscoderBuilder filter(Filter... filters) {
            this.filters.addAll(Arrays.asList(filters));
            return this;
        }

        public Transcoder create() {
              return new SWFrameTranscoder(srcWidth, srcHeight, srcPixelFormat, dstWidth, dstHeight, dstPixelFormat, filters);
        }
    }

    public static class AudioTranscoderBuilder {
        private SampleFormat from, to;
        private ArrayList<Filter> filters = new ArrayList<Filter>();

        public AudioTranscoderBuilder from(SampleFormat from) {
            this.from = from;
            return this;
        }

        public AudioTranscoderBuilder to(SampleFormat to) {
            this.to = to;
            return this;
        }

        public AudioTranscoderBuilder filter(Filter... filters) {
            this.filters.addAll(Arrays.asList(filters));
            return this;
        }

        public Transcoder create() {
            return new SWAudioTranscoder(from, to, filters);
        }
    }

    public static VideoTranscoderBuilder frame() {
        return new VideoTranscoderBuilder();
    }

    public static AudioTranscoderBuilder audio() {
        return new AudioTranscoderBuilder();
    }
}
