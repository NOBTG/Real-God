package com.nobtg.realgod.utils.clazz;

import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import com.nobtg.realgod.utils.Pair;
import com.nobtg.realgod.utils.file.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ClassHelper {
    //                             class   mcp      srg
    private static final List<Pair<String, String, String>> reNameMap = new ArrayList<>();
    private static final boolean isRunInIDE;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FileHelper.downloadFile("out.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] a = line.split(",");
                for (String string : a) {
                    String[] target = string.split(";");
                    String[] strings = target[1].split(":");
                    reNameMap.add(new Pair<>(target[0], strings[1], strings[0]));
                }
            }

            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        isRunInIDE = !new File(ClassHelper.getJarPath(ClassHelper.class)).isFile();
    }

    public static Class<?> getCallerClass() {
        try {
            return Class.forName(new Exception().getStackTrace()[2].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isModClass(Class<?> clazz) {
        File f = new File(getJarPath(clazz));
        return f.getParentFile() != null && f.getParentFile().getName().equals("mods");
    }

    public static String getJarPath(Class<?> clazz) {
        String file = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (!file.isEmpty()) {
            if (file.startsWith("union:")) file = file.substring(6);
            if (file.startsWith("/")) file = file.substring(1);
            file = file.substring(0, file.lastIndexOf(".jar") + 4);
            file = file.replaceAll("/", "\\\\");
        }
        return URLDecoder.decode(file, StandardCharsets.UTF_8);
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
        return isRunInIDE() ? mcp : reNameMap.stream().filter(pair -> pair.a().equals(className) && pair.b().equals(mcp)).map(Pair::c).findFirst().orElse(mcp);
    }

    public static boolean isRunInIDE() {
        return isRunInIDE;
    }
}
