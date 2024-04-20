package com.nobtg.realgod.utils.file;

import com.nobtg.realgod.utils.ReflectionHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileHelper {
    public static String getPath(String name) {
        String strPath = new RealGodFile(name).getAbsolutePath();
        Path path = Path.of(strPath);

        try (InputStream in = ReflectionHelper.getCallerClass().getClassLoader().getResourceAsStream(name)) {
            assert in != null;

            if (!Files.exists(path) || Files.size(path) != in.available()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error getting file from classpath: " + name, e);
        }

        return strPath;
    }

    public static String downloadFile(String name) {
        try (InputStream in = new URI("https://raw.githubusercontent.com/NOBTG/Real-God/main/" + name).toURL().openStream()) {
            String strPath = new RealGodFile(name).getAbsolutePath();
            Path path = Path.of(strPath);

            if (!Files.exists(path) || Files.size(path) != in.available()) {
                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }

            return strPath;
        } catch (URISyntaxException | IOException e) {
            System.err.println("Error downloading file: " + name);
            System.err.println("Please check your network connection.");

            for (int i = 5; i > 0; i--) {
                System.out.println("Wait " + i + " seconds before trying again.");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }

            return downloadFile(name);
        }
    }
}
