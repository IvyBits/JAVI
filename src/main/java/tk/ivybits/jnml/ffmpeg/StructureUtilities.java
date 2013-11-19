package tk.ivybits.jnml.ffmpeg;

import com.sun.jna.Structure;

import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.List;

public class StructureUtilities {
    public static void printFields(Structure victim) {
        try {
            String name = victim.getClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            System.out.println(name + ":");
            System.out.println("  size: " + victim.size());
            System.out.println("  offsets:");
            Method getFieldOrder = Structure.class.getDeclaredMethod("getFieldOrder");
            getFieldOrder.setAccessible(true);
            Method fieldOffset = Structure.class.getDeclaredMethod("fieldOffset", String.class);
            fieldOffset.setAccessible(true);
            for (String field : (List<String>) getFieldOrder.invoke(victim)) {
                System.out.printf("    %-30s: %d\n", field, fieldOffset.invoke(victim, field));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void printFieldsC(Structure victim, OutputStream out) {
        try {
            String name = victim.getClass().getName();
            name = name.substring(name.lastIndexOf(".") + 1);
            System.out.println("puts(\"" + name + ":\");");
            System.out.println("printf(\"  size: %d\\n\", sizeof(" + name + "));");
            System.out.println("puts(\"  offsets:\");");
            Method getFieldOrder = Structure.class.getDeclaredMethod("getFieldOrder");
            getFieldOrder.setAccessible(true);
            for (String field : (List<String>) getFieldOrder.invoke(victim)) {
                System.out.println("printf(\"    %-30s: %d\\n\", \"" + field + "\", offsetof(" + name + ", " + field + "));");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
