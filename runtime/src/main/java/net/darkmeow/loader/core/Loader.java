package net.darkmeow.loader.core;

import org.tukaani.xz.XZInputStream;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Loader {
    public static final File LOADER_DIR = new File(new File(System.getProperty("java.io.tmpdir")), "DarkLoader");

    public static URL loadMod() {
        return load();
    }

    private static URL load() {
        try {
            File file = loadFile();
            return file.toURI().toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File loadFile() throws IOException, NoSuchAlgorithmException {
        LOADER_DIR.mkdirs();

        byte[] bytes;
        final String checksum;
        try (InputStream is = Objects.requireNonNull(getModPackageStream())) {
            bytes = readBytes(is);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            checksum = toHexString(md.digest(bytes));
        }

        // 不能移除 jar 后缀
        // 否则会破坏 Reflections
        File jarFile = new File(LOADER_DIR, checksum.toLowerCase(Locale.ROOT) + ".jar");

        if (jarFile.exists()) {
            return jarFile;
        }

        try (ZipOutputStream zipOut = getZipOut(jarFile)) {
            zipOut.setMethod(ZipOutputStream.DEFLATED);
            zipOut.setLevel(Deflater.BEST_COMPRESSION);
            try (ZipInputStream tarIn = getZipIn(bytes)) {
                ZipEntry entryIn;
                String pathPrefix = "package/";
                byte[] buffer = new byte[1024];
                while ((entryIn = tarIn.getNextEntry()) != null) {
                    if (entryIn.getName().startsWith(pathPrefix)) {
                        ZipEntry entryOut = new ZipEntry(entryIn.getName().substring(pathPrefix.length()));
                        zipOut.putNextEntry(entryOut);
                        for (int len; (len = tarIn.read(buffer)) > 0; ) {
                            zipOut.write(buffer, 0, len);
                        }
                        zipOut.closeEntry();
                    }
                }
            }
        }

        return jarFile;
    }

    private static InputStream getModPackageStream() {
        return Loader.class.getClassLoader().getResourceAsStream("META-INF/NATIVE");
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private static ZipInputStream getZipIn(byte[] bytes) throws IOException {
        return new ZipInputStream(new XZInputStream(new ByteArrayInputStream(bytes)));
    }

    private static ZipOutputStream getZipOut(File file) throws IOException {
        return new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(file.toPath())));
    }

    public static byte[] readBytes(InputStream stream) throws IOException {
        byte[] buffer = new byte[Math.max(stream.available(), 1024 * 1024)];
        int read = 0;
        for (int len; (len = stream.read(buffer, read, buffer.length - read)) > 0; ) {
            read += len;
            if (buffer.length - read == 0) {
                buffer = Arrays.copyOf(buffer, buffer.length + buffer.length >> 1);
            }
        }
        return Arrays.copyOf(buffer, read);
    }

}
