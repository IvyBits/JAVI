package tk.ivybits.javi.swing;

import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.MediaHandler;
import tk.ivybits.javi.media.stream.AudioStream;
import tk.ivybits.javi.media.stream.MediaStream;
import tk.ivybits.javi.media.stream.VideoStream;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.LockSupport;

/**
 * Media component for Swing.
 * <p/>
 * Uses JavaSound's {@link javax.sound.sampled.SourceDataLine} to output audio, and paints outside of the EDT to
 * minimize overhead. Handles audio-video sync.
 *
 * @version 1.0
 * @since 1.0
 */
public class SwingMediaPanel extends JPanel {
    private final Media media;
    private MediaStream stream;
    private Thread streamingThread;
    private BufferedImage nextFrame;
    private int frames = 0, lost = 0;
    private ArrayList<StreamListener> listeners = new ArrayList<>();
    private SourceDataLine sdl;
    private Mixer mixer;

    /**
     * Fetches the mixer in use.
     *
     * @return The mixer currently being used, or null if there is none (or the desired one failed to open).
     */
    public Mixer getMixer() {
        return mixer;
    }

    /**
     * Sets the mixer used for audio playback.
     *
     * @param mixer The mixer to use, or null to disable (mute) audio.
     * @return True if the mixer was successfully set, or false if it was not.
     *         <b>A mixer may not be set if a <code>LineUnavailableException</code> is thrown when opening a line.</b>
     *         This is generally caused by the desired mixer not supporting the format the audio stream is encoded in.
     */
    public boolean setMixer(Mixer mixer) {
        // Close audio line, if it exists
        if (sdl != null) {
            sdl.drain();
            sdl.close();
        }

        if (mixer == null) {
            // If mixer is null, then audio is disabled
            this.mixer = mixer;
            return true;
        }
        try {
            AudioFormat af = stream.getAudioStream().audioFormat();
            sdl = AudioSystem.getSourceDataLine(af, mixer.getMixerInfo());
            // Attempt to use a large buffer, such that sdl.write has a lower
            // chance of blocking
            sdl.open(af, 512000);
        } catch (LineUnavailableException failed) {
            return false;
        }
        this.mixer = mixer;
        sdl.start();
        return true;
    }

    /**
     * Creates a new SwingMediaPanel component.
     *
     * @param media The source to be played. Does not have to contain a video media. In the case that a video media does
     *              not exist, this component will act like a normal <code>JPanel</code> while playing available
     *              streams.
     * @since 1.0
     */
    public SwingMediaPanel(final Media media) throws IOException {
        this.media = media;

        stream = media
                .stream()
                .audio(new MediaHandler<byte[]>() {
                    @Override
                    public void handle(byte[] buffer) {
                        if (sdl == null) {// Audio failed to initialize; ignore this buffer
                            return;
                        }
                        int written = 0;
                        // sdl.write is not guaranteed to write our entire buffer.
                        // Therefore, we keep writing until out buffer has been fully
                        // written, to prevent audio skips
                        while (written < buffer.length) {
                            written += sdl.write(buffer, written, buffer.length);
                        }
                    }
                })
                .video(new MediaHandler<BufferedImage>() {
                    @Override
                    public void handle(BufferedImage buffer, long duration) {
                        ++frames;
                        if (duration < 0) {
                            // Video is behind audio; skip frame
                            ++lost;
                            return;
                        }

                        LockSupport.parkNanos(duration);
                        // Set our current frame to the passed buffer,
                        // and repaint immediately. Because we do not use repaint(), we
                        // have a guarantee that each frame will be drawn separately. repaint() tends
                        // to squash multiple paints into one, giving a jerkish appearance to the video.
                        nextFrame = buffer;
                        paintImmediately(getBounds());
                    }

                    @Override
                    public void end() {
                        // We've finished the video: set the frame to null such that on the next repaint,
                        // we won't draw the final frame of the video.
                        nextFrame = null;
                        paintImmediately(getBounds());

                        // Notify all listeners that our stream has ended
                        for (StreamListener listener : listeners) {
                            listener.onEnd();
                        }
                    }
                })
                .create();

        streamingThread = new Thread(stream);
    }

