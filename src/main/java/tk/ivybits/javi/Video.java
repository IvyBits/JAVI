package tk.ivybits.javi;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;
import tk.ivybits.javi.ffmpeg.avcodec.AVSampleFormat;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.ffmpeg.avutil.AVMediaType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;

import static tk.ivybits.javi.FFmpeg.*;
import static tk.ivybits.javi.ffmpeg.avcodec.AVPixelFormat.AV_PIX_FMT_BGR24;

/**
 * Tudor
 * 2013-11-19
 */
public class Video implements Closeable {
    private final AVFormatContext format;
    private final AVCodecContext vCodecCtx, aCodecCtx;
    private final AVCodec vCodec, aCodec;
    protected SafeBlockingQueue<BufferedImage> vEmptyQueue = new SafeBlockingQueue<>();
    protected SafeBlockingQueue<BufferedImage> vFilledQueue = new SafeBlockingQueue<>();
    protected SafeBlockingQueue<byte[]> aEmptyQueue = new SafeBlockingQueue<>();
    protected SafeBlockingQueue<byte[]> aFilledQueue = new SafeBlockingQueue<>();
    protected AVStream videoStream, audioStream;
    protected StreamThread streamer;

    public BufferedImage fetchFrameData() {
        if (streamer == null)
            throw new IllegalStateException("stream not initialized: call stream()");
        return vFilledQueue.take();
    }

    public void freeFrameData(BufferedImage ptr) {
        if (streamer == null)
            throw new IllegalStateException("stream not initialized: call stream()");
        vEmptyQueue.put(ptr);
    }

    public byte[] fetchPCMData() {
        if (streamer == null)
            throw new IllegalStateException("stream not initialized: call stream()");
        return aFilledQueue.take();
    }

    public void freePCMData(byte[] ptr) {
        if (streamer == null)
            throw new IllegalStateException("stream not initialized: call stream()");
        aEmptyQueue.put(ptr);
    }

    public class StreamThread extends Thread {
        private AVFrame.ByReference pFrameRGB;
        private AVFrame.ByReference pFrame;
        private int vBufferSize;

        public StreamThread() throws IOException {
            pFrame = avcodec.avcodec_alloc_frame();
            pFrameRGB = avcodec.avcodec_alloc_frame();

            vBufferSize = avcodec.avpicture_get_size(AV_PIX_FMT_BGR24, vCodecCtx.width, vCodecCtx.height);

            Pointer buffer = avutil.av_malloc(vBufferSize);
            int ret = avcodec.avpicture_fill(pFrameRGB.getPointer(), buffer, AV_PIX_FMT_BGR24, vCodecCtx.width, vCodecCtx.height);
            if (ret < 0 || ret != vBufferSize)
                throw new IOException("failed to fill frame buffer");
        }

