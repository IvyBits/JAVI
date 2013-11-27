package tk.ivybits.javi.media.ffmedia;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.exc.StreamException;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.media.MediaHandler;
import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.VideoStream;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.concurrent.Semaphore;

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
 * FFmpeg MediaStream implementation.
 * </p>
 * Cannot be instantiated directly.
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

    public AVFrame.ByReference pBGRFrame;
    public AVFrame.ByReference pFrame;

    public AVCodec videoCodec, audioCodec;
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
     * {@inheritDoc}
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
     * {@inheritDoc}
     */
    public AudioStream setAudioStream(AudioStream stream) {
        if (stream.container() != media)
            throw new IllegalArgumentException("stream not from same container");
        AudioStream pre = audioStream;
        audioStream = (FFAudioStream) stream;
        audioCodec = audioStream.codec;
        return pre;
    }

    /**
     * {@inheritDoc}
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
            } else if (videoStream != null && packet.stream_index == videoStream.index()) { // We only care about the media media here
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
                    videoHandler.handle(imageBuffer, duration);
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
            mutex.release();
        }
        videoHandler.end();
        audioHandler.end();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPlaying() {
        return playing;
    }

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        avcodec_free_frame(new PointerByReference(pFrame.getPointer()));
        pFrame = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FFAudioStream getAudioStream() {
        return audioStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FFVideoStream getVideoStream() {
        return videoStream;
    }

    /**
     * {@inheritDoc}
     */
    public static class Builder implements MediaStream.Builder {
        public FFMedia media;
        public MediaHandler<byte[]> audioHandler;
        public MediaHandler<BufferedImage> videoHandler;

        /**
         * {@inheritDoc}
         */
        public Builder(FFMedia media) {
            this.media = media;
        }

        /**
         * {@inheritDoc}
         */
        public Builder audio(MediaHandler<byte[]> audioHandler) {
            this.audioHandler = audioHandler;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public Builder video(MediaHandler<BufferedImage> videoHandler) {
            this.videoHandler = videoHandler;
            return this;
        }

        /**
         * {@inheritDoc}
         */
        public FFMediaStream create() throws IOException {
            if (audioHandler == null && videoHandler == null)
                throw new IllegalStateException("no media handlers specified");
            return new FFMediaStream(media, audioHandler, videoHandler);
        }
    }
}
