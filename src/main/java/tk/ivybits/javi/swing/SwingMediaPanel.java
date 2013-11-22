package tk.ivybits.javi.swing;

import tk.ivybits.javi.stream.Media;
import tk.ivybits.javi.stream.MediaHandler;
import tk.ivybits.javi.stream.MediaStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SwingMediaPanel extends JPanel {
    private final Media media;
    private MediaStream stream;
    private BufferedImage nextFrame;
    private int frames = 0, lost = 0;

    public SwingMediaPanel(Media media) {
        this.media = media;
    }

    public void start() throws IOException {
        stream = media
                .stream()
                .audio(new MediaHandler<byte[]>() {
                    private SourceDataLine sdl;

                    {
                        try {
                            AudioFormat af = media.audioFormat();
                            sdl = AudioSystem.getSourceDataLine(af);
                            sdl.open(af, 512000);
                            System.out.println(sdl.getBufferSize());
                            sdl.start();
                        } catch (LineUnavailableException e) {
                            throw new IllegalStateException("failed to initialize audio line");
                        }
                    }

                    @Override
                    public void handle(byte[] buffer) {
                        if (sdl == null)
                            return;
                        int written = 0;
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
                            ++lost;
                            return;
                        }
                        try {
                            Thread.sleep(duration);
                        } catch (InterruptedException e) {
                        }
                        nextFrame = buffer;
                        paintImmediately(getBounds());
                    }

                    @Override
                    public void end() {
                        nextFrame = null;
                        paintImmediately(getBounds());
                    }
                })
                .create();
        stream.start();
    }

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

    public double frameLossRate() {
        return lost / (double) frames;
    }
}
