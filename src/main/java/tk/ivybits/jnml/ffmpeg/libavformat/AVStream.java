package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVCodecContext;
import tk.ivybits.jnml.ffmpeg.libavcodec.AVPacket;
import tk.ivybits.jnml.ffmpeg.libavutil.AVDictionary;
import tk.ivybits.jnml.ffmpeg.libavutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVStream extends Structure {
    public static class ByReference extends AVStream implements Structure.ByReference {
    }

    public static class ByValue extends AVStream implements Structure.ByValue {
    }

    public int index;
    public int id;
    public AVCodecContext.ByReference codec;
    public Pointer priv_data;
    public AVFrac.ByValue pts;
    public AVRational.ByValue time_base;
    public long start_time;
    public long duration;
    public long nb_frames;
    public int disposition;
    public int discard;
    public AVRational.ByValue sample_aspect_ratio;
    public AVDictionary metadata;
    public AVRational.ByValue avg_frame_rate;
    public AVPacket.ByValue attached_pic;
    public /*info_struct.ByReference*/ Pointer info;
    @Deprecated
    public long do_not_use;
    public long first_dts;
    public long cur_dts;
    public long last_IP_pts;
    public int last_IP_duration;
    public int probe_packets;
    public int codec_info_nb_frames;
    public int /* AVStreamParseType */ need_parsing;
    public Pointer /* AVCodecParserContext */ parser;
    public AVPacketList.ByReference last_in_packet_buffer;
    public AVProbeData.ByValue probe_data;
    public long[] pts_buffer = new long[MAX_REORDER_DELAY+1];
    public Pointer /* AVIndexEntry */ index_entries;
    public int nb_index_entries;
    public int index_entries_allocated_size;
    public AVRational.ByValue r_frame_rate;
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

    public static final int MAX_STD_TIMEBASES = 60 * 12 + 6;
    public static final int MAX_PROBE_PACKETS = 2500;
    public static final int MAX_REORDER_DELAY = 16;

    public static class info_struct extends Structure {
        public static class ByReference extends info_struct implements Structure.ByReference {
        }

        public static class ByValue extends info_struct implements Structure.ByValue {
        }

        public long last_dts;
        public long duration_gcd;
        public int duration_count;
        public Pointer duration_error;
        public long codec_info_duration;
        public long codec_info_duration_fields;
        public int found_decoder;
        public long last_duration;
        public long fps_first_dts;
        public int fps_first_dts_idx;
        public long fps_last_dts;
        public int fps_last_dts_idx;

        public info_struct(Pointer address) {
            super(address);
            read();
        }

        public info_struct() {
            super();
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("last_dts", "duration_gcd", "duration_count", "duration_error",
                    "codec_info_duration", "codec_info_duration_fields", "found_decoder", "last_duration",
                    "fps_first_dts", "fps_first_dts_idx", "fps_last_dts", "fps_last_dts_idx");
        }
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("index",
                "id",
                "codec",
                "priv_data",
                "pts",
                "time_base",
                "start_time",
                "duration",
                "nb_frames",
                "disposition",
                "discard",
                "sample_aspect_ratio",
                "metadata",
                "avg_frame_rate",
                "attached_pic",
                "info",
                "do_not_use",
                "first_dts",
                "cur_dts",
                "last_IP_pts",
                "last_IP_duration",
                "probe_packets",
                "codec_info_nb_frames",
                "need_parsing",
                "parser",
                "last_in_packet_buffer",
                "probe_data",
                "pts_buffer",
                "index_entries",
                "nb_index_entries",
                "index_entries_allocated_size",
                "r_frame_rate",
                "stream_identifier",
                "interleaver_chunk_size",
                "interleaver_chunk_duration",
                "request_probe",
                "skip_to_keyframe",
                "skip_samples",
                "nb_decoded_frames",
                "mux_ts_offset",
                "pts_wrap_reference",
                "pts_wrap_behavior");
    }

    public AVStream() {
        super();
    }

    public AVStream(Pointer address) {
        super(address);
        read();
    }
}
