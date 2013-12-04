package tk.ivybits.javi.media;

import tk.ivybits.javi.media.stream.MediaStream;

import java.util.concurrent.locks.LockSupport;

public class AVSync {
    // If stream goes out of sync by anything larger than this (nano time), seek to catch up instead of sleep
    private static final int SEEK_THRESHOLD = 2000000000;
    private long lastPts;
    private long lost, frames;
    private MediaStream stream;

    public AVSync(MediaStream stream) {
        this.stream = stream;
        reset();
    }

    public void sync(long duration, Runnable callback) {
        ++frames;
        // Add in duration, which is the time that is spent waiting for the frame to render, so we get
        // the time when this frame is rendered, and set it as the last frame.
        // If duration is NEGATIVE, nothing should be rendered. We basically are subtracting the overdue
        // time from time we started handling this frame, so we get the time on which the current frame
        // should be rendered. If multiple frames are skipped, this still works, as the lastFrame will
        // advance by the length of each lost frame until it goes back to sync,
        // i.e. duration is back to positive.
        long time = System.nanoTime();
        duration -= time - lastPts;
        if (duration < 0) {
            if (duration < -SEEK_THRESHOLD) {
                stream.seek(stream.position());
                reset();
            } else {
                // Video is behind audio; skip frame
                lastPts = time + duration;
            }
            ++lost;
            return;
        }
        LockSupport.parkNanos(duration);
        try {
            callback.run();
        } catch (Exception e) {
            throw new IllegalStateException("exception raised in callback", e);
        }
        lastPts = time + duration;
    }

    public void reset() {
        lastPts = System.nanoTime();
    }

    public double frameLossRate() {
        return lost / (double) frames;
    }
}
