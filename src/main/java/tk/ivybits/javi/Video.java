package tk.ivybits.javi;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.ffmpeg.avutil.AVMediaType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
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
    private final AVCodecContext.ByReference vCodecCtx, aCodecCtx;
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
        private int aBufferSize;

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
            Pointer pSwsContext = swscale.sws_getContext(vCodecCtx.width, vCodecCtx.height, vCodecCtx.pix_fmt, vCodecCtx.width, vCodecCtx.height, AV_PIX_FMT_BGR24, 0, null, null, null);


            AudioFormat af = new AudioFormat(aCodecCtx.sample_rate, 16, 2, true, false);
            SourceDataLine sdl = null;
            try {
                sdl = AudioSystem.getSourceDataLine(af);
                sdl.open(af);
                sdl.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (avformat.av_read_frame(format.getPointer(), packet.getPointer()) >= 0) {
                packet.read();
                if (packet.stream_index == audioStream.index) {
                    System.out.println("Audio: " + packet.size);
                    // Decode the video into our pFrame
                    int err = avcodec.avcodec_decode_audio4(aCodecCtx.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());

                    if (err < 0) {
                        System.err.println("Error reading audio frame: " + err);
                        continue; // frame was not read
                    }

                    if (frameFinished.getValue() != 0) {
                        aBufferSize = avutil.av_samples_get_buffer_size(null, aCodecCtx.channels, pFrame.nb_samples, aCodecCtx.sample_fmt, 1);
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
        aCodecCtx = audioStream != null ? audioStream.codec : null;
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
        for (int i = 0; i != 3; i++) {
            aEmptyQueue.put(new byte[65536]);
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

    public void close() {
        if (streamer != null)
            streamer.stop();
    }
}
