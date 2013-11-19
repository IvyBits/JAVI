package tk.ivybits.jnml;

import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.StructureUtilities;

public class JNADebug {
    public static final String[] classes = {
            "libavcodec.AVCodec",
            "libavcodec.AVCodecContext",
            "libavcodec.AVPacket",
            "libavformat.AVChapter",
            "libavformat.AVFormatContext",
            "libavformat.AVFrac",
            "libavformat.AVInputFormat",
            "libavformat.AVIOContext",
            "libavformat.AVIOInterruptCB",
            "libavformat.AVOutputFormat",
            "libavformat.AVPacketList",
            "libavformat.AVProbeData",
            "libavformat.AVProgram",
            "libavformat.AVStream",
            "libavutil.AVClass",
            "libavutil.AVFrame",
            "libavutil.AVRational",
    };

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (String clazz : classes) {
            Class<?> struct = Class.forName("tk.ivybits.jnml.ffmpeg." + clazz);
            Structure victim = (Structure) struct.newInstance();
            StructureUtilities.printFields(victim);
            System.out.println();
        }
    }
}
