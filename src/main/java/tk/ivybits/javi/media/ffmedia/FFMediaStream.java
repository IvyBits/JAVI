package tk.ivybits.javi.media.ffmedia;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.ffmpeg.avcodec.*;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.format.PixelFormat;
import tk.ivybits.javi.format.SubtitleType;
import tk.ivybits.javi.media.*;
import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.SubtitleStream;
import tk.ivybits.javi.media.stream.VideoStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.*;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_read_frame;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_seek_frame;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_malloc;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_samples_alloc_array_and_samples;
import static tk.ivybits.javi.ffmpeg.LibSWResample.*;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_freeContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;
import static tk.ivybits.javi.format.PixelFormat.BGR24;
import static tk.ivybits.javi.format.SampleFormat.SIGNED_16BIT;

/**
 * A media stream.
 * </p>
 * Cannot be instantiated directly: use {@link tk.ivybits.javi.media.stream.MediaStream.Builder}.
 *
 * @version 1.0
 * @since 1.0
 */
public class FFMediaStream implements MediaStream {
    public final FFMedia media;
    public MediaHandler<byte[]> audioHandler;
    public MediaHandler<BufferedImage> videoHandler;
    public FFAudioStream audioStream;
    public FFVideoStream videoStream;
    public FFSubtitleStream subtitleStream;

    public AVFrame.ByReference pBGRFrame;
    public AVFrame.ByReference pFrame;

    public AVSubtitle pSubtitle;

    public AVCodec videoCodec, audioCodec, subtitleCodec;
    public long lastFrame = 0;
    public boolean playing = false;
    public boolean started;
    private final Semaphore mutex = new Semaphore(1);

    FFMediaStream(FFMedia media, MediaHandler<byte[]> audioHandler, MediaHandler<BufferedImage> videoHandler) throws IOException {
        this.media = media;
        this.audioHandler = audioHandler;
        this.videoHandler = videoHandler;
        pFrame = avcodec_alloc_frame();
    }

    /**
     * Sets a video stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream perviously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     */
    public VideoStream setVideoStream(VideoStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        VideoStream pre = videoStream;

        if (pBGRFrame != null) {
            avcodec_free_frame(new PointerByReference(pBGRFrame.getPointer()));
        }
        pBGRFrame = avcodec_alloc_frame();
        int size = avpicture_get_size(BGR24.ordinal(), stream.width(), stream.height());

        Pointer buffer = av_malloc(size);
        int ret = avpicture_fill(pBGRFrame.getPointer(), buffer, BGR24.ordinal(), stream.width(), stream.height());
        if (ret < 0 || ret != size)
            throw new StreamException("failed to fill frame buffer");
        videoStream = (FFVideoStream) stream;
        videoCodec = videoStream.codec;
        return pre;
    }

    /**
     * Sets a audio stream to be played.
     *
     * @param stream The stream to play.
     * @return The stream perviously playing, null if none.
     * @throws IllegalArgumentException Thrown if the stream does not belong to the parent container.
     * @throws StreamException          Thrown if an error occurs while allocating stream buffers.
     */
    public AudioStream setAudioStream(AudioStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        AudioStream pre = audioStream;
        audioStream = (FFAudioStream) stream;
        audioCodec = audioStream.codec;
        return pre;
    }

    public SubtitleStream setSubtitleStream(SubtitleStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        if (pSubtitle == null)
            pSubtitle = new AVSubtitle();
        SubtitleStream pre = subtitleStream;
        subtitleStream = (FFSubtitleStream) stream;
        subtitleCodec = subtitleStream.codec;
        System.out.println("Subtitle is set");
        return pre;
    }

