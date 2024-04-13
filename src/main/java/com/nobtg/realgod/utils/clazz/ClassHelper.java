package com.nobtg.realgod.utils.clazz;

import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import com.nobtg.realgod.utils.Triplet;
import com.nobtg.realgod.utils.file.FileHelper;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClassHelper {
    //                             class   mcp      srg
    private static final List<Triplet<String, String, String>> reNameMap = new ArrayList<>();
    private static final boolean isRunInIDE;
    public static Instrumentation inst;

    static {
        try (BufferedReader reader = new BufferedReader(new FileReader(FileHelper.downloadFile("out.txt")))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] a = line.split(",");
                for (String string : a) {
                    String[] target = string.split(";");
                    String[] strings = target[1].split(":");
                    reNameMap.add(new Triplet<>(target[0], strings[1], strings[0]));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //isRunInIDE = !new File(ClassHelper.getJarPath()).isFile();
        isRunInIDE = true;

        //inject();
    }

    public static Class<?> getCallerClass() {
        try {
            return Class.forName(new Exception().getStackTrace()[2].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isModClass(Class<?> clazz) {
        File f;
        try {
            f = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return f.getParentFile() != null && f.getParentFile().getName().equals("mods");
    }

    public static String getJarPath(Class<?>... clazz) {
        if (clazz.length == 0) {
            clazz = new Class[]{getCallerClass()};
        } else if (clazz.length > 1) {
            throw new IllegalArgumentException("More than one class found");
        }

        try {
            return new File(clazz[0].getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("URI scheme is not \"file\"")) {
                return "C:\\Users\\NOBTG\\Documents\\GitHub\\Real-God\\build\\libs\\real_god-1.0.0.jar";
            } else throw e;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopOtherThread(Thread... target) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            for (Thread thread1 : target) {
                if (thread != thread1) {
                    try {
                        JVMUtil.lookup.findVirtual(Thread.class, "suspend0", MethodType.methodType(Void.TYPE)).invoke(thread);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static Object getOuter(Object inner) {
        Object outer;
        try {
            Field field = inner.getClass().getDeclaredField(reMapName("this$0", inner.getClass().getName()));
            field.setAccessible(true);
            outer = field.get(inner);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return outer;
    }

    public static String reMapName(String mcp, String className) {
        Optional<String> mapName = Optional.of(mcp);

        for (Triplet<String, String, String> map : reNameMap) {
            if (map.first().equals(className) && map.second().equals(mcp)) {
                mapName = Optional.of(map.third());
                break;
            }
        }

        return isRunInIDE() ? mcp : mapName.get();
    }

    public static boolean isRunInIDE() {
        return isRunInIDE;
    }

    public static List<Byte> getReturn(String desc) {
        List<Byte> insnList = new ArrayList<>();
        switch (Type.getReturnType(desc).getSort()) {
            case Type.VOID -> insnList.add((byte) Opcodes.RETURN);
            case Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> {
                insnList.add((byte) Opcodes.ICONST_0);
                insnList.add((byte) Opcodes.IRETURN);
            }
            case Type.FLOAT -> {
                insnList.add((byte) Opcodes.FCONST_0);
                insnList.add((byte) Opcodes.FRETURN);
            }
            case Type.LONG -> {
                insnList.add((byte) Opcodes.LCONST_0);
                insnList.add((byte) Opcodes.LRETURN);
            }
            case Type.DOUBLE -> {
                insnList.add((byte) Opcodes.DCONST_0);
                insnList.add((byte) Opcodes.DRETURN);
            }
            case Type.ARRAY, Type.OBJECT -> {
                insnList.add((byte) Opcodes.ACONST_NULL);
                insnList.add((byte) Opcodes.ARETURN);
            }
            default -> throw new IllegalStateException("Unexpected value: " + Type.getReturnType(desc).getSort());
        }

        return insnList;
    }

    public static void inject() {
        try {
            ProcessBuilder builder = new ProcessBuilder("java", "-jar", getJarPath(), String.valueOf(ProcessHandle.current().pid()));
            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
