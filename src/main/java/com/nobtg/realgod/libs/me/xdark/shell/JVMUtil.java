package com.nobtg.realgod.libs.me.xdark.shell;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class JVMUtil {
    public static final Unsafe unsafe;
    public static final MethodHandles.Lookup lookup;

    public static NativeLibrary findJvm() {
        Path jvmDir = Paths.get(System.getProperty("java.home"));
        Path maybeJre = jvmDir.resolve("jre");
        if (Files.isDirectory(maybeJre)) {
            jvmDir = maybeJre;
        }
        jvmDir = jvmDir.resolve("bin");
        String os = System.getProperty("os.name").toLowerCase();
        Path pathToJvm;
        if (os.contains("win")) {
            pathToJvm = findFirstFile(jvmDir, "server/jvm.dll", "client/jvm.dll");
        } else if (os.contains("nix") || os.contains("nux")) {
            pathToJvm = findFirstFile(jvmDir, "lib/amd64/server/libjvm.so", "lib/i386/server/libjvm.so");
        } else {
            throw new RuntimeException("Unsupported OS (probably MacOS X): " + os);
        }
        return loadLibrary(pathToJvm.normalize().toString());
    }

    private static Path findFirstFile(Path directory, String... files) {
        for (String file : files) {
            Path path = directory.resolve(file);
            if (Files.exists(path)) return path;
        }
        throw new RuntimeException("Failed to find one of the required paths!: " + Arrays.toString(files));
    }

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
            field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(field),
                    unsafe.staticFieldOffset(field));
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public static NativeLibrary loadLibrary(String path) {
        try {
            Class<?> nativeLibraryImpl = Class.forName("jdk.internal.loader.NativeLibraries$NativeLibraryImpl", true, null);
            MethodHandle constructor = lookup.findConstructor(nativeLibraryImpl, MethodType.methodType(Void.TYPE, Class.class, String.class, Boolean.TYPE, Boolean.TYPE));

            MethodHandle open = lookup.findVirtual(nativeLibraryImpl, "open", MethodType.methodType(Boolean.TYPE));
            MethodHandle  find = lookup.findVirtual(nativeLibraryImpl, "find", MethodType.methodType(Long.TYPE, String.class));

            Object library = constructor.invoke(JVMUtil.class, path, false, false);

            open.invoke(library);

            return entry -> {
                try {
                    return (long) find.invoke(library, entry);
                } catch (Throwable t) {
                    throw new InternalError(t);
                }
            };
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
