package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.libavutil.AVClass;

import java.util.Arrays;
import java.util.List;

public class AVIOContext extends Structure {
    public static class ByReference extends AVIOContext implements Structure.ByReference {}
    public static class ByValue extends AVIOContext implements Structure.ByValue {}

    public AVClass.ByReference av_class;
    public Pointer buffer;
    public int buffer_size;
    public Pointer buf_ptr;
    public Pointer buf_end;
    public Pointer opaque;
    public read_packet_callback read_packet;
    public write_packet_callback write_packet;
    public seek_callback seek;
    public long pos;
    public int must_flush;
    public int eof_reached;
    public int write_flag;
    public int max_packet_size;
    public long checksum;
    public Pointer checksum_ptr;
    public update_checksum_callback update_checksum;
    public int error;
    public read_pause_callback read_pause;
    public read_seek_callback read_seek;
    public int seekable;
    public long maxsize;
    public int direct;

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("av_class", "buffer", "buffer_size", "buf_ptr", "buf_end",
                "opaque", "read_packet", "write_packet", "seek", "pos",
                "must_flush", "eof_reached", "write_flag", "max_packet_size",
                "checksum", "checksum_ptr", "update_checksum", "error",
                "read_pause", "read_seek", "seekable", "maxsize", "direct");
    }

    public interface read_packet_callback extends Callback {
        int apply(Pointer opaque, Pointer buf, int buf_size);
    }

    public interface write_packet_callback extends Callback {
        int apply(Pointer opaque, Pointer buf, int buf_size);
    }

    public interface seek_callback extends Callback {
        long apply(Pointer opaque, long offset, int whence);
    }

    public interface update_checksum_callback extends Callback {
        long apply(long checksum, Pointer buf, int size);
    }

    public interface read_pause_callback extends Callback {
        int apply(Pointer opaque, int pause);
    }

    public interface read_seek_callback extends Callback {
        long apply(Pointer opaque, int stream_index, long timestamp, int flags);
    }

    public AVIOContext(Pointer address) {
        super(address);
        read();
    }

    public AVIOContext() {
        super();
    }
}