package tk.ivybits.javi.ffmpeg.avutil;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;
import java.util.List;


public class AVClass extends Structure {
    public static class ByReference extends AVClass implements Structure.ByReference {
    }

    public static class ByValue extends AVClass implements Structure.ByValue {
    }

    public Pointer class_name;
    public item_name_callback item_name;
    public Pointer option;
    public int version;
    public int log_level_offset_offset;
    public int parent_log_context_offset;
    public child_next_callback child_next;
    public child_class_next_callback child_class_next;
    public int category;
    public get_category_callback get_category;
    public query_ranges_callback query_ranges;

    public interface item_name_callback extends Callback {
        String apply(Pointer ctx);
    }

    public interface child_next_callback extends Callback {
        Pointer apply(Pointer obj, Pointer prev);
    }

    public interface child_class_next_callback extends Callback {
        AVClass apply(AVClass prev);
    }

    public interface get_category_callback extends Callback {
        int apply(Pointer ctx);
    }

    public interface query_ranges_callback extends Callback {
        int apply(PointerByReference AVOptionRangesPtrPtr1, Pointer obj, Pointer key, int flags);
    }

    public AVClass() {
        super();
    }

    public AVClass(Pointer address) {
        super(address);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("class_name", "item_name", "option", "version", "log_level_offset_offset",
                "parent_log_context_offset", "child_next", "child_class_next", "category",
                "get_category", "query_ranges");
    }
}
