package tk.ivybits.jnml;

import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.Utilities;
import tk.ivybits.jnml.ffmpeg.libavformat.AVFormatContext;

import java.io.IOException;

import static tk.ivybits.jnml.FFmpeg.libavcodec;
import static tk.ivybits.jnml.FFmpeg.libavformat;

public class JNATest {
    public static void main(String[] args) throws IOException {
        System.out.printf("Running libavcodec %s, libavformat %s.\n", libavcodec.avcodec_version(), libavformat.avformat_version());
        //libavformat.av_register_all();
        libavformat.avformat_network_init();

        PointerByReference ppFormatCtx = new PointerByReference();

        if (libavformat.avformat_open_input(ppFormatCtx, "C:/Users/Tudor/Desktop/Rolling in the Deep.mp4", null, null) != 0)
            throw new IOException("failed to open video file");

        AVFormatContext.ByReference format = new AVFormatContext.ByReference(ppFormatCtx.getValue());
        if (libavformat.av_find_stream_info(format) < 0)
            throw new IOException("failed to find stream info");
        System.out.println("Streams:  " + format.nb_streams);
        System.out.println("Length:   " + Utilities.getSeconds(format.duration) + " seconds");
        System.out.println("Bitrate:  " + format.bit_rate);
    }
}
