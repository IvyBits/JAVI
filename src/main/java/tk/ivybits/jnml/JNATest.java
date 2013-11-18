package tk.ivybits.jnml;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.Utilities;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodec;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodecContext;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPacket;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPixelFormat;
import tk.ivybits.jnml.ffmpeg.libavformat.AVFormatContext;
import tk.ivybits.jnml.ffmpeg.libavformat.AVStream;
import tk.ivybits.jnml.ffmpeg.libavutil.AVFrame;
import tk.ivybits.jnml.ffmpeg.libavutil.AVMediaType;

import java.io.IOException;

import static tk.ivybits.jnml.FFmpeg.*;

public class JNATest {
    public static void main(String[] args) throws IOException {
        System.out.printf("Running avcodec %s, avformat %s, avutil %s.\n", avcodec.avcodec_version(), avformat.avformat_version(), avutil.avutil_version());
        avformat.avformat_network_init();

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
            if (stream.codec.codec_type == AVMediaType.AVMEDIA_TYPE_VIDEO)
                videoStream = stream;
        }
        if (videoStream == null)
            throw new RuntimeException("no video stream");
        AVCodecContext pCodecCtx = videoStream.codec;
        System.out.printf("Size:     (%sx%s)\n", pCodecCtx.width, pCodecCtx.height);
        System.out.println("Video stream bitrate: " + pCodecCtx.bit_rate);
        AVCodec pCodec = avcodec.avcodec_find_decoder(pCodecCtx.codec_id);

        if (pCodec == null)
            throw new IllegalStateException("unsupported codec: " + pCodecCtx.codec_id);
        if (avcodec.avcodec_open2(pCodecCtx.getPointer(), pCodec.getPointer(), null) < 0)
            throw new IOException("could not open codec");

        AVFrame.ByReference pFrame = avcodec.avcodec_alloc_frame();
        int numBytes = avcodec.avpicture_get_size(AVPixelFormat.AV_PIX_FMT_RGB24, pCodecCtx.width, pCodecCtx.height);

        System.out.println(numBytes);
        Pointer buffer = avutil.av_malloc(numBytes);
        avcodec.avpicture_fill(pFrame.getPointer(), buffer, AVPixelFormat.AV_PIX_FMT_RGB24, pCodecCtx.width, pCodecCtx.height);

        IntByReference frameFinished = new IntByReference();
        AVPacket packet = new AVPacket();

        int i = 0;
        while (avformat.av_read_frame(format.getPointer(), packet.getPointer()) >= 0) {
            if(packet.stream_index == videoStream.index) {

            }
        }
    }
}
