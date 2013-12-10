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

package tk.ivybits.javi.media.ff;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.ffmpeg.avcodec.*;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.format.SampleFormat;
import tk.ivybits.javi.format.SubtitleType;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.handler.AudioHandler;
import tk.ivybits.javi.media.handler.FrameHandler;
import tk.ivybits.javi.media.handler.SubtitleHandler;
import tk.ivybits.javi.media.stream.*;
import tk.ivybits.javi.media.subtitle.BitmapSubtitle;
import tk.ivybits.javi.media.subtitle.DonkeyParser;
import tk.ivybits.javi.media.subtitle.TextSubtitle;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.*;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_read_frame;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_seek_frame;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_malloc;
import static tk.ivybits.javi.ffmpeg.LibC.memcpy;
import static tk.ivybits.javi.format.PixelFormat.BGR24;
import static tk.ivybits.javi.format.SampleFormat.Encoding.*;
import static tk.ivybits.javi.media.stream.Frame.Plane;

/**
 * FFmpeg MediaStream implementation.
 * </p>
 * Cannot be instantiated directly.
 *
 * @version 1.0
 * @since 1.0
 */
public class FFMediaStream implements MediaStream {
    public final FFMedia media;
    public AudioHandler audioHandler;
    public FrameHandler videoHandler;
    public SubtitleHandler subtitleHandler;
    public FFAudioStream audioStream;
    public FFVideoStream videoStream;
    public FFSubtitleStream subtitleStream;

    public AVFrame.ByReference pBGRFrame;
    public AVFrame.ByReference pFrame;

    public DonkeyParser[] donkeyParsers;
    public AVSubtitle pSubtitle;

    public AVCodec videoCodec, audioCodec, subtitleCodec;
    public boolean playing = false;
    public boolean started;
    public long time;
    private final Semaphore mutex = new Semaphore(1);

    FFMediaStream(FFMedia media, AudioHandler audioHandler, FrameHandler videoHandler,
                  SubtitleHandler subtitleHandler) throws IOException {
        this.media = media;
        this.audioHandler = audioHandler;
        this.videoHandler = videoHandler;
        this.subtitleHandler = subtitleHandler;
        pFrame = avcodec_alloc_frame();

        donkeyParsers = new DonkeyParser[media.formatContext.nb_streams];
    }

    @Override
    public VideoStream setVideoStream(VideoStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        VideoStream pre = videoStream;

        if (pBGRFrame != null) {
            avcodec_free_frame(new PointerByReference(pBGRFrame.getPointer()));
        }
        pBGRFrame = avcodec_alloc_frame();
        int size = avpicture_get_size(BGR24.id, stream.width(), stream.height());

        Pointer buffer = av_malloc(size);
        int ret = avpicture_fill(pBGRFrame.getPointer(), buffer, BGR24.id, stream.width(), stream.height());
        if (ret < 0 || ret != size)
            throw new StreamException("failed to fill frame buffer: " + ret, ret);
        videoStream = (FFVideoStream) stream;
        videoCodec = videoStream.codec;
        return pre;
    }

    @Override
    public AudioStream setAudioStream(AudioStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        AudioStream pre = audioStream;
        audioStream = (FFAudioStream) stream;
        audioCodec = audioStream.codec;
        return pre;
    }

    @Override
    public SubtitleStream setSubtitleStream(SubtitleStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        if (pSubtitle == null)
            pSubtitle = new AVSubtitle();
        SubtitleStream pre = subtitleStream;
        subtitleStream = (FFSubtitleStream) stream;
        subtitleCodec = subtitleStream.codec;
        return pre;
    }

