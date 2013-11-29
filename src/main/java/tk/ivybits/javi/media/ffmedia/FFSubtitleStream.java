package tk.ivybits.javi.media.ffmedia;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.SubtitleStream;

import javax.sound.sampled.AudioFormat;

public class FFSubtitleStream extends FFStream implements SubtitleStream {
    FFSubtitleStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
    }
}
