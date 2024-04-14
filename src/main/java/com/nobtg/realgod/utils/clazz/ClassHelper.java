package com.nobtg.realgod.utils.clazz;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public final class ClassHelper {
    public static MethodHandles.Lookup lookup;
    public static Unsafe unsafe;

    static {
        Field lookupF;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
            lookupF = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupF), unsafe.staticFieldOffset(lookupF));
    }

    public static Class<?> getCallerClass() {
        try {
            return Class.forName(new Exception().getStackTrace()[2].getClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void stopOtherThread(Thread... target) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            for (Thread thread1 : target) {
                if (thread != thread1) {
                    try {
                        ClassHelper.lookup.findVirtual(Thread.class, "suspend0", MethodType.methodType(Void.TYPE)).invoke(thread);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public static Object getOuter(Object inner, String... name) {
        Object outer;
        try {
            Field field = inner.getClass().getDeclaredField("this$0");
            field.setAccessible(true);
            outer = field.get(inner);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Field field = inner.getClass().getDeclaredField(name[0]);
                field.setAccessible(true);
                outer = field.get(inner);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return outer;
    }
}
