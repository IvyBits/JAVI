package tk.ivybits.javi.media;

import tk.ivybits.javi.media.ffmedia.FFMedia;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public final class MediaFactory {
    private MediaFactory() {
        throw new AssertionError();
    }

    public static Media open(URI uri) throws IOException {
        return new FFMedia(uri);
    }

    public static Media open(File file) throws IOException {
        return new FFMedia(file);
    }
}
