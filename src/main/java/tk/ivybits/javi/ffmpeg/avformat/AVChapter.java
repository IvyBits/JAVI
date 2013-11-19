package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import tk.ivybits.javi.ffmpeg.avutil.AVDictionary;
import tk.ivybits.javi.ffmpeg.avutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVChapter extends Structure {
    public static class ByReference extends AVChapter implements Structure.ByReference {
    }

    public static class ByValue extends AVChapter implements Structure.ByValue {
    }

    public int id;
    public AVRational.ByValue time_base;
    public long start;
    public long end;
    public AVDictionary metadata;

    public AVChapter(Pointer address) {
        super(address);
        read();
    }

    public AVChapter() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("id", "time_base", "start", "end", "metadata");
    }
}
