package tk.ivybits.javi.media;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;

import static tk.ivybits.javi.ffmpeg.LibAVCodec.*;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_read_frame;
import static tk.ivybits.javi.ffmpeg.LibAVFormat.av_seek_frame;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_malloc;
import static tk.ivybits.javi.ffmpeg.LibAVUtil.av_samples_alloc_array_and_samples;
import static tk.ivybits.javi.ffmpeg.LibSWResample.*;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_getContext;
import static tk.ivybits.javi.ffmpeg.LibSWScale.sws_scale;
import static tk.ivybits.javi.format.PixelFormat.BGR24;
import static tk.ivybits.javi.format.SampleFormat.SIGNED_16BIT;

/**
 * A media stream.
 * </p>
 * Cannot be instantiated directly: use {@link MediaStream.Builder}.
 *
 * @version 1.0
 * @since 1.0
 */
public class MediaStream extends Thread {
    private final Media media;
    private MediaHandler<byte[]> audioHandler;
    private MediaHandler<BufferedImage> videoHandler;

    private AVFrame.ByReference pBGRFrame;
    private AVFrame.ByReference pFrame;

    private AVCodec videoCodec, audioCodec;
    private long lastFrame = 0;
    private boolean playing = false;

    MediaStream(Media media, MediaHandler<byte[]> audioHandler, MediaHandler<BufferedImage> videoHandler) throws IOException {
        this.media = media;
        this.audioHandler = audioHandler;
        this.videoHandler = videoHandler;
        if (videoHandler != null && media.hasStream(StreamType.STREAM_VIDEO)) {
            videoCodec = avcodec_find_decoder(media.videoContext.codec_id);
            if (videoCodec == null || avcodec_open2(media.videoContext.getPointer(), videoCodec.getPointer(), null) < 0) {
                throw new IOException("unsupported video codec");
            }
            media.videoContext.read();
            videoCodec.read();

            pBGRFrame = avcodec_alloc_frame();
            int size = avpicture_get_size(BGR24.ordinal(), media.width(), media.height());

            Pointer buffer = av_malloc(size);
            int ret = avpicture_fill(pBGRFrame.getPointer(), buffer, BGR24.ordinal(), media.width(), media.height());
            if (ret < 0 || ret != size)
                throw new IOException("failed to fill frame buffer");
        }
        if (audioHandler != null && media.hasStream(StreamType.STREAM_AUDIO)) {
            audioCodec = avcodec_find_decoder(media.audioContext.codec_id);
            if (audioCodec == null || avcodec_open2(media.audioContext.getPointer(), audioCodec.getPointer(), null) < 0) {
                throw new IOException("unsupported video codec");
            }
            media.audioContext.read();
            audioCodec.read();
        }

        pFrame = avcodec_alloc_frame();
    }

    @Override
    public synchronized void start() {
        super.start();
        playing = true;
    }

    /**
     * Starts synchronous streaming.
     *
     * @since 1.0
     */
    public void run() {
        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();
        av_init_packet(packet.getPointer());
        int width = media.width();
        int height = media.height();
        AVCodecContext ac = media.audioContext;
        AVCodecContext vc = media.videoContext;

        Pointer pSwsContext = sws_getContext(
                width, height, vc.pix_fmt,
                width, height, BGR24.ordinal(),
                0, null, null, null);

        Pointer pSwrContext = swr_alloc_set_opts(
                null, 3, SIGNED_16BIT.ordinal(),
                ac.sample_rate, ac.channel_layout, ac.sample_fmt,
                ac.sample_rate, 0, null);
        swr_init(pSwrContext);
        ac.read();

        BufferedImage imageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] audioBuffer = new byte[16 * ac.channels * 64]; // Estimate common size

        media.videoStream.read();
        media.videoContext.read();

        lastFrame = System.nanoTime();
        _outer:
        while (av_read_frame(media.formatContext.getPointer(), packet.getPointer()) >= 0) {
            synchronized (this) {
                if (!playing)
                    try {
                        wait();
                        lastFrame = System.nanoTime();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

            packet.read();

            if (packet.stream_index == media.audioStream.index) {
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
                        continue _outer; // handle was not read
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
                                throw new RuntimeException("failed to allocate destination buffer: " + err);
                            }
                            int length = dstLinesize.getValue();
                            err = swr_convert(pSwrContext, dstData.getValue(), length,
                                    pFrame.extended_data, pFrame.nb_samples);
                            if (err < 0)
                                throw new RuntimeException("failed to transcode audio: " + err);
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
            } else if (packet.stream_index == media.videoStream.index) { // We only care about the media media here
                // Decode the media into our pFrame
                int err = avcodec_decode_video2(media.videoContext.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                // If the return of avcodec_decode_video2 is negative, an error occurred.
                // Fun fact: the error is actually the negative of an ASCII string in little-endian order.
                if (err < 0) {
                    continue; // handle was not read
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

                    long duration = pFrame.pkt_duration * 1000000000 * media.videoStream.time_base.num / media.videoStream.time_base.den;
                    long time = System.nanoTime();
                    duration -= time - lastFrame;
                    //System.out.println("Duration: " + pFrame.pkt_duration + " " + duration);
                    videoHandler.handle(imageBuffer, duration / 1000000);
                    // Add in duration, which is the time that is spent waiting for the frame to render, so we get
                    // the time when this frame is rendered, and set it as the last frame.
                    // If duration is NEGATIVE, nothing will be rendered. We basically are subtracting the overdue
                    // time from time we started handling this frame, so we get the time on which the current frame
                    // should be rendered. If multiple frames are skipped, this still works, as the lastFrame will
                    // advance by the length of each lost frame until it goes back to sync,
                    // i.e. duration is back to positive.
                    lastFrame = time + duration;
                }
            }
            // Free the packet that av_read_frame allocated
            av_free_packet(packet.getPointer());
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
     * @since 1.0
     */
    public synchronized void setPlaying(boolean flag) {
        playing = flag;
        notify();
    }

    /**
     * Sets the current position of the stream, in milliseconds.
     *
     * @param to The position to seek to.
     * @throws IllegalArgumentException Thrown if the seek position is invalid.
     * @throws IOException Thrown if the seek failed.
     * @since 1.0
     */
    public void seek(long to) throws IOException {
        if (to < 0)
            throw new IllegalArgumentException("negative position");
        if (to > media.length())
            throw new IllegalArgumentException("position greater then video length");
        if (av_seek_frame(media.formatContext.getPointer(), -1, to * 1000, 0) == 0)
            throw new IOException("failed to seek video");
        lastFrame = System.nanoTime();
    }


    /**
     * Builder for generating valid {@link MediaStream} objects.
     *
     * @since 1.0
     */
    public static class Builder {
        private Media media;
        private MediaHandler<byte[]> audioHandler;
        private MediaHandler<BufferedImage> videoHandler;

        /**
         * Creates a MediaStream builder for the specified {@link Media} object.
         *
         * @param media The container designated for streaming.
         * @since 1.0
         */
        public Builder(Media media) {
            this.media = media;
        }

        /**
         * Specifies the audio stream handler.
         *
         * @param audioHandler The audio handler. Should accept byte arrays of arbitrary size as
         *                     signed 16-bit PCM audio data. Frequency and channels may be obtained
         *                     from the source media container.
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
         * @param videoHandler The audio handler. Should accept BufferedImages of arbitrary sizes.
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
        public MediaStream create() throws IOException {
            if (audioHandler == null && videoHandler == null)
                throw new IllegalStateException("no media handlers specified");
            return new MediaStream(media, audioHandler, videoHandler);
        }
    }
}
