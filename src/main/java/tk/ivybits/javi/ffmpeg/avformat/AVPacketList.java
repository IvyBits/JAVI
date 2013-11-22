package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import tk.ivybits.javi.ffmpeg.avcodec.AVPacket;

import java.util.Arrays;
import java.util.List;

public class AVPacketList extends Structure {
    public static class ByReference extends AVPacketList implements Structure.ByReference {
    }

    public static class ByValue extends AVPacketList implements Structure.ByValue {
    }

    public AVPacket pkt;
    public Pointer next;

    public AVPacketList(Pointer address) {
        super(address);
        read();
    }

    public AVPacketList() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("pkt", "next");
    }
}