    @Override
    public void run() {
        started = playing = true;
        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();
        av_init_packet(packet.getPointer());
        AVCodecContext ac = audioStream != null ? audioStream.ffstream.codec : null;
        AVCodecContext vc = videoStream != null ? videoStream.ffstream.codec : null;

        if (videoStream != null) {
            vc.read();
        }
        if (audioStream != null) {
            ac.read();
        }
        audioHandler.start();
        videoHandler.start();
        subtitleHandler.start();
        _outer:
        while (av_read_frame(media.formatContext.getPointer(), packet.getPointer()) >= 0) {
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                throw new IllegalStateException("could not acquire frame mutex");
            }

            packet.read();

            if (audioStream != null && packet.stream_index == audioStream.index()) {
                // Decode the media into our pFrame
                int read = 0;

                // According to FFmpeg docs:
                // Some decoders may support multiple frames in a single AVPacket.
                // Such decoders would then just decode the first handle and the return value
                // would be less than the packet size. In this case, avcodec_decode_audio4 has
                // to be called again with an AVPacket containing the remaining data in order to
                // decode the second handle, etc... Even if no frames are returned, the packet needs
                // to be fed to the decoder with remaining data until it is completely consumed or
                // an error occurs.
                // Implemented the first two sentences. Not sure about the last.
                while (read < packet.size) {
                    int err = avcodec_decode_audio4(ac.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());

                    if (err < 0) {
                        throw new StreamException("error while decoding audio stream: " + err, err);
                    } else {
                        read += err;
                    }

                    pFrame.read();
                    if (frameFinished.getValue() != 0) {
                        int linesize = pFrame.linesize[0];
                        Plane[] planes = new Plane[isPlanar(audioStream.audioFormat().encoding()) ? pFrame.channels : 1];

                        for(int p = 0; p != planes.length; p++) {
                            planes[p] = new Plane(Native.getDirectByteBuffer(
                                    Pointer.nativeValue(pFrame.extended_data.getPointer(p * Pointer.SIZE)),
                                    linesize), linesize);
                        }
                        audioHandler.handle(new Frame(planes, pFrame.nb_samples));
                    }
                }
            } else if (videoStream != null && packet.stream_index == videoStream.index()) {
                // Decode the media into our pFrame
                int err = avcodec_decode_video2(videoStream.ffstream.codec.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                // If the return of avcodec_decode_video2 is negative, an error occurred.
                // Fun fact: the error is actually the negative of an ASCII string in little-endian order.
                if (err < 0) {
                    throw new StreamException("error while decoding video stream: " + err, err);
                }
                if (frameFinished.getValue() != 0) {
                    pFrame.read();
                    // Don't kill us for this.
                    // Normally, sws_scale accepts a pointer to AVFrame.data
                    // However, getting such a pointer is non-trivial.
                    // Since data is the first member of the AVFrame structure, we may actually pass
                    // a pointer to the struct instead, because by definition, a pointer to a struct
                    // points to the first member of the struct.
                    // We use sws_scale itself to convert a data buffer of an arbitrary pixel format
                    // into our desired format: 3-byte BGR

                    //memcpy(pImageBuffer, pFrame.data[0], imageBuffer.limit());


                    // Read the buffer directly into the raster of our image
                    long nano = pFrame.pkt_duration * 1000000000 *
                            videoStream.ffstream.time_base.num / videoStream.ffstream.time_base.den;
                    if (nano == 0)
                        nano = (long) ((1000 / videoStream.framerate()) * 1000000);
                    time += nano / 1000000;

                    int i = 0;
                    for (; i < pFrame.linesize.length && pFrame.linesize[i] != 0; i++) ;
                    Plane[] planes = new Plane[i];
                    for(int p = 0; p != i; p++) {
                        int l = pFrame.linesize[p];
                        planes[p] = new Plane(Native.getDirectByteBuffer(Pointer.nativeValue(pFrame.data[p]), l * pFrame.height), l);
                    }
                    videoHandler.handle(new Frame(planes), nano);
                }
            } else if (subtitleStream != null && packet.stream_index == subtitleStream.index()) {
                int err = avcodec_decode_subtitle2(subtitleStream.ffstream.codec.getPointer(), pSubtitle.getPointer(), frameFinished, packet.getPointer());
                if (err < 0) {
                    throw new StreamException("error while decoding video stream: " + err, err);
                }
                if (frameFinished.getValue() != 0) {
                    pSubtitle.read();

                    long start = pSubtitle.start_display_time * 1000 * subtitleStream.ffstream.time_base.num / subtitleStream.ffstream.time_base.den;
                    long end = pSubtitle.end_display_time * 1000 * subtitleStream.ffstream.time_base.num / subtitleStream.ffstream.time_base.den;
                    for (Pointer pointer : pSubtitle.rects.getPointerArray(0, pSubtitle.num_rects)) {
                        AVSubtitleRect rect = new AVSubtitleRect(pointer);
                        switch (SubtitleType.values()[rect.type]) {
                            case SUBTITLE_NONE:
                                break;
                            case SUBTITLE_BITMAP: {
                                byte[] r = new byte[rect.nb_colors], g = new byte[rect.nb_colors],
                                        b = new byte[rect.nb_colors], a = new byte[rect.nb_colors];
                                for (int i = 0; i < rect.nb_colors; ++i) {
                                    int colour = rect.pict.data[1].getInt(i * 4);
                                    r[i] = (byte) ((colour >> 16) & 0xff);
                                    g[i] = (byte) ((colour >> 8) & 0xff);
                                    b[i] = (byte) ((colour) & 0xff);
                                    a[i] = (byte) ((colour >> 24) & 0xff);
                                }
                                IndexColorModel palette = new IndexColorModel(8, rect.nb_colors, r, g, b, a);
                                BufferedImage result = new BufferedImage(rect.w, rect.h, BufferedImage.TYPE_BYTE_INDEXED, palette);
                                byte[] raster = ((DataBufferByte) result.getRaster().getDataBuffer()).getData();
                                rect.pict.data[0].read(0, raster, 0, raster.length);

                                subtitleHandler.handle(new BitmapSubtitle(rect.x, rect.y, result), start, end);
                                break;
                            }
                            case SUBTITLE_TEXT: {
                                String subtitle = rect.text.getString(0, "UTF-8");
                                subtitleHandler.handle(new TextSubtitle(subtitle), start, end);
                                break;
                            }
                            case SUBTITLE_DONKEY: {
                                if (donkeyParsers[packet.stream_index] == null) {
                                    if (subtitleStream.ffstream.codec.subtitle_header_size <= 0)
                                        throw new IllegalStateException("subtitle without header");
                                    String header = subtitleStream.ffstream.codec.subtitle_header.getString(0, "UTF-8");
                                    //System.out.println(header);
                                    DonkeyParser parser = new DonkeyParser(header);
                                    donkeyParsers[packet.stream_index] = parser;
                                }
                                String subtitle = rect.ass.getString(0, "UTF-8");
                                subtitleHandler.handle(donkeyParsers[packet.stream_index].processDialog(subtitle), start, end);
                                break;
                            }
                        }
                    }
                }
            }
            // Free the packet that av_read_frame allocated
            av_free_packet(packet.getPointer());
            mutex.release();
        }
        videoHandler.end();
        audioHandler.end();
        subtitleHandler.end();
        setPlaying(false);
    }

