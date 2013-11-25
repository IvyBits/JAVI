package tk.ivybits.javi.media;

import tk.ivybits.javi.ffmpeg.avformat.AVStream;

import javax.sound.sampled.AudioFormat;

public class AudioStream extends Stream {
    AudioStream(Media container, AVStream ffstream) {
        super(container, ffstream);
    }

    /**
     * Fetches the audio stream's format.
     * <b/>
     * <b>Important note: this will always be signed little-endian 16-bit PCM.
     * Only sample rate and number of channels may vary.</b>
     *
     * @return The stream's format.
     * @since 1.0
     */
    public AudioFormat audioFormat() {
        return new AudioFormat(ffstream.codec.sample_rate,
                16,
                ffstream.codec.channels,
                true, false);
    }
}
