package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.LibAVCodec;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.LibAVUtil;
import tk.ivybits.javi.media.Media;
import tk.ivybits.javi.swing.SwingMediaPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Minimalistic media player.
 * <p/>
 * To run from command line, pass in the sources to play as arguments to the program.
 * Each source will open in a separate window.
 * Streaming is supported.
 *
 * @version 1.0
 * @since 1.0
 */
public class JPlay {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("File not specified.");
            System.exit(1);
        }

        LibAVFormat.av_register_all();
        LibAVCodec.avcodec_register_all();
        LibAVFormat.avformat_network_init();

        System.out.printf("Running avcodec %s, avformat %s, avutil %s.\n",
                LibAVCodec.avcodec_version() >> 16,
                LibAVFormat.avformat_version() >> 16,
                LibAVUtil.avutil_version() >> 16);

        for (String source : args) {
            play(source);
        }
    }

    public static void play(String source) throws IOException {
        File videoFile = new File(source);
        Media media = new Media(videoFile);

        JFrame frame = new JFrame(videoFile.getName());
        frame.setLayout(new BorderLayout());

        final SwingMediaPanel videoPanel = new SwingMediaPanel(media);
        videoPanel.setBackground(Color.BLACK);
        frame.add(BorderLayout.CENTER, videoPanel);

        int width = media.width();
        int height = media.height();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (width > screen.width - 20 || height > screen.height - 60) {
            width = screen.width - 20;
            height = screen.height - 60;
        }

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.out.printf("Frame loss: %.2f%%\n", videoPanel.frameLossRate() * 100);
            }
        });
        videoPanel.start();
    }
}