    /**
     * Starts synchronous streaming.
     *
     * @throws StreamException Thrown if an error occurs while decoding.
     * @since 1.0
     */
    public void run() {
        started = playing = true;
        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();
        av_init_packet(packet.getPointer());
        int width = 0, height = 0;
        AVCodecContext ac = audioStream != null ? audioStream.ffstream.codec : null;
        AVCodecContext vc = videoStream != null ? videoStream.ffstream.codec : null;

        Pointer pSwsContext = null;
        BufferedImage imageBuffer = null;
        if (videoStream != null) {
            width = videoStream.width();
            height = videoStream.height();
            imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

            pSwsContext = sws_getContext(
                    width, height, vc.pix_fmt,
                    width, height, BGR24.ordinal(),
                    0, null, null, null);
            vc.read();
        }

        Pointer pSwrContext = null;
        byte[] audioBuffer = null;
        if (audioStream != null) {
            audioBuffer = new byte[16 * ac.channels * 64]; // Estimate common size

            pSwrContext = swr_alloc_set_opts(
                    null, 3, SIGNED_16BIT.ordinal(),
                    ac.sample_rate, ac.channel_layout, ac.sample_fmt,
                    ac.sample_rate, 0, null);
            swr_init(pSwrContext);
            ac.read();
        }

        lastFrame = System.nanoTime();
        int subtitle = 0;
        _outer:
        while (av_read_frame(media.formatContext.getPointer(), packet.getPointer()) >= 0) {
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                throw new StreamException("could not acquire mutex");
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
                        throw new StreamException("error while decoding audio stream: " + err);
                    } else {
                        read += err;
                    }

                    pFrame.read();
                    if (frameFinished.getValue() != 0) {
                        // We output signed 16 bit PCM data: we only need to convert if it is
                        // not in the format already
                        if (ac.sample_fmt != SIGNED_16BIT.ordinal()) {
                            PointerByReference dstData = new PointerByReference();
                            IntByReference dstLinesize = new IntByReference();
                            err = av_samples_alloc_array_and_samples(dstData, dstLinesize, pFrame.channels,
                                    pFrame.nb_samples, SIGNED_16BIT.ordinal(), 0);
                            if (err < 0) {
                                throw new StreamException("failed to allocate destination buffer: " + err);
                            }
                            int length = dstLinesize.getValue();
                            err = swr_convert(pSwrContext, dstData.getValue(), length,
                                    pFrame.extended_data, pFrame.nb_samples);
                            if (err < 0)
                                throw new StreamException("failed to transcode audio: " + err);
                            if (audioBuffer.length < length)
                                audioBuffer = new byte[length];
                            dstData.getValue().getPointer(0).read(0, audioBuffer, 0, length);
                        } else {
                            int length = pFrame.linesize[0];
                            if (audioBuffer.length < length)
                                audioBuffer = new byte[length];
                            pFrame.data[0].read(0, audioBuffer, 0, length);
                        }
                        audioHandler.handle(audioBuffer);
                    }
                }
            } else if (videoStream != null && packet.stream_index == videoStream.index()) {
                // Decode the media into our pFrame
                int err = avcodec_decode_video2(videoStream.ffstream.codec.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                // If the return of avcodec_decode_video2 is negative, an error occurred.
                // Fun fact: the error is actually the negative of an ASCII string in little-endian order.
                if (err < 0) {
                    throw new StreamException("error while decoding video stream: " + err);
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

                    sws_scale(pSwsContext, pFrame.getPointer(), pFrame.linesize, 0, height, pBGRFrame.getPointer(), pBGRFrame.linesize);
                    // Only data and linesize were changed in the sws_scale call: we only need update them
                    pBGRFrame.readField("data");
                    pBGRFrame.readField("linesize");
                    // A handle may be of arbitrary size: size is not required to be constant.
                    // If you decided to encode your files such that size changes, it is
                    // you who will feel the pain of reallocating the buffers.
                    // You have been warned.
                    if (imageBuffer.getWidth() != width || imageBuffer.getHeight() != height)
                        imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                    // Read the buffer directly into the raster of our image
                    byte[] raster = ((DataBufferByte) imageBuffer.getRaster().getDataBuffer()).getData();
                    pBGRFrame.data[0].read(0, raster, 0, raster.length);

                    long duration = pFrame.pkt_duration * 1_000_000_000 *
                            videoStream.ffstream.time_base.num / videoStream.ffstream.time_base.den;
                    long time = System.nanoTime();
                    duration -= time - lastFrame;
                    videoHandler.handle(imageBuffer, duration / 1_000_000);
                    // Add in duration, which is the time that is spent waiting for the frame to render, so we get
                    // the time when this frame is rendered, and set it as the last frame.
                    // If duration is NEGATIVE, nothing will be rendered. We basically are subtracting the overdue
                    // time from time we started handling this frame, so we get the time on which the current frame
                    // should be rendered. If multiple frames are skipped, this still works, as the lastFrame will
                    // advance by the length of each lost frame until it goes back to sync,
                    // i.e. duration is back to positive.
                    lastFrame = time + duration;
                }
            } else if (subtitleStream != null && packet.stream_index == subtitleStream.index()) {
                int err = avcodec_decode_subtitle2(subtitleStream.ffstream.codec.getPointer(), pSubtitle.getPointer(), frameFinished, packet.getPointer());
                if (err < 0) {
                    throw new StreamException("error while decoding video stream: " + err);
                }
                if (frameFinished.getValue() != 0) {
                    pSubtitle.read();

                    System.out.printf("Subtitle: %d - %d (%d)\n", pSubtitle.start_display_time, pSubtitle.end_display_time, pSubtitle.num_rects);
                    for (Pointer pointer : pSubtitle.rects.getPointerArray(0, pSubtitle.num_rects)) {
                        AVSubtitleRect rect = new AVSubtitleRect(pointer);
                        System.out.printf("  - Rect: %d\n", rect.type);
                        switch (SubtitleType.values()[rect.type]) {
                            case SUBTITLE_NONE:
                                break;
                            case SUBTITLE_BITMAP: {
                                System.out.println("    Raw Data: " + Arrays.toString(rect.pict.data));
                                System.out.println("    Colours: " + rect.nb_colors);
                                System.out.println("    Size: " + rect.w + "x" + rect.h);
                                System.out.println("    First: " + Arrays.toString(rect.pict.data[0].getLongArray(0, rect.h * rect.w / 8)));
                                break;
                            }
                            case SUBTITLE_TEXT:
                                System.out.println(rect.text.getString(0, "UTF-8"));
                                break;
                            case SUBTITLE_DONKEY:
                                break;
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
    }

    /**
     * Checks if the stream is playing.
     *
     * @return True if so, false otherwise.
     * @since 1.0
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * Sets the current state of the stream.
     *
     * @param flag If true, the stream will be played. Otherwise, it will be paused.
     * @throws StreamException Thrown if called when called on a stream that is not started.
     * @since 1.0
     */
    public void setPlaying(boolean flag) {
        if (!started)
            throw new StreamException("stream not started");
        playing = flag;
        if (!playing)
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        else {
            lastFrame = System.nanoTime();
            mutex.release();
        }
    }

    /**
     * Sets the current position of the stream, in milliseconds.
     *
     * @param to The position to seek to.
     * @throws IllegalArgumentException Thrown if the seek position is invalid.
     * @throws StreamException          Thrown if the seek failed.
     * @throws StreamException          Thrown if called when called on a stream that is not started.
     * @since 1.0
     */
    public void seek(long to) {
        if (!started)
            throw new StreamException("stream not started");
        if (to < 0)
            throw new IllegalArgumentException("negative position");
        if (to > media.length())
            throw new IllegalArgumentException("position greater then video length");
        int err = av_seek_frame(media.formatContext.getPointer(), -1, to * 1_000, 0);
        if (err < 0)
            throw new StreamException("failed to seek video: error " + err);
        lastFrame = System.nanoTime();
    }

    @Override
    public void close() {
        avcodec_free_frame(new PointerByReference(pFrame.getPointer()));
        pFrame = null;
    }

    /**
     * Builder for generating valid {@link MediaStream} objects.
     *
     * @since 1.0
     */
    public static class Builder implements MediaStream.Builder {
        public FFMedia media;
        public MediaHandler<byte[]> audioHandler;
        public MediaHandler<BufferedImage> videoHandler;

        /**
         * Creates a MediaStream builder for the specified {@link Media} object.
         *
         * @param media The container designated for streaming.
         * @since 1.0
         */
        public Builder(FFMedia media) {
            this.media = media;
        }

        /**
         * Specifies the audio stream handler.
         *
         * @param audioHandler The audio handler. Should accept byte arrays of arbitrary size as signed 16-bit PCM audio
         *                     data. Frequency and channels may be obtained from the source media container.
         * @return The current Builder.
         * @since 1.0
         */
        public Builder audio(MediaHandler<byte[]> audioHandler) {
            this.audioHandler = audioHandler;
            return this;
        }

        /**
         * Specifies the video stream handler.
         *
         * @param videoHandler The video handler. Should accept BufferedImages of arbitrary sizes.
         * @return The current Builder.
         * @since 1.0
         */
        public Builder video(MediaHandler<BufferedImage> videoHandler) {
            this.videoHandler = videoHandler;
            return this;
        }

        /**
         * Finalize creation of a {@link MediaStream}.
         *
         * @return The aforementioned stream.
         * @throws IOException           Thrown if a stream could not be established.
         * @throws IllegalStateException Thrown if no stream handlers have been specified.
         * @since 1.0
         */
        public FFMediaStream create() throws IOException {
            if (audioHandler == null && videoHandler == null)
                throw new IllegalStateException("no media handlers specified");
            return new FFMediaStream(media, audioHandler, videoHandler);
        }
    }
}
