package tk.ivybits.javi.natives;

import com.sun.jna.Platform;

import java.io.*;
import java.net.URLConnection;
import java.util.HashMap;

public class Natives {
    private static String libNameFormat = null;
    private static File dllCache = null;
    private static HashMap<String, File> libraryMap = new HashMap<String, File>();
    private static boolean initialized = false;

    public static void unpack() {
        if (initialized)
            return;
        switch (Platform.getOSType()) {
            case Platform.WINDOWS:
                libNameFormat = "tk/ivybits/javi/natives/windows/%d/%s.dll";
                break;
            case Platform.LINUX:
                libNameFormat = "tk/ivybits/javi/natives/linux/%d/%s.so";
                break;
            case Platform.MAC:
                throw new IllegalStateException("Mac is too proprietary to be allowed.\n" +
                        "O Macintosh!\n" +
                        "Land of incompatibility,\n" +
                        "Thy face is full, of the lack of software!\n" +
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
        // The DLLs that other DLLs depends on must be loaded
        unpack("avutil-52"); // All FFmpeg libraries depends on this one
        unpack("avcodec-55"); // avformat depends on this one
        initialized = true;
    }

    public static File getPath(String name) {
        return unpack(name);
    }

    public static File unpack(String name) {
        if (libraryMap.containsKey(name))
            return libraryMap.get(name);
        String jarPath = getLibraryPath(name);
        File cache = new File(dllCache.getAbsolutePath() + File.separator + jarPath.substring(jarPath.lastIndexOf("/")));
        System.out.print("Loading: " + jarPath + "...");
        URLConnection file;
        try {
            file = ClassLoader.getSystemResource(jarPath).openConnection();
        } catch (IOException e) {
            throw new IllegalStateException("failed to unpack " + name, e);
        }
        // Compare last updated time with second accuracy. Some platforms can not represent milliseconds.
        if (!cache.exists() || cache.lastModified() / 1000 != file.getLastModified() / 1000) {
            InputStream in = ClassLoader.getSystemResourceAsStream(jarPath);
            BufferedOutputStream out;
            try {
                out = new BufferedOutputStream(new FileOutputStream(cache));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("failed to unpack " + name, e);
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
                throw new IllegalStateException("failed to unpack " + name, e);
            }
            cache.setLastModified(file.getLastModified());
            System.out.println(" Loaded");
        } else {
            System.out.println(" Cached");
        }
        libraryMap.put(name, cache);
        return cache;
    }

    private static String getLibraryPath(String name) {
        return String.format(libNameFormat, Platform.is64Bit() ? 64 : 32, name);
    }
}
