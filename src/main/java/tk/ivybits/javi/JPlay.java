package tk.ivybits.javi;

import tk.ivybits.javi.ffmpeg.LibAVCodec;
import tk.ivybits.javi.ffmpeg.LibAVFormat;
import tk.ivybits.javi.ffmpeg.LibAVUtil;
import tk.ivybits.javi.stream.Media;
import tk.ivybits.javi.swing.SwingMediaPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

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
        File videoFile = new File(args[0]);
        Media media = new Media(videoFile);

        JFrame frame = new JFrame(videoFile.getName());
        frame.setLayout(new BorderLayout());

        final SwingMediaPanel videoPanel = new SwingMediaPanel(media);
        videoPanel.setBackground(Color.BLACK);
        frame.add(BorderLayout.CENTER, videoPanel);

        frame.setSize(media.width(), media.height());
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