        public void run() {
            IntByReference frameFinished = new IntByReference();
            AVPacket packet = new AVPacket();
            avcodec.av_init_packet(packet.getPointer());
            Pointer pSwsContext = swscale.sws_getContext(
                    vCodecCtx.width,
                    vCodecCtx.height,
                    vCodecCtx.pix_fmt,
                    vCodecCtx.width,
                    vCodecCtx.height,
                    AV_PIX_FMT_BGR24,
                    0,
                    null,
                    null,
                    null);
            Pointer pSwrContext = swresample.swr_alloc_set_opts(
                    null,
                    3,
                    AVSampleFormat.AV_SAMPLE_FMT_S16P,
                    aCodecCtx.sample_rate,
                    aCodecCtx.channel_layout,
                    aCodecCtx.sample_fmt,
                    aCodecCtx.sample_rate,
                    0,
                    null
            );
            swresample.swr_init(pSwrContext);
            //System.out.println(aCodecCtx.sample_fmt);
            //if(true)System.exit(0);

            AudioFormat af = new AudioFormat(aCodecCtx.sample_rate, 16, 1, true, false);
            SourceDataLine sdl = null;
            try {
                sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            aCodecCtx.read();
            _outer:
            while (avformat.av_read_frame(format.getPointer(), packet.getPointer()) >= 0) {
                packet.read();
                if (packet.stream_index == audioStream.index) {
                    // Decode the video into our pFrame
                    int read = 0;

                    // According to FFmpeg docs:
                    // Some decoders may support multiple frames in a single AVPacket.
                    // Such decoders would then just decode the first frame and the return value
                    // would be less than the packet size. In this case, avcodec_decode_audio4 has
                    // to be called again with an AVPacket containing the remaining data in order to
                    // decode the second frame, etc... Even if no frames are returned, the packet needs
                    // to be fed to the decoder with remaining data until it is completely consumed or
                    // an error occurs.
                    // Implemented the first two sentences. Not sure about the last.
                    while (read < packet.size) {
                        int err = avcodec.avcodec_decode_audio4(aCodecCtx.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());

                        if (err < 0) {
                            System.err.print("\rError reading audio frame " + packet.size + ": " + err + "\n");
                            continue _outer; // frame was not read
                        } else {
                            read += err;
                            System.err.print("\rSuccessfully read " + read + " / " + packet.size);
                        }

                        if (frameFinished.getValue() != 0) {
                            pFrame.read();
                            if (aCodecCtx.sample_fmt != AVSampleFormat.AV_SAMPLE_FMT_S16P) {
                                PointerByReference dstData = new PointerByReference();
                                IntByReference dstLinesize = new IntByReference();
                                int ret = avutil.av_samples_alloc_array_and_samples(dstData, dstLinesize, pFrame.channels,
                                        pFrame.nb_samples, AVSampleFormat.AV_SAMPLE_FMT_S16P, 0);
                                if (ret < 0) {
                                    throw new RuntimeException("failed to allocate destination buffer: " + ret);
                                }
                                int length = dstLinesize.getValue();
                                ret = swresample.swr_convert(pSwrContext, dstData.getValue(), length,
                                        pFrame.extended_data, pFrame.nb_samples);
                                if(ret < 0)
                                    throw new RuntimeException("failed to transcode audio: " + ret);

                             sdl.write(dstData.getValue().getPointer(0).getByteArray(0, length), 0, length);
                            }
                        } else {
                            sdl.write(pFrame.data[0].getByteArray(0, pFrame.linesize[0]), 0, pFrame.linesize[0]);
                        }
                    }
                } else if (packet.stream_index == videoStream.index) { // We only care about the video stream here
                    // Decode the video into our pFrame
                    int err = avcodec.avcodec_decode_video2(vCodecCtx.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                    // If the return of avcodec_decode_video2 is negative, an error occured.
                    // Fun fact: the error is actually the negative of an ASCII string in little-endian order.
                    if (err < 0) {
                        continue; // frame was not read
                    }
                    if (frameFinished.getValue() != 0) {
                        pFrame.read();
                        swscale.sws_scale(pSwsContext, pFrame.data, pFrame.linesize, 0, vCodecCtx.height, pFrameRGB.data, pFrameRGB.linesize);
                        // Only data and linesize were changed in the sws_scale call: we only need update them
                        pFrameRGB.readField("data");
                        pFrameRGB.readField("linesize");
                        BufferedImage out = vEmptyQueue.take();
                        // A frame may be of arbitrary size: size is not required to be constant.
                        // If you decided to encode your files such that size changes, it is
                        // you who will feel the pain of reallocating the buffers.
                        // You have been warned.
                        if (out.getWidth() != vCodecCtx.width || out.getHeight() != vCodecCtx.height) {
                            out = new BufferedImage(vCodecCtx.width, vCodecCtx.height, BufferedImage.TYPE_3BYTE_BGR);
                        }
                        // Read the frame buffer directly into the raster of our image
                        pFrameRGB.data[0].read(0, ((DataBufferByte) out.getRaster().getDataBuffer()).getData(), 0, vBufferSize);
                        // And mark it as available to be rendered
                        vFilledQueue.put(out);
                    }
                }
                // Free the packet that av_read_frame allocated
                avcodec.av_free_packet(packet.getPointer());
            }
        }
    }

    public Video(File source) throws IOException {
        this(source.getAbsolutePath());
    }

    public Video(URI source) throws IOException {
        this(source.toASCIIString());
    }

    private Video(String source) throws IOException {
        PointerByReference ppFormatCtx = new PointerByReference();

        source = URLDecoder.decode(source, "UTF-8");
        if (avformat.avformat_open_input(ppFormatCtx, source, null, null) != 0)
            throw new IOException("failed to open video file: " + source);

        format = new AVFormatContext(ppFormatCtx.getValue());
        if (avformat.av_find_stream_info(format.getPointer()) < 0)
            throw new IOException("failed to find stream info");
        format.read();

        for (int i = 0; i < format.nb_streams; ++i) {
            AVStream stream = new AVStream(format.streams.getPointer(i * Pointer.SIZE));
            int type = stream.codec.codec_type;
            switch (type) {
                case AVMediaType.AVMEDIA_TYPE_VIDEO:
                    videoStream = stream;
                    break;
                case AVMediaType.AVMEDIA_TYPE_AUDIO:
                    audioStream = stream;
                    break;
            }
        }
        if (videoStream == null)
            throw new IOException("no video stream");
        vCodecCtx = videoStream.codec;
        vCodec = avcodec.avcodec_find_decoder(vCodecCtx.codec_id);
        if (vCodec == null || avcodec.avcodec_open2(vCodecCtx.getPointer(), vCodec.getPointer(), null) < 0) {
            System.err.println("Unsupported video codec!");
            System.exit(2);
        }
        vCodecCtx.read();
        vCodec.read();
        System.out.println("Video codec: " + vCodec.long_name);
        if (audioStream != null) {
            aCodecCtx = audioStream.codec;
            aCodec = avcodec.avcodec_find_decoder(aCodecCtx.codec_id);
            if (aCodec == null || avcodec.avcodec_open2(aCodecCtx.getPointer(), aCodec.getPointer(), null) < 0) {
                System.err.println("Unsupported video codec!");
                System.exit(2);
            }
            aCodecCtx.read();
            aCodec.read();
            System.out.println("Audio codec: " + aCodec.long_name);
        } else {
            aCodecCtx = null;
            aCodec = null;
        }
    }

    public void stream() throws IOException {
        AVCodec pCodec = avcodec.avcodec_find_decoder(vCodecCtx.codec_id);

        if (pCodec == null)
            throw new IllegalStateException("unsupported codec: " + vCodecCtx.codec_id);
        if (avcodec.avcodec_open2(vCodecCtx.getPointer(), pCodec.getPointer(), null) < 0)
            throw new IOException("could not open codec");
        for (int i = 0; i != 3; i++) {
            vEmptyQueue.put(new BufferedImage(videoStream.codec.width, videoStream.codec.height, BufferedImage.TYPE_3BYTE_BGR));
        }
        streamer = new StreamThread();
        streamer.start();
    }

    public boolean hasAudio() {
        return aCodecCtx != null;
    }

    public AudioFormat audioFormat() {
        if (aCodecCtx == null)
            throw new IllegalStateException("video does not contain audio stream");
        return new AudioFormat(aCodecCtx.sample_rate, 16, 2, true, false);
    }

    @Override
    public void close() {
        if (streamer != null)
            streamer.stop();
    }

    public Dimension dimensions() {
        return new Dimension(videoStream.codec.width, videoStream.codec.height);
    }

    public int width() {
        return videoStream.codec.width;
    }

    public int height() {
        return videoStream.codec.width;
    }

    public float aspectRatio() {
        return width() / (float) height();
    }
}
