package com.nobtg.realgod.utils.file;

import com.nobtg.realgod.utils.clazz.ClassHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FileHelper {
    public static String getPath(String name) {
        String path = new RealGodFile(name).getAbsolutePath();

        try (InputStream input = ClassHelper.getCallerClass().getClassLoader().getResourceAsStream(name)) {
            assert input != null;

            Files.copy(input, Path.of(path), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return path;
    }

    public static String downloadFile(String name) {
        try (InputStream in = new URI("https://github.com/NOBTG/Real-God/blob/main/" + name).toURL().openStream()) {
            String path = new RealGodFile(name).getAbsolutePath();
            Files.copy(in, Path.of(path), StandardCopyOption.REPLACE_EXISTING);
            return path;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
