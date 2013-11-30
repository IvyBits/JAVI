package tk.ivybits.javi.media;

public class AVSync {
    private long lastPts;

    public AVSync() {
        reset();
    }

    public long sync(long duration) {
        // Add in duration, which is the time that is spent waiting for the frame to render, so we get
        // the time when this frame is rendered, and set it as the last frame.
        // If duration is NEGATIVE, nothing should be rendered. We basically are subtracting the overdue
        // time from time we started handling this frame, so we get the time on which the current frame
        // should be rendered. If multiple frames are skipped, this still works, as the lastFrame will
        // advance by the length of each lost frame until it goes back to sync,
        // i.e. duration is back to positive.
        long time = System.nanoTime();
        duration -= time - lastPts;
        lastPts = time + duration;
        return duration;
    }

    public void reset() {
        lastPts = System.nanoTime();
    }
}
