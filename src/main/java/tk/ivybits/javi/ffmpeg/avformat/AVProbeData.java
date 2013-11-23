package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVProbeData extends Structure {
    public static class ByReference extends AVProbeData implements Structure.ByReference {
    }

    public static class ByValue extends AVProbeData implements Structure.ByValue {
    }

    public Pointer filename;
    public Pointer buf;
    public int buf_size;

    public AVProbeData(Pointer address) {
        super(address);
        read();
    }

    public AVProbeData() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("filename", "buf", "buf_size");
    }
}
