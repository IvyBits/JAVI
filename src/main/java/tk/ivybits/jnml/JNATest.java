package tk.ivybits.jnml;

import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavformat.AVFormatContext;

import static tk.ivybits.jnml.FFmpeg.*;

public class JNATest {
    public static void main(String[] args) {
        System.out.println(libavcodec.avcodec_version());

        PointerByReference byref = new PointerByReference();
        libavformat.avformat_open_input(byref, "ocanada.mp4", null, null);
        AVFormatContext format = new AVFormatContext(byref.getValue());
        System.out.println(new String(format.filename));
        System.out.println(format.nb_streams);
        System.out.println(format.duration);
    }
}
