package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.libavutil.AVDictionary;

import java.util.Arrays;
import java.util.List;

public class AVProgram extends Structure {
    public static class ByReference extends AVProgram implements Structure.ByReference {
    }

    public static class ByValue extends AVProgram implements Structure.ByValue {
    }

    public int id;
    public int flags;
    public int discard;
    public Pointer stream_index;
    public int nb_stream_indexes;
    public AVDictionary metadata;
    public int program_num;
    public int pmt_pid;
    public int pcr_pid;

    public AVProgram(Pointer address) {
        super(address);
        read();
    }

    public AVProgram() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("id", "flags", "discard", "stream_index", "nb_stream_indexes",
                "metadata", "program_num", "pmt_pid", "pcr_pid");
    }
}
