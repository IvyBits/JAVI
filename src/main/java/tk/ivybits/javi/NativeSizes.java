package tk.ivybits.javi;

import com.sun.jna.Structure;
import tk.ivybits.javi.ffmpeg.StructureUtilities;

public class NativeSizes {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        System.out.println("#include <avutil/avutil.h>\n" +
                "#include <avcodec/avcodec.h>\n" +
                "#include <avformat/avformat.h>\n" +
                "int main() {");
        for (String clazz : JNADebug.classes) {
            Class<?> struct = Class.forName("tk.ivybits.javi.ffmpeg." + clazz);
            Structure victim = (Structure) struct.newInstance();
            StructureUtilities.printFieldsC(victim, System.out);
            System.out.println("puts(\"\");");
            System.out.println();
        }
        System.out.println("}");
    }
}
