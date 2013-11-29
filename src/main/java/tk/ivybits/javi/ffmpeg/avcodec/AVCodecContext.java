package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.ShortByReference;
import tk.ivybits.javi.ffmpeg.avutil.AVFrame;
import tk.ivybits.javi.ffmpeg.avutil.AVRational;

import java.util.Arrays;
import java.util.List;

public class AVCodecContext extends Structure {
    public static final int AV_NUM_DATA_POINTERS = 8;

    public static class ByReference extends AVCodecContext implements Structure.ByReference {
        public ByReference(Pointer pointer) {
            super(pointer);
        }
    }

    public static class ByValue extends AVCodecContext implements Structure.ByValue {
    }

    public Pointer /* AVClass.ByReference */ av_class;
    public int log_level_offset;
    public int codec_type;
    public AVCodec.ByReference codec;
    public byte[] codec_name = new byte[32];
    public int codec_id;
    public int codec_tag;
    public int stream_codec_tag;
    public Pointer priv_data;
    public Pointer internal;
    public Pointer opaque;
    public int bit_rate;
    public int bit_rate_tolerance;
    public int global_quality;
    public int compression_level;
    public int flags;
    public int flags2;
    public Pointer extradata;
    public int extradata_size;
    public AVRational time_base;
    public int ticks_per_frame;
    public int delay;
    public int width;
    public int height;
    public int coded_width;
    public int coded_height;
    public int gop_size;
    public int pix_fmt;
    public int me_method;
    public draw_horiz_band_callback draw_horiz_band;
    public get_format_callback get_format;
    public int max_b_frames;
    public float b_quant_factor;
    public int rc_strategy;
    public int b_frame_strategy;
    public float b_quant_offset;
    public int has_b_frames;
    public int mpeg_quant;
    public float i_quant_factor;
    public float i_quant_offset;
    public float lumi_masking;
    public float temporal_cplx_masking;
    public float spatial_cplx_masking;
    public float p_masking;
    public float dark_masking;
    public int slice_count;
    public int prediction_method;
    public IntByReference slice_offset;
    public AVRational sample_aspect_ratio;
    public int me_cmp;
    public int me_sub_cmp;
    public int mb_cmp;
    public int ildct_cmp;
    public int dia_size;
    public int last_predictor_count;
    public int pre_me;
    public int me_pre_cmp;
    public int pre_dia_size;
    public int me_subpel_quality;
    public int dtg_active_format;
    public int me_range;
    public int intra_quant_bias;
    public int inter_quant_bias;
    public int slice_flags;
    public int xvmc_acceleration;
    public int mb_decision;
    public ShortByReference intra_matrix;
    public ShortByReference inter_matrix;
    public int scenechange_threshold;
    public int noise_reduction;
    public int me_threshold;
    public int mb_threshold;
    public int intra_dc_precision;
    public int skip_top;
    public int skip_bottom;
    public float border_masking;
    public int mb_lmin;
    public int mb_lmax;
    public int me_penalty_compensation;
    public int bidir_refine;
    public int brd_scale;
    public int keyint_min;
    public int refs;
    public int chromaoffset;
    public int scenechange_factor;
    public int mv0_threshold;
    public int b_sensitivity;
    public int color_primaries;
    public int color_trc;
    public int colorspace;
    public int color_range;
    public int chroma_sample_location;
    public int slices;
    public int field_order;
    public int sample_rate;
    public int channels;
    public int sample_fmt;
    public int frame_size;
    public int frame_number;
    public int block_align;
    public int cutoff;
    @Deprecated
    public int request_channels;
    public long channel_layout;
    public long request_channel_layout;
    public int audio_service_type;
    public int request_sample_fmt;
    @Deprecated
    public Pointer get_buffer;
    @Deprecated
    public Pointer release_buffer;
    @Deprecated
    public Pointer reget_buffer;
    public get_buffer2_callback get_buffer2;
    public int refcounted_frames;
    public float qcompress;
    public float qblur;
    public int qmin;
    public int qmax;
    public int max_qdiff;
    public float rc_qsquish;
    public float rc_qmod_amp;
    public int rc_qmod_freq;
    public int rc_buffer_size;
    public int rc_override_count;
    public /*RcOverride*/ Pointer rc_override;
    public Pointer rc_eq;
    public int rc_max_rate;
    public int rc_min_rate;
    public float rc_buffer_aggressivity;
    public float rc_initial_cplx;
    public float rc_max_available_vbv_use;
    public float rc_min_vbv_overflow_use;
    public int rc_initial_buffer_occupancy;
    public int coder_type;
    public int context_model;
    public int lmin;
    public int lmax;
    public int frame_skip_threshold;
    public int frame_skip_factor;
    public int frame_skip_exp;
    public int frame_skip_cmp;
    public int trellis;
    public int min_prediction_order;
    public int max_prediction_order;
    public long timecode_frame_start;
    public rtp_callback_callback rtp_callback;
    public int rtp_payload_size;
    public int mv_bits;
    public int header_bits;
    public int i_tex_bits;
    public int p_tex_bits;
    public int i_count;
    public int p_count;
    public int skip_count;
    public int misc_bits;
    public int frame_bits;
    public Pointer stats_out;
    public Pointer stats_in;
    public int workaround_bugs;
    public int strict_std_compliance;
    public int error_concealment;
    public int debug;
    public int debug_mv;
    public int err_recognition;
    public long reordered_opaque;
    public /*AVHWAccel* */ Pointer hwaccel;
    public Pointer hwaccel_context;
    public long[] error = new long[AV_NUM_DATA_POINTERS];
    public int dct_algo;
    public int idct_algo;
    public int bits_per_coded_sample;
    public int bits_per_raw_sample;
    @Deprecated
    public int lowres;
    public /*AVFrame* */ AVFrame.ByReference coded_frame;
    public int thread_count;
    public int thread_type;
    public int active_thread_type;
    public int thread_safe_callbacks;
    public execute_callback execute;
    public execute2_callback execute2;
    @Deprecated
    public Pointer thread_opaque;
    public int nsse_weight;
    public int profile;
    public int level;
    public int /* AVDiscard */  skip_loop_filter;
    public int /* AVDiscard */  skip_idct;
    public int /* AVDiscard */  skip_frame;
    public Pointer subtitle_header;
    public int subtitle_header_size;
    @Deprecated
    public int error_rate;
    @Deprecated
    public AVPacket.ByReference pkt;
    public long vbv_delay;
    public AVRational pkt_timebase;
    public Pointer /*AVCodecDescriptor */ codec_descriptor;
    /*public int lowres;*/ // New feature in 56
    public long pts_correction_num_faulty_pts;
    public long pts_correction_num_faulty_dts;
    public long pts_correction_last_pts;
    public long pts_correction_last_dts;
    public Pointer sub_charenc;
    public int sub_charenc_mode;
    public int skip_alpha;
    public int seek_preroll;
    /*public int debug_mv;*/ // New feature in 56


