/*
 * This file is part of JAVI.
 *
 * JAVI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * JAVI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with JAVI.  If not, see
 * <http://www.gnu.org/licenses/>.
 */

package tk.ivybits.javi.ffmpeg.avformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avutil.AVDictionary;
import tk.ivybits.javi.ffmpeg.avutil.AVRational;

import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around public parts of AVFormatContext.
 * Do NOT instantiate from Java.
 */
public class AVFormatContext extends Structure {
    public static class ByReference extends AVFormatContext implements Structure.ByReference {
    }

    public Pointer /* AVClass.ByReference */ av_class;
    public Pointer /* AVInputFormat.ByReference */ iformat;
    public Pointer /* AVOutputFormat.ByReference */ oformat;
    public Pointer priv_data;
    public Pointer /* AVIOContext.ByReference */ pb;
    public int ctx_flags;
    public int nb_streams;
    public Pointer streams; // AVStream ** array of pointers
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

    public Pointer interrupt_callback;
    public Pointer interrupt_opaque;

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
    public int probe_score;
    public Pointer /* AVPacketList.ByReference */ packet_buffer;
    public Pointer /* AVPacketList.ByReference */ packet_buffer_end;
    public long data_offset;
    public Pointer /* AVPacketList.ByReference */ raw_packet_buffer;
    public Pointer /* AVPacketList.ByReference */ raw_packet_buffer_end;
    public Pointer /* AVPacketList.ByReference */ parse_queue;
    public Pointer /* AVPacketList.ByReference */ parse_queue_end;
    public int raw_packet_buffer_remaining_size;
    public long offset;
    public AVRational offset_timebase;
    public int io_repositioned;

    // And some private fields

    public AVFormatContext() {
        super();
    }

    public AVFormatContext(Pointer address) {
        super(address);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("av_class", "iformat", "oformat", "priv_data", "pb", "ctx_flags", "nb_streams",
                "streams", "filename", "start_time", "duration", "bit_rate", "packet_size",
                "max_delay", "flags", "probesize", "max_analyze_duration", "key", "keylen",
                "nb_programs", "programs", "video_codec_id", "audio_codec_id",
                "subtitle_codec_id", "max_index_size", "max_picture_buffer", "nb_chapters",
                "chapters", "metadata", "start_time_realtime", "fps_probe_size", "error_recognition",
                "interrupt_callback", "interrupt_opaque", "debug", "ts_id", "audio_preload", "max_chunk_duration",
                "max_chunk_size", "use_wallclock_as_timestamps", "avoid_negative_ts", "avio_flags",
                "duration_estimation_method", "skip_initial_bytes", "correct_ts_overflow", "seek2any",
                "flush_packets", "probe_score", "packet_buffer", "packet_buffer_end", "data_offset", "raw_packet_buffer",
                "raw_packet_buffer_end", "parse_queue", "parse_queue_end",
                "raw_packet_buffer_remaining_size", "offset", "offset_timebase", "io_repositioned");
    }
}
