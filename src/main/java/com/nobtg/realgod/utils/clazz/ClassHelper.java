package com.nobtg.realgod.utils.clazz;

import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public final class ClassHelper {
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
            if (file.startsWith("union:"))
                file = file.substring(6);
            if (file.startsWith("/"))
                file = file.substring(1);
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
            Field field = inner.getClass().getDeclaredField("this$0");
            field.setAccessible(true);
            outer = field.get(inner);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        return outer;
    }

    public static void set(Class<?> clazz, String name, Object instance, Object value, Class<?> fieldType) {
        try {
            Field target = clazz.getDeclaredField(name);
            Unsafe unsafe = JVMUtil.unsafe;
            unsafe.putObject(instance, unsafe.objectFieldOffset(target), value);
        } catch (NoSuchFieldException e) {
            MethodHandles.Lookup lookup = JVMUtil.lookup;
            try {
                lookup.findVarHandle(clazz, name, fieldType).set(instance, value);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public static void setStatic(Class<?> clazz, String name, Object value, Class<?> fieldType) {
        try {
            Field target = clazz.getDeclaredField(name);
            Unsafe unsafe = JVMUtil.unsafe;
            unsafe.putObject(unsafe.staticFieldBase(target), unsafe.staticFieldOffset(target), value);
        } catch (NoSuchFieldException e) {
            MethodHandles.Lookup lookup = JVMUtil.lookup;
            try {
                lookup.findStaticVarHandle(clazz, name, fieldType).set(value);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