    public interface draw_horiz_band_callback extends Callback {
        void apply(ByReference s, Pointer src, int[] offset, int y, int type, int height);
    }

    public interface get_format_callback extends Callback {
        void apply(ByReference s, Pointer fmt);
    }

    public interface get_buffer2_callback extends Callback {
        int apply(ByReference s, /*AVFrame*/Pointer frame, int flags);
    }

    public interface rtp_callback_callback extends Callback {
        void apply(ByReference s, Pointer data, int size, int mb_nb);
    }

    public interface execute_callback2 extends Callback {
        int apply(ByReference c2, Pointer arg);
    }

    public interface execute_callback extends Callback {
        int apply(ByReference c, execute_callback2 func, Pointer arg2, IntByReference ret, int count, int size);
    }

    public interface execute2_callback2 extends Callback {
        int apply(ByReference c2, Pointer arg, int jobnr, int threadnr);
    }

    public interface execute2_callback extends Callback {
        int apply(ByReference c, execute2_callback2 func, Pointer arg2, IntByReference ret, int count);
    }

    public AVCodecContext() {
        super();
    }

    public AVCodecContext(Pointer address) {
        super(address);
        read();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("av_class", "log_level_offset", "codec_type",
                "codec", "codec_name", "codec_id", "codec_tag", "stream_codec_tag",
                "priv_data", "internal", "opaque", "bit_rate", "bit_rate_tolerance",
                "global_quality", "compression_level", "flags", "flags2", "extradata",
                "extradata_size", "time_base", "ticks_per_frame", "delay", "width",
                "height", "coded_width", "coded_height", "gop_size", "pix_fmt",
                "me_method", "draw_horiz_band", "get_format", "max_b_frames",
                "b_quant_factor", "rc_strategy", "b_frame_strategy", "b_quant_offset",
                "has_b_frames", "mpeg_quant", "i_quant_factor", "i_quant_offset",
                "lumi_masking", "temporal_cplx_masking", "spatial_cplx_masking",
                "p_masking", "dark_masking", "slice_count", "prediction_method",
                "slice_offset", "sample_aspect_ratio", "me_cmp", "me_sub_cmp", "mb_cmp",
                "ildct_cmp", "dia_size", "last_predictor_count", "pre_me", "me_pre_cmp",
                "pre_dia_size", "me_subpel_quality", "dtg_active_format", "me_range",
                "intra_quant_bias", "inter_quant_bias", "slice_flags", "xvmc_acceleration",
                "mb_decision", "intra_matrix", "inter_matrix", "scenechange_threshold",
                "noise_reduction", "me_threshold", "mb_threshold", "intra_dc_precision",
                "skip_top", "skip_bottom", "border_masking", "mb_lmin", "mb_lmax",
                "me_penalty_compensation", "bidir_refine", "brd_scale", "keyint_min",
                "refs", "chromaoffset", "scenechange_factor", "mv0_threshold",
                "b_sensitivity", "color_primaries", "color_trc", "colorspace",
                "color_range", "chroma_sample_location", "slices", "field_order",
                "sample_rate", "channels", "sample_fmt", "frame_size", "frame_number",
                "block_align", "cutoff", "request_channels", "channel_layout", "request_channel_layout",
                "audio_service_type", "request_sample_fmt", "get_buffer", "release_buffer",
                "reget_buffer", "get_buffer2", "refcounted_frames", "qcompress", "qblur",
                "qmin", "qmax", "max_qdiff", "rc_qsquish", "rc_qmod_amp", "rc_qmod_freq",
                "rc_buffer_size", "rc_override_count", "rc_override", "rc_eq", "rc_max_rate",
                "rc_min_rate", "rc_buffer_aggressivity", "rc_initial_cplx", "rc_max_available_vbv_use",
                "rc_min_vbv_overflow_use", "rc_initial_buffer_occupancy", "coder_type",
                "context_model", "lmin", "lmax", "frame_skip_threshold", "frame_skip_factor",
                "frame_skip_exp", "frame_skip_cmp", "trellis", "min_prediction_order",
                "max_prediction_order", "timecode_frame_start", "rtp_callback", "rtp_payload_size",
                "mv_bits", "header_bits", "i_tex_bits", "p_tex_bits", "i_count", "p_count",
                "skip_count", "misc_bits", "frame_bits", "stats_out", "stats_in", "workaround_bugs",
                "strict_std_compliance", "error_concealment", "debug", "debug_mv", "err_recognition",
                "reordered_opaque", "hwaccel", "hwaccel_context", "error", "dct_algo", "idct_algo",
                "bits_per_coded_sample", "bits_per_raw_sample", "lowres", "coded_frame", "thread_count",
                "thread_type", "active_thread_type", "thread_safe_callbacks", "execute", "execute2",
                "thread_opaque", "nsse_weight", "profile", "level", "skip_loop_filter", "skip_idct",
                "skip_frame", "subtitle_header", "subtitle_header_size", "error_rate", "pkt",
                "vbv_delay", "pkt_timebase", "codec_descriptor", "pts_correction_num_faulty_pts",
                "pts_correction_num_faulty_dts", "pts_correction_last_pts", "pts_correction_last_dts",
                "sub_charenc", "sub_charenc_mode", "skip_alpha", "seek_preroll");
    }
}