package tk.ivybits.javi.ffmpeg.avcodec;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class AVPicture extends Structure {
    public Pointer[] data = new Pointer[AV_NUM_DATA_POINTERS];
    public int[] linesize = new int[AV_NUM_DATA_POINTERS];

    public static final int AV_NUM_DATA_POINTERS = 8;

    public AVPicture(Pointer address) {
        super(address);
        read();
    }

    public AVPicture() {
        super();
    }

    @Override
    protected List getFieldOrder() {
        return Arrays.asList("data", "linesize");
    }
}
