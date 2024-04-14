package com.nobtg.realgod.utils.client.render.memory;

import com.nobtg.realgod.utils.clazz.ClassHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public final class MemUtilUnsafeHelper {
    private static final long ADDRESS;
    private static final long Matrix4f_m00;

    static {
        try {
            Class<?> clazz = Class.forName("org.joml.MemUtil$MemUtilUnsafe");
            ADDRESS = (long) ClassHelper.lookup.findStaticVarHandle(clazz, "ADDRESS", long.class).get();
            Matrix4f_m00 = (long) ClassHelper.lookup.findStaticVarHandle(clazz, "Matrix4f_m00", long.class).get();
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void put(Matrix4f m, int offset, FloatBuffer dest) {
        put(m, ClassHelper.unsafe.getLong(dest, ADDRESS) + ((long) offset << 2));
    }

    public static void put(Matrix4f m, long destAddr) {
        for (int i = 0; i < 8; i++) {
            ClassHelper.unsafe.putLong(null, destAddr + (i << 3), ClassHelper.unsafe.getLong(m, Matrix4f_m00 + (i << 3)));
        }
    }
}
