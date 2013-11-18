package tk.ivybits.jnml;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.Utilities;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodec;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodecContext;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPacket;
import tk.ivybits.jnml.ffmpeg.libavformat.AVFormatContext;
import tk.ivybits.jnml.ffmpeg.libavformat.AVStream;
import tk.ivybits.jnml.ffmpeg.libavutil.AVFrame;
import tk.ivybits.jnml.ffmpeg.libavutil.AVMediaType;

import java.io.IOException;
import java.util.Arrays;

import static tk.ivybits.jnml.FFmpeg.*;
import static tk.ivybits.jnml.ffmpeg.libavcodec.AVPixelFormat.AV_PIX_FMT_RGB24;

public class JNATest {
    public static void main(String[] args) throws IOException {
        System.out.printf("Running avcodec %s, avformat %s, avutil %s.\n", avcodec.avcodec_version(), avformat.avformat_version(), avutil.avutil_version());
        avformat.avformat_network_init();

        PointerByReference ppFormatCtx = new PointerByReference();

        if (avformat.avformat_open_input(ppFormatCtx, "ocanada.mp4", null, null) != 0)
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
            if (stream.codec.codec_type == AVMediaType.AVMEDIA_TYPE_VIDEO)
                videoStream = stream;
        }
        if (videoStream == null)
            throw new RuntimeException("no video stream");
        AVCodecContext codecCtx = videoStream.codec;
        System.out.printf("Size:     (%sx%s)\n", codecCtx.width, codecCtx.height);
        System.out.println("Video stream bitrate: " + codecCtx.bit_rate);
        AVCodec pCodec = avcodec.avcodec_find_decoder(codecCtx.codec_id);

        if (pCodec == null)
            throw new IllegalStateException("unsupported codec: " + codecCtx.codec_id);
        if (avcodec.avcodec_open2(codecCtx.getPointer(), pCodec.getPointer(), null) < 0)
            throw new IOException("could not open codec");

        AVFrame pFrame = avcodec.avcodec_alloc_frame();
        AVFrame pFrameRGB = avcodec.avcodec_alloc_frame();

        int numBytes = avcodec.avpicture_get_size(AV_PIX_FMT_RGB24, codecCtx.width, codecCtx.height);

        Pointer buffer = avutil.av_malloc(numBytes);
        avcodec.avpicture_fill(pFrameRGB.getPointer(), buffer, AV_PIX_FMT_RGB24, codecCtx.width, codecCtx.height);

        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();
        avcodec.av_init_packet(packet.getPointer());

        Pointer pSwsContext = swscale.sws_getContext(codecCtx.width, codecCtx.height, codecCtx.pix_fmt,
                codecCtx.width, codecCtx.height, AV_PIX_FMT_RGB24, 0, null, null, null);

        if (false) {
            System.out.println(pFrame.size());
            return;
        }
        int frame = 0;
        while (avformat.av_read_frame(format.getPointer(), packet.getPointer()) >= 0) {
            if (packet.stream_index == videoStream.index) {
                int err = avcodec.avcodec_decode_video2(codecCtx.getPointer(), pFrame.getPointer(), frameFinished, packet.getPointer());
                if (err < 0) {
                    System.err.println("\rerror while decoding frame " + frame + ": " + err);
                }
                if (frameFinished.getValue() != 0) {
                    System.out.println("\rdecoded frame " + frame);
                    frame++;
                   // swscale.sws_scale(pSwsContext, pFrame.data, pFrame.linesize, 0, codecCtx.height, pFrameRGB.data, pFrameRGB.linesize);
                    System.out.println(Arrays.toString(pFrame.data));
                }
            }
        }
    }
}
