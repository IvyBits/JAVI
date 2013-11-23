package tk.ivybits.javi.stream;

public abstract class MediaHandler<T> {
    public void handle(T buffer) {}
    public void handle(T buffer, long time) {}

    public void end() {

    }
}