    /**
     * Starts media streaming.
     *
     * @since 1.0
     */
    public void start() {
        streamingThread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
        Dimension boundary = getSize();
        if (nextFrame != null) {
            int width = nextFrame.getWidth();
            int height = nextFrame.getHeight();
            /* Graphics2D g2d = nextFrame.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.drawString("HEIL UNS SELBSTS!", 50, 50); */

            // Scale image dimensions with aspect ratio to fit inside the panel
            int bwidth;
            int bheight = ((bwidth = boundary.width) * height) / width;
            if (bheight > boundary.height) {
                bwidth = ((bheight = boundary.height) * width) / height;
            }

            // Don't filter if the difference in size is insignificant (under 20px)
            if (Math.max(bwidth - width, bheight - height) > 20) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            // Center it in the space given
            int x = Math.abs(boundary.width - bwidth) / 2;
            int y = Math.abs(boundary.height - bheight) / 2;
            g.drawImage(nextFrame, x, y, bwidth, bheight, null);
            // Now draw the black sizes on the side or the top
            // By not filling the entire client area with a colour and then overwriting with the current frame,
            // we save potentially significant amounts of time.
            g.setColor(getBackground());
            if (bheight == boundary.height) {
                g.fillRect(0, 0, x, boundary.height);
                g.fillRect(x + bwidth, 0, x + 1, boundary.height);
            } else {
                g.fillRect(0, 0, boundary.width, y);
                g.fillRect(0, y + bheight, boundary.width, y + 1);
            }
        } else {
            // Foregoe call to super.paint: emulate it with less overhead
            g.setColor(getBackground());
            g.fillRect(0, 0, boundary.width, boundary.height);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNotify() {
        super.removeNotify();
        try {
            streamingThread.join(100);
        } catch (InterruptedException death) {

        }
        // Close data lines
        setMixer(null);
        stream.close();
    }

    /**
     * Returns the amount of video frames not rendered due to audio-video sync, as a percentage.
     *
     * @return The aforementioned percentage, from 0..1
     * @since 1.0
     */
    public double frameLossRate() {
        return lost / (double) frames;
    }

    /**
     * Checks if the stream is running.
     *
     * @return True if so, false otherwise.
     * @throws IllegalStateException Thrown if the stream is not started.
     * @since 1.0
     */
    public boolean isPlaying() {
        return stream.isPlaying();
    }

    /**
     * Sets the current state of the stream.
     *
     * @param flag If true, the stream will be played. Otherwise, it will be paused.
     * @throws IllegalStateException Thrown if the stream is not started.
     * @since 1.0
     */
    public void setPlaying(boolean flag) {
        stream.setPlaying(flag);
    }

    /**
     * Seeks to a position in the stream.
     *
     * @param to The position to seek to, in milliseconds.
     * @throws IllegalStateException Thrown if the stream was never started.
     * @throws tk.ivybits.javi.exc.StreamException
     *                               Thrown if seek failed.
     * @throws IllegalStateException Thrown if called when called on a stream that is not started.
     * @since 1.0
     */
    public void seek(long to) {
        stream.seek(to);
    }

    /**
     * Sets the AudioStream to be played.
     *
     * @param audioStream The AudioStream to begin playing.
     * @return The AudioStream previously being played, null if none.
     * @since 1.0
     */
    public AudioStream setAudioStream(AudioStream audioStream) {
        // Set the audio stream, but keep the previous stream to return later
        // This call cannot be last since setMixer closes our SourceDataLine
        AudioStream previous = stream.setAudioStream(audioStream);

        // Desired attributes
        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioStream.audioFormat());
        // If all else fails after this, at the very least we close the current line, if any
        setMixer(null);
        // Iterate over all mixers and select one that supports out audio stream's format
        // Such a mixer may not necessarily exist, which is why it's important to close the
        // line before we do this
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            Mixer mix = AudioSystem.getMixer(info);
            if (mix.isLineSupported(lineInfo)) {
                setMixer(mix); // Start using this mixer
                break;
            }
        }
        return previous;
    }

    /**
     * Sets the VideoStream to be played.
     *
     * @param videoStream The VideoStream to begin playing.
     * @return The VideoStream previously being played, null if none.
     * @since 1.0
     */
    public VideoStream setVideoStream(VideoStream videoStream) {
        return stream.setVideoStream(videoStream);
    }

    /**
     * Registers <code>listener</code> so that it will receive events when
     * the playing state of the panel changes.
     *
     * @param listener The <code>StreamListener</code> to register.
     */
    public void addStreamListener(StreamListener listener) {
        listeners.add(listener);
    }

    /**
     * Unregisters <code>listener</code> so that it will no longer recieve
     * playback events.
     *
     * @param listener The <code>StreamListener</code> to unregister.
     */
    public void removeStreamListener(StreamListener listener) {
        listeners.remove(listener);
    }
}
