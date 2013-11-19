package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avutil.AVClass;

import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around the public parts of AVInputFormat. Please do not instantiate from Java.
 */
public class AVInputFormat extends Structure {
    public static class ByReference extends AVInputFormat implements Structure.ByReference {
    }

    public static class ByValue extends AVInputFormat implements Structure.ByValue {
    }

    public Pointer name;
    public Pointer long_name;
    public int flags;
    public Pointer extensions;
    public PointerByReference codec_tag;
    public AVClass priv_class;

    public AVInputFormat(Pointer address) {
        super(address);
        read();
    }

    public AVInputFormat() {
        super();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("name", "long_name", "flags", "extensions", "codec_tag", "priv_class");
    }
}
