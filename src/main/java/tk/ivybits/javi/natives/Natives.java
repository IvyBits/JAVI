package tk.ivybits.javi.natives;

import com.sun.jna.Platform;

import java.io.*;

public class Natives {
    private static String libNameFormat = null;
    private static File dllCache = null;

    public static void unpack() {
        switch (Platform.getOSType()) {
            case Platform.WINDOWS:
                libNameFormat = "windows/%d/%s.dll";
                break;
            case Platform.LINUX:
                libNameFormat = "linux/%d/%s.so";
                break;
            case Platform.MAC:
                throw new IllegalStateException("Mac is too proprietary to be allowed.\n" +
                        "O Macintosh!\n" +
                        "Land of incompatibility,\n" +
                        "Thy face is full, of lack of software!\n" +
                        "As thy arm is ready to release, a new SDK!\n" +
                        "Thy developers, is in grief, once more like it always is!\n" +
                        "And thy SDK, is like the moon.\n" +
                        "Break our apps, once and for all, AGAIN!\n" +
                        "BREAK OUR APPS, ONCE AND FOR ALL, FOR EVER!");
            default:
                throw new RuntimeException("We are sorry but thy beloved platform is supported not");
        }
        dllCache = new File(System.getProperty("java.io.tmpdir") + File.separator + "FFmpeg_libs");
        dllCache.mkdirs();
        unpack("avutil-55");
        unpack("avcodec-55");
        unpack("avformat-55");
        unpack("swscale-2");
        unpack("swresample-0");
        if (System.getProperty("jna.library.path") == null)
            System.setProperty("jna.library.path", dllCache.getAbsolutePath());
        else
            System.setProperty("jna.library.path",
                    System.getProperty("jna.library.path") + File.pathSeparator + dllCache.getAbsolutePath());
    }

    public static File unpack(String name) {
        String jarPath = getLibraryPath(name);
        File cache = new File(dllCache.getAbsolutePath() + File.separator + jarPath.substring(jarPath.lastIndexOf("/")));
        InputStream in = ClassLoader.getSystemResourceAsStream(jarPath);
        BufferedOutputStream out;
        try {
            out = new BufferedOutputStream(new FileOutputStream(cache));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int len;
        byte[] buf = new byte[65536];
        try {
            while ((len = in.read(buf)) > -1) {
                out.write(buf, 0, len);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return cache;
    }

    public static String getLibraryPath(String name) {
        return String.format(libNameFormat, Platform.is64Bit() ? 64 : 32, name);
    }
}
