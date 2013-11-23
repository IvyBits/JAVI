package tk.ivybits.javi.ffmpeg.avutil;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;
import tk.ivybits.javi.ffmpeg.avcodec.AVCodecContext;

import java.util.Arrays;
import java.util.List;

public class AVFrame extends Structure {
    public static class ByReference extends AVFrame implements Structure.ByReference {
    }

    public static class ByValue extends AVFrame implements Structure.ByValue {
    }

    public Pointer[] data = new Pointer[AV_NUM_DATA_POINTERS];
    public int[] linesize = new int[AV_NUM_DATA_POINTERS];
    public Pointer extended_data;
    public int width, height;
    public int nb_samples;
    public int format;
    public int key_frame;
    public int pict_type;
    @Deprecated
    public ByteByReference[] base = new ByteByReference[AV_NUM_DATA_POINTERS];
    public AVRational sample_aspect_ratio;
    public long pts;
    public long pkt_pts;
    public long pkt_dts;
    public int coded_picture_number;
    public int display_picture_number;
    public int quality;
    @Deprecated
    public int reference;
    @Deprecated
    public ByteByReference qscale_table;
    @Deprecated
    public int qstride;
    @Deprecated
    public int qscale_type;
    @Deprecated
    public ByteByReference mbskip_table;
    @Deprecated
    public Pointer[] motion_val = new Pointer[2];
    @Deprecated
    public IntByReference mb_type;
    public ShortByReference dct_coeff;
    @Deprecated
    public ByteByReference[] ref_index = new ByteByReference[2];
    public Pointer opaque;
    public long[] error = new long[AV_NUM_DATA_POINTERS];
    @Deprecated
    public int type;
    public int repeat_pict;
    public int interlaced_frame;
    public int top_field_first;
    public int palette_has_changed;
    @Deprecated
    public int buffer_hints;
    @Deprecated
    public Pointer pan_scan;
    public long reordered_opaque;
    @Deprecated
    public Pointer hwaccel_picture_private;
    @Deprecated
    public AVCodecContext.ByReference owner;
    @Deprecated
    public Pointer thread_opaque;
    @Deprecated
    public byte motion_subsample_log2;
    public int sample_rate;
    public long channel_layout;
    public Pointer[] /* AVBufferRef[] */ buf = new Pointer[AV_NUM_DATA_POINTERS];
    public PointerByReference extended_buf;
    public int nb_extended_buf;
    public PointerByReference side_data;
    public int nb_side_data;
    public int flags;
    public long best_effort_timestamp;
    public long pkt_pos;
    public long pkt_duration;
    public AVDictionary metadata;
    public int decode_error_flags;
    public int channels;
    public int pkt_size;
    public int colorspace;
    public int color_range;
    public Pointer qp_table_buf;

    public static final int AV_NUM_DATA_POINTERS = 8;


    public AVFrame(Pointer address) {
        super(address);
        read();
    }

    public AVFrame() {
        super();
    }

    public Pointer getPointerToOffset(int offset) {
        return new Pointer(Pointer.nativeValue(getPointer()) + offset);
    }

    public Pointer getPointerToOffset(String name) {
        return getPointerToOffset(fieldOffset(name));
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("data", "linesize", "extended_data", "width", "height", "nb_samples", "format",
                "key_frame", "pict_type", "base", "sample_aspect_ratio", "pts", "pkt_pts", "pkt_dts",
                "coded_picture_number", "display_picture_number", "quality", "reference",
                "qscale_table", "qstride", "qscale_type", "mbskip_table", "motion_val", "mb_type",
                "dct_coeff", "ref_index", "opaque", "error", "type", "repeat_pict", "interlaced_frame",
                "top_field_first", "palette_has_changed", "buffer_hints", "pan_scan", "reordered_opaque",
                "hwaccel_picture_private", "owner", "thread_opaque", "motion_subsample_log2",
                "sample_rate", "channel_layout", "buf", "extended_buf", "nb_extended_buf", "side_data",
                "nb_side_data", "flags", "best_effort_timestamp", "pkt_pos", "pkt_duration", "metadata",
                "decode_error_flags", "channels", "pkt_size", "colorspace", "color_range", "qp_table_buf");
    }
}
