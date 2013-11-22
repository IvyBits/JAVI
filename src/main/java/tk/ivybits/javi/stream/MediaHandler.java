package tk.ivybits.javi.stream;

/**
 * Tudor
 * 2013-11-21
 */
public abstract class MediaHandler<T> {
    public void handle(T buffer) {}
    public void handle(T buffer, long time) {}

    public void end() {

    }
}