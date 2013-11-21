package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.avutil.AVLogConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static tk.ivybits.javi.FFmpeg.*;

public class JNATest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.printf("Running avcodec %s, avformat %s, avutil %s.\n",
                avcodec.avcodec_version() >> 16,
                avformat.avformat_version() >> 16,
                avutil.avutil_version() >> 16);
        avutil.av_log_set_level(AVLogConstants.AV_LOG_QUIET);

        avformat.avformat_network_init();
        final Video video = new Video(new URI("http://dl.dropboxusercontent.com/u/36712017/music/ocanada.mp4"));
        video.stream();

        final JFrame frame = new JFrame("Rendering Test");
        frame.setLayout(new BorderLayout());
        frame.add(BorderLayout.CENTER, new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                BufferedImage img = video.fetchFrameData();
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int width = img.getWidth();
                int height = img.getHeight();

                Dimension boundary = getSize();

                // Scale image dimensions with aspect ratio to fit inside the panel
                int bwidth;
                int bheight = ((bwidth = boundary.width) * height) / width;
                if (bheight > boundary.height)
                    bwidth = ((bheight = boundary.height) * width) / height;

                // Center it in the space given
                g.drawImage(img, Math.abs(boundary.width - bwidth) / 2, Math.abs(boundary.height - bheight) / 2, bwidth, bheight, null);
                video.freeFrameData(img);
            }
        });

        Timer repaintTimer = new Timer(1000 / 30, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.repaint();
            }
        });
        frame.setSize(video.dimensions());
        repaintTimer.start();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}