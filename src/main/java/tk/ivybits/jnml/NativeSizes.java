package tk.ivybits.jnml;

import com.sun.jna.Structure;
import tk.ivybits.jnml.ffmpeg.StructureUtilities;

public class NativeSizes {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("#include <libavutil/avutil.h>\n" +
                "#include <libavcodec/avcodec.h>\n" +
                "#include <libavformat/avformat.h>\n" +
                "int main() {");
        for (String clazz : JNADebug.classes) {
            Class<?> struct = Class.forName("tk.ivybits.jnml.ffmpeg." + clazz);
            Structure victim = (Structure) struct.newInstance();
            StructureUtilities.printFieldsC(victim, System.out);
            System.out.println("puts(\"\");");
            System.out.println();
        }
        System.out.println("}");
    }
}
