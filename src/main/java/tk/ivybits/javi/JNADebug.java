package tk.ivybits.javi;

import com.sun.jna.Structure;
import tk.ivybits.javi.ffmpeg.StructureUtilities;

public class JNADebug {
    public static final String[] classes = {
            "avcodec.AVCodec",
            "avcodec.AVCodecContext",
            "avcodec.AVPacket",
            "avformat.AVChapter",
            "avformat.AVFormatContext",
            "avformat.AVFrac",
            "avformat.AVInputFormat",
            "avformat.AVIOContext",
            "avformat.AVIOInterruptCB",
            "avformat.AVOutputFormat",
            "avformat.AVPacketList",
            "avformat.AVProbeData",
            "avformat.AVProgram",
            "avformat.AVStream",
            "avutil.AVClass",
            "avutil.AVFrame",
            "avutil.AVRational",
    };

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        for (String clazz : classes) {
            Class<?> struct = Class.forName("tk.ivybits.javi.ffmpeg." + clazz);
            Structure victim = (Structure) struct.newInstance();
            StructureUtilities.printFields(victim);
            System.out.println();
        }
    }
}
