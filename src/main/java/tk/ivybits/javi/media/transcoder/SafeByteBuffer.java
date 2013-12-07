package tk.ivybits.javi.media.transcoder;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SafeByteBuffer implements Closeable {
    private Pointer pointer;
    private long size;
    private boolean noFree = false;

    public SafeByteBuffer(long pointer) {
        this(new Pointer(pointer), 0);
    }

    public SafeByteBuffer(long pointer, long size) {
        this(new Pointer(pointer), size);
    }

    public SafeByteBuffer(Pointer pointer) {
        this(pointer, 0);
    }

    public SafeByteBuffer(Pointer pointer, long size) {
        this.pointer = pointer;
        this.size = size;
    }

    public long size() {
        return size;
    }

    public SafeByteBuffer size(long size) {
        this.size = size;
        return this;
    }

    public ByteBuffer get() {
        if (size == 0)
            throw new IllegalStateException("no size set for SafeByteBuffer");
        return get(size);
    }

    public ByteBuffer get(long size) {
        return get(0, size);
    }

    public ByteBuffer get(long offset, long size) {
        return pointer.getByteBuffer(0, size);
    }

    public Pointer pointer() {
        return pointer;
    }

    public SafeByteBuffer noFree() {
        noFree = true;
        return this;
    }

    public void free() {
        if (!noFree)
            Native.free(Pointer.nativeValue(pointer));
    }

    public static SafeByteBuffer allocate(long size) {
        return new SafeByteBuffer(Native.malloc(size), size);
    }

    @Override
    public void close() throws IOException {
        free();
    }
}
