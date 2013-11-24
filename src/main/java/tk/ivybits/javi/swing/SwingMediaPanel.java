package tk.ivybits.javi.swing;

import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.media.MediaHandler;
import tk.ivybits.javi.media.MediaStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Media component for Swing.
 * <p/>
 * Uses JavaSound's {@link javax.sound.sampled.SourceDataLine} to output audio, and paints outside of the EDT to minimize overhead.
 * Handles audio-video sync.
 */
public class SwingMediaPanel extends JPanel {
    private final Media media;
    private MediaStream stream;
    private BufferedImage nextFrame;
    private int frames = 0, lost = 0;

    /**
     * Creates a new SwingMediaPanel component.
     *
     * @param media The source to be played. Does not have to contain a video media.
     *              In the case that a video media does not exist, this component
     *              will act like a normal <code>JPanel</code> while playing available streams.
     */
    public SwingMediaPanel(Media media) {
        this.media = media;
    }

    /**
     * Starts media streaming.
     *
     * @throws IOException Thrown if a media stream could not be established.
     */
    public void start() throws IOException {
        if (stream != null)
            stream.start();
        else
            stream = media
                    .stream()
                    .audio(new MediaHandler<byte[]>() {
                        private SourceDataLine sdl;

                        {
                            try {
                                AudioFormat af = media.audioFormat();
                                sdl = AudioSystem.getSourceDataLine(af);
                                // Use a large buffer, such that sdl.write has a lower
                                // chance of blocking
                                sdl.open(af, 512000);
                                sdl.start();
                            } catch (LineUnavailableException e) {
                                throw new IllegalStateException("failed to initialize audio line");
                            }
                        }

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
                            try {
                                Thread.sleep(duration);
                            } catch (InterruptedException e) {
                            }
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
                        }
                    })
                    .create();
        stream.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (nextFrame != null) {
            int width = nextFrame.getWidth();
            int height = nextFrame.getHeight();

            Dimension boundary = getSize();

            // Scale image dimensions with aspect ratio to fit inside the panel
            int bwidth;
            int bheight = ((bwidth = boundary.width) * height) / width;
            if (bheight > boundary.height) {
                bwidth = ((bheight = boundary.height) * width) / height;
            }

            // Don't filter if the difference in size is insignificant
            if (Math.max(bwidth - width, bheight - height) > 20) {
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            // Center it in the space given
            g.drawImage(nextFrame, Math.abs(boundary.width - bwidth) / 2, Math.abs(boundary.height - bheight) / 2, bwidth, bheight, null);
        }
    }

    /**
     * Returns the amount of video frames not rendered due to audio-video sync, as a percentage.
     *
     * @return The aforementioned percentage, from 0..1
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
        if (stream == null)
            throw new IllegalStateException("stream not started");
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
        if (stream == null)
            throw new IllegalStateException("stream not started");
        stream.setPlaying(flag);
    }

    /**
     * Checks if the stream has been completed.
     *
     * @return True if so, false otherwise.
     * @throws IllegalStateException Thrown if the stream was never started.
     * @since 1.0
     */
    public boolean isFinished() {
        if (stream == null)
            throw new IllegalStateException("stream not started");
        return nextFrame == null;
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
        if (stream == null)
            throw new IllegalStateException("stream not started");
        stream.seek(to);
    }
}
