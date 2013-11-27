package tk.ivybits.javi.media.ffmedia;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;
import tk.ivybits.javi.media.stream.AudioStream;

import javax.sound.sampled.AudioFormat;

/**
 * FFmpeg-backed VideoStream.
 */
public class FFAudioStream extends FFStream implements AudioStream {
    FFAudioStream(FFMedia container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * {@inheritDoc}
     */
    public AudioFormat audioFormat() {
        return new AudioFormat(ffstream.codec.sample_rate,
                16,
                ffstream.codec.channels,
                true, false);
    }
}
