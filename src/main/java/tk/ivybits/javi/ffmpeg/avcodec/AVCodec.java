package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.LongByReference;
import tk.ivybits.javi.ffmpeg.avutil.AVClass;
import tk.ivybits.javi.ffmpeg.avutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVCodec extends Structure {
    public static class ByReference extends AVCodec implements Structure.ByReference {
    }

    public static class ByValue extends AVCodec implements Structure.ByValue {
    }

    public String name;

    public String long_name;
    public int type;
    public int id;

    public int capabilities;
    public AVRational.ByReference supported_framerates;
    public Pointer pix_fmts;

    public Pointer supported_samplerates;
    public Pointer sample_fmts;
    public LongByReference channel_layouts;
    public byte max_lowres;
    public AVClass.ByReference priv_class;
    public Pointer /* AVProfile.ByReference */ profiles;

    public AVCodec(Pointer address) {
        super(address);
        read();
    }

    public AVCodec() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("name", "long_name", "type", "id", "capabilities",
                "supported_framerates", "pix_fmts", "supported_samplerates",
                "sample_fmts", "channel_layouts", "max_lowres", "priv_class", "profiles");
    }
}
