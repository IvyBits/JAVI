package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodecContext;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPacket;
import tk.ivybits.jnml.ffmpeg.libavutil.AVDictionary;
import tk.ivybits.jnml.ffmpeg.libavutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVStream extends Structure {
    public static final int MAX_STD_TIMEBASES = 60 * 12 + 6;
    public static final int MAX_PROBE_PACKETS = 2500;
    public static final int MAX_REORDER_DELAY = 16;
    public static class ByReference extends AVStream implements Structure.ByReference {}
    public static class ByValue extends AVStream implements Structure.ByValue {}

    public int index;
    public int id;
    public AVCodecContext.ByReference codec;
    public Pointer priv_data;
    public /*AVFrac*/ Pointer pts;
    public AVRational time_base;
    public long start_time;
    public long duration;
    public long nb_frames;
    public int disposition;
    public /*AVDiscard*/ int discard;
    public AVRational sample_aspect_ratio;
    public AVDictionary metadata;
    public AVRational avg_frame_rate;
    public AVPacket.ByValue attached_pic;

    /*  int64_t last_dts;
        int64_t duration_gcd;
        int duration_count;
        double (*duration_error)[2][MAX_STD_TIMEBASES];
        int64_t codec_info_duration;
        int64_t codec_info_duration_fields;
        int found_decoder;

        int64_t last_duration;

        int64_t fps_first_dts;
        int     fps_first_dts_idx;
        int64_t fps_last_dts;
        int     fps_last_dts_idx;
    */
    public Pointer info;
    public int pts_wrap_bits;
    @Deprecated
    public long do_not_use;
    public long first_dts;
    public long cur_dts;
    public long last_IP_pts;
    public int last_IP_duration;
    public int probe_packets;
    public int codec_info_nb_frames;

    public int /*enum AVStreamParseType*/ need_parsing;
    public Pointer /*AVCodecParserContext*/ parser;
    public AVPacketList.ByReference last_in_packet_buffer;
    public long[] pts_buffer = new long[MAX_REORDER_DELAY + 1];
    public Pointer /*AVIndexEntry*/ index_entries;
    public int nb_index_entries;
    public int index_entries_allocated_size;
    public AVRational r_frame_rate;
    public int stream_identifier;
    public long interleaver_chunk_size;
    public long interleaver_chunk_duration;
    public int request_probe;
    public int skip_to_keyframe;
    public int skip_samples;
    public int nb_decoded_frames;
    public long mux_ts_offset;
    public long pts_wrap_reference;
    public int pts_wrap_behavior;

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList();
    }
}
