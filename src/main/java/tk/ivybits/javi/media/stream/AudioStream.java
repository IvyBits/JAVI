package tk.ivybits.javi.media.stream;

import javax.sound.sampled.AudioFormat;

public interface AudioStream extends Stream {
    AudioFormat audioFormat();
}