package tk.ivybits.jnml.ffmpeg.libavformat;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;
import tk.ivybits.jnml.ffmpeg.libavutil.AVClass;

import java.util.Arrays;
import java.util.List;

/**
 * Wrapper around the public parts of AVOutputFormat. Please do not instantiate from Java.
 */
public class AVOutputFormat extends Structure {
    public static class ByReference extends AVOutputFormat implements Structure.ByReference {}
    public static class ByValue extends AVOutputFormat implements Structure.ByValue {}

    public Pointer name;
    public Pointer long_name;
    public Pointer mime_type;
    public Pointer extensions;
    public int audio_codec;
    public int video_codec;
    public int subtitle_codec;
    public int flags;
    public PointerByReference codec_tag;
    public AVClass priv_class;

    // And here comes the private parts

    protected List<String> getFieldOrder() {
        return Arrays.asList("name", "long_name", "mime_type", "extensions", "audio_codec", "video_codec",
                "subtitle_codec", "flags", "codec_tag", "priv_class");
    }
}
