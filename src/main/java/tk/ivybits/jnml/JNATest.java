package tk.ivybits.jnml;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.Utilities;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPacket;
import tk.ivybits.jnml.ffmpeg.libavformat.AVFormatContext;
import tk.ivybits.jnml.ffmpeg.libavformat.AVStream;
import tk.ivybits.jnml.ffmpeg.libavutil.AVMediaType;

import java.io.IOException;

import static tk.ivybits.jnml.FFmpeg.libavcodec;
import static tk.ivybits.jnml.FFmpeg.libavformat;

public class JNATest {
    public static void main(String[] args) throws IOException {
        System.out.printf("Running libavcodec %s, libavformat %s.\n", libavcodec.avcodec_version(), libavformat.avformat_version());
        libavformat.avformat_network_init();

        PointerByReference ppFormatCtx = new PointerByReference();

        if (libavformat.avformat_open_input(ppFormatCtx, "http://dl.dropboxusercontent.com/u/36712017/music/O%20Canada.mp3", null, null) != 0)
            throw new IOException("failed to open video file");

        AVFormatContext format = new AVFormatContext(ppFormatCtx.getValue());
        if (libavformat.av_find_stream_info(format.getPointer()) < 0)
            throw new IOException("failed to find stream info");
        format.read();
        System.out.println("Streams:  " + format.nb_streams);
        System.out.println("Length:   " + Utilities.getSeconds(format.duration) + " seconds");
        System.out.println("Bitrate:  " + format.bit_rate);

        AVStream audioStream = null;
        for (int i = 0; i < format.nb_streams; ++i) {
            AVStream stream = new AVStream(format.streams.getPointer(i * Pointer.SIZE));
            if (stream.codec.codec_type == AVMediaType.AVMEDIA_TYPE_AUDIO)
                audioStream = stream;
        }
        if (audioStream == null)
            throw new RuntimeException("No audio stream");
        System.out.println("Audio stream bitrate: " + audioStream.codec.bit_rate);
    }
}
