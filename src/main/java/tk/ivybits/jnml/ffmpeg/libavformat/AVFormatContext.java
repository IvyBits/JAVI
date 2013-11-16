package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavutil.AVClass;
import tk.ivybits.jnml.ffmpeg.libavutil.AVRational;

import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around public parts of AVFormatContext.
 * Do NOT instiate from Java.
 */
public class AVFormatContext extends Structure {
    public static class ByReference extends AVFormatContext implements Structure.ByReference {}
    public static class ByValue extends AVFormatContext implements Structure.ByValue {}

    public AVClass.ByReference av_class;
    public AVInputFormat.ByReference iformat;
    public AVOutputFormat.ByReference oformat;
    public Pointer priv_data;
    public AVIOContext pb;
    public int ctx_flags;
    public int nb_streams;
    public PointerByReference streams;
    public byte[] filename = new byte[1024];
    public long start_time;
    public long duration;
    public int bit_rate;
    public int packet_size;
    public int max_delay;
    public int flags;
    public int probesize;
    public int max_analyze_duration;
    public Pointer key;
    public int keylen;
    public int nb_programs;
    public PointerByReference programs;
    public int video_codec_id;
    public int audio_codec_id;
    public int subtitle_codec_id;
    public int max_index_size;
    public int max_picture_buffer;
    public int nb_chapters;
    public PointerByReference chapters;
    public AVDictionary metadata;
    public long start_time_realtime;
    public int fps_probe_size;
    public int error_recognition;
    public AVIOInterruptCB.ByValue interrupt_callback;
    public int debug;
    public int ts_id;
    public int audio_preload;
    public int max_chunk_duration;
    public int max_chunk_size;
    public int use_wallclock_as_timestamps;
    public int avoid_negative_ts;
    public int avio_flags;
    public int duration_estimation_method;
    public int skip_initial_bytes;
    public int correct_ts_overflow;
    public int seek2any;
    public int flush_packets;
    public AVPacketList packet_buffer;
    public AVPacketList packet_buffer_end;
    public long data_offset;
    public AVPacketList raw_packet_buffer;
    public AVPacketList raw_packet_buffer_end;
    public AVPacketList parse_queue;
    public AVPacketList parse_queue_end;
    public int raw_packet_buffer_remaining_size;
    public long offset;
    public AVRational offset_timebase;
    public int io_repositioned;

    // And some private fields

    public static final int AVFMT_FLAG_GENPTS = 0x0001;
    public static final int AVFMT_FLAG_IGNIDX = 0x0002;
    public static final int AVFMT_FLAG_NONBLOCK = 0x0004;
    public static final int AVFMT_FLAG_IGNDTS = 0x0008;
    public static final int AVFMT_FLAG_NOFILLIN = 0x0010;
    public static final int AVFMT_FLAG_NOPARSE = 0x0020;
    public static final int AVFMT_FLAG_NOBUFFER = 0x0040;
    public static final int AVFMT_FLAG_CUSTOM_IO = 0x0080;
    public static final int AVFMT_FLAG_DISCARD_CORRUPT = 0x0100;
    public static final int AVFMT_FLAG_MP4A_LATM = 0x8000;
    public static final int AVFMT_FLAG_SORT_DTS = 0x10000;
    public static final int AVFMT_FLAG_PRIV_OPT = 0x20000;
    public static final int AVFMT_FLAG_KEEP_SIDE_DATA = 0x40000;
    public static final int FF_FDEBUG_TS = 0x0001;
    public static final int RAW_PACKET_BUFFER_SIZE = 2500000;

    protected List<String> getFieldOrder() {
        return Arrays.asList("av_class", "iformat", "oformat", "priv_data", "pb", "ctx_flags", "nb_streams",
                             "streams", "filename", "start_time", "duration", "bit_rate", "packet_size",
                             "max_delay", "flags", "probesize", "max_analyze_duration", "key", "keylen",
                             "nb_programs", "programs", "video_codec_id", "audio_codec_id",
                             "subtitle_codec_id", "max_index_size", "max_picture_buffer", "nb_chapters",
                             "chapters", "metadata", "start_time_realtime", "fps_probe_size", "error_recognition",
                             "interrupt_callback", "debug", "ts_id", "audio_preload", "max_chunk_duration",
                             "max_chunk_size", "use_wallclock_as_timestamps", "avoid_negative_ts", "avio_flags",
                             "duration_estimation_method", "skip_initial_bytes", "correct_ts_overflow", "seek2any",
                             "flush_packets", "packet_buffer", "packet_buffer_end", "data_offset", "raw_packet_buffer",
                             "raw_packet_buffer_end", "parse_queue", "parse_queue_end",
                             "raw_packet_buffer_remaining_size", "offset", "offset_timebase", "io_repositioned");
    }

    public AVFormatContext() {
        super();
    }

    public AVFormatContext(Pointer address) {
        super(address);
        read();
    }
}
