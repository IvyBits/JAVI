package tk.ivybits.javi;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SafeBlockingQueue<T> {
    private static final Object NULL_VALUE = new Object();

    private final BlockingQueue<T> backing;

    public SafeBlockingQueue() {
        this(new LinkedBlockingQueue<T>(), Integer.MAX_VALUE);
    }

    public SafeBlockingQueue(int cap) {
        this(new ArrayBlockingQueue<T>(cap), cap);
    }

    private SafeBlockingQueue(BlockingQueue<T> backing, int capacity) {
        this.backing = backing;
    }

    public void clear() {
        this.backing.clear();
    }

    public boolean isEmpty() {
        return this.backing.isEmpty();
    }

    public int size() {
        return this.backing.size();
    }

    public void put(T item) {
        item = wrap(item);

        try {
            this.backing.put(item);
        } catch (InterruptedException exc) {
        }
    }

    public T peek() {
        try {
            return unwrap(this.backing.peek());
        } catch (IllegalMonitorStateException exc) {
            return null;
        }
    }

    public T poll() {
        try {
            return unwrap(this.backing.poll());
        } catch (IllegalMonitorStateException exc) {
            return null;
        }
    }

    public T poll(long ms) {
        try {
            return unwrap(this.backing.poll(ms, TimeUnit.MILLISECONDS));
        } catch (InterruptedException exc) {
            return null;
        }
    }

    public T take() {
        try {
            return unwrap(this.backing.take());
        } catch (InterruptedException exc) {
            return null;
        }
    }

    T wrap(T item) {
        return (item == null) ? (T) NULL_VALUE : item;
    }

    T unwrap(T item) {
        return (item == NULL_VALUE) ? null : item;
    }
}