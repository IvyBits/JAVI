package tk.ivybits.javi.ffmpeg;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.javi.ffmpeg.avformat.AVInputFormat;

public interface LibAVFormat extends Library {
    int avformat_version();

    void av_register_all();

    int avformat_network_init();

    int avformat_network_deinit();

    int avformat_open_input(PointerByReference ps, String filename, AVInputFormat.ByReference fmt, PointerByReference options);

    int av_find_stream_info(Pointer ps);

    void av_dump_format(Pointer ps, int index, String url, int is_output);

    int av_read_frame(Pointer s, Pointer pkt);
}