    @Override
    public boolean isPlaying() {
        return playing;
    }

    @Override
    public void setPlaying(boolean flag) {
        if (!started)
            throw new IllegalStateException("stream not started");
        playing = flag;
        if (!playing)
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                throw new IllegalStateException("failed to aqcuire frame mutex");
            }
        else {
            mutex.release();
        }
    }

    @Override
    public void seek(long to) {
        if (!started)
            throw new IllegalStateException("stream not started");
        if (to < 0)
            throw new IllegalArgumentException("negative position");
        if (to > media.length())
            throw new IllegalArgumentException("position greater then video length");
        int err = av_seek_frame(media.formatContext.getPointer(), -1, to * 1000, 0);
        if (err < 0)
            throw new StreamException("failed to seek video: error " + err, err);
        time = to;
    }

    @Override
    public long position() {
        return time;
    }

    @Override
    public void close() {
        avcodec_free_frame(new PointerByReference(pFrame.getPointer()));
    }

    @Override
    public FFAudioStream getAudioStream() {
        return audioStream;
    }

    @Override
    public FFVideoStream getVideoStream() {
        return videoStream;
    }

    @Override
    public FFSubtitleStream getSubtitleStream() {
        return subtitleStream;
    }

    public static class Builder implements MediaStream.Builder {
        public FFMedia media;
        public AudioHandler audioHandler = AudioHandler.NO_HANDLER;
        public FrameHandler videoHandler = FrameHandler.NO_HANDLER;
        public SubtitleHandler subtitleHandler = SubtitleHandler.NO_HANDLER;

        /**
         * Creates a MediaStream builder for the specified {@link Media} object.
         *
         * @param media The container designated for streaming.
         * @since 1.0
         */
        public Builder(FFMedia media) {
            this.media = media;
        }

        @Override
        public Builder audio(AudioHandler audioHandler) {
            this.audioHandler = audioHandler;
            return this;
        }

        @Override
        public Builder video(FrameHandler videoHandler) {
            this.videoHandler = videoHandler;
            return this;
        }

        @Override
        public Builder subtitle(SubtitleHandler subtitleHandler) {
            this.subtitleHandler = subtitleHandler;
            return this;
        }

        @Override
        public FFMediaStream create() throws IOException {
            if (audioHandler == null && videoHandler == null && subtitleHandler == null)
                throw new IllegalStateException("no media handlers specified");
            return new FFMediaStream(media, audioHandler, videoHandler, subtitleHandler);
        }
    }
}
