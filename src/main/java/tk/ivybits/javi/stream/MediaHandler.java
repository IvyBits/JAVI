package tk.ivybits.javi.stream;

/**
 * Tudor
 * 2013-11-21
 */
public abstract class MediaHandler<T> {
    public abstract void handle(T buffer);

    public void end() {

    }
}