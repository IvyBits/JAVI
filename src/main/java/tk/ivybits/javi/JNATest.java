package tk.ivybits.javi;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.Utilities;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodec;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;
import tk.ivybits.javi.ffmpeg.avformat.AVFormatContext;
import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.ffmpeg.avutil.AVLogConstants;
import tk.ivybits.javi.ffmpeg.avutil.AVMediaType;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Arrays;

import static tk.ivybits.javi.FFmpeg.*;
import static tk.ivybits.javi.ffmpeg.avcodec.AVPixelFormat.AV_PIX_FMT_BGR24;

public class JNATest {
    public static void main(String[] args) throws IOException {
        System.out.printf("Running avcodec %s, avformat %s, avutil %s.\n",
                avcodec.avcodec_version() >> 16,
                avformat.avformat_version() >> 16,
                avutil.avutil_version() >> 16);
        avutil.av_log_set_level(AVLogConstants.AV_LOG_QUIET);

        final SafeBlockingQueue<BufferedImage> emptyQueue = new SafeBlockingQueue<BufferedImage>();
        final SafeBlockingQueue<BufferedImage> filledQueue = new SafeBlockingQueue<BufferedImage>();
        final JFrame renderer = new JFrame("Rendering Test");
        renderer.setLayout(new BorderLayout());
        renderer.add(BorderLayout.CENTER, new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                BufferedImage movie = filledQueue.take();
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int width = movie.getWidth();
                int height = movie.getHeight();

                Dimension boundary = getSize();

                // Scale image dimensions with aspect ratio to fit inside the panel
                int bwidth;
                int bheight = ((bwidth = boundary.width) * height) / width;
                if (bheight > boundary.height)
                    bwidth = ((bheight = boundary.height) * width) / height;

                // Center it in the space given
                g.drawImage(movie, Math.abs(boundary.width - bwidth) / 2, Math.abs(boundary.height - bheight) / 2, bwidth, bheight, null);
                emptyQueue.put(movie);
            }
        });
        Thread repainter = new Thread() {
            public void run() {
                 while(true) {
                     renderer.repaint();
                     try {
                         Thread.sleep(1000/30);
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
            }
        };
        renderer.setSize(640, 480);

        //avformat.avformat_network_init();

        PointerByReference ppFormatCtx = new PointerByReference();

        if (avformat.avformat_open_input(ppFormatCtx, "C:/Users/Tudor/Desktop/Claymore - OP.mp4", null, null) != 0)
            throw new IOException("failed to open video file");

        AVFormatContext format = new AVFormatContext(ppFormatCtx.getValue());
        if (avformat.av_find_stream_info(format.getPointer()) < 0)
            throw new IOException("failed to find stream info");
        format.read();
        System.out.println("Streams:  " + format.nb_streams);
        System.out.println("Length:   " + Utilities.getSeconds(format.duration) + " seconds");
        System.out.println("Bitrate:  " + format.bit_rate);
        //avformat.av_dump_format(format.getPointer(), 0, new String(format.filename, "UTF-8"), 0);

        AVStream videoStream = null;
        for (int i = 0; i < format.nb_streams; ++i) {
            AVStream stream = new AVStream(format.streams.getPointer(i * Pointer.SIZE));
            if (stream.codec.codec_type == AVMediaType.AVMEDIA_TYPE_VIDEO) {
                videoStream = stream;
                break;
            }
        }
        if (videoStream == null)
            throw new RuntimeException("no video stream");
        AVCodecContext codecCtx = videoStream.codec;
        System.out.printf("Size:     (%sx%s)\n", codecCtx.width, codecCtx.height);

        AVCodec pCodec = avcodec.avcodec_find_decoder(codecCtx.codec_id);

        if (pCodec == null)
            throw new IllegalStateException("unsupported codec: " + codecCtx.codec_id);
        if (avcodec.avcodec_open2(codecCtx.getPointer(), pCodec.getPointer(), null) < 0)
            throw new IOException("could not open codec");
        for (int i = 0; i != 5; i++) {
            emptyQueue.put(new BufferedImage(videoStream.codec.width, videoStream.codec.height, BufferedImage.TYPE_3BYTE_BGR));
        }
        System.out.println(pCodec.long_name);

        codecCtx.extradata = videoStream.codec.extradata;
        codecCtx.extradata_size = videoStream.codec.extradata_size;
        AVFrame pFrame = avcodec.avcodec_alloc_frame();
        AVFrame pFrameRGB = avcodec.avcodec_alloc_frame();

        int numBytes = avcodec.avpicture_get_size(AV_PIX_FMT_BGR24, codecCtx.width, codecCtx.height);

        Pointer buffer = avutil.av_malloc(numBytes);
        int ret = avcodec.avpicture_fill(pFrameRGB.getPointer(), buffer, AV_PIX_FMT_BGR24, codecCtx.width, codecCtx.height);
        if (ret < 0 || ret != numBytes)
            throw new IOException("failed to fill frame buffer");

        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();
        avcodec.av_init_packet(packet.getPointer());
        System.out.println("Pixel format " + codecCtx.pix_fmt);
        Pointer pSwsContext = swscale.sws_getContext(codecCtx.width, codecCtx.height, codecCtx.pix_fmt,
                codecCtx.width, codecCtx.height, AV_PIX_FMT_BGR24, 0, null, null, null);
        int frame = 0;
        renderer.setVisible(true);
        repainter.start();
        while (avformat.av_read_frame(format.getPointer(), packet.getPointer()) >= 0) {
            if (packet.stream_index == videoStream.index) {
                int err = avcodec.avcodec_decode_video2(codecCtx.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                if (err < 0) {
                    System.err.print("\rerror while decoding frame " + frame + ": " + err);
                    frame++;
                }
                if (frameFinished.getValue() != 0) {
                    BufferedImage out = emptyQueue.take();
                    pFrame.read();
                    System.out.print("\rdecoded frame " + frame + ", read " + err + " bytes: " + Arrays.toString(pFrame.data));
                    swscale.sws_scale(pSwsContext, pFrame.data, pFrame.linesize, 0, codecCtx.height, pFrameRGB.data, pFrameRGB.linesize);
                    pFrameRGB.read();
                    frame++;
                    System.arraycopy(pFrameRGB.data[0].getByteArray(0, numBytes), 0, ((DataBufferByte) out.getRaster().getDataBuffer()).getData(), 0, numBytes);
                    filledQueue.put(out);
                }
            }
        }
    }
}