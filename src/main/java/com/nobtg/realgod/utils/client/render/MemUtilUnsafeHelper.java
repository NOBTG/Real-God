package com.nobtg.realgod.utils.client.render;

import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
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
            ADDRESS = (long) JVMUtil.lookup.findStaticVarHandle(clazz, "ADDRESS", long.class).get();
            Matrix4f_m00 = (long) JVMUtil.lookup.findStaticVarHandle(clazz, "Matrix4f_m00", long.class).get();
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void put(Matrix4f m, int offset, FloatBuffer dest) {
        put(m, JVMUtil.unsafe.getLong(dest, ADDRESS) + ((long) offset << 2));
    }

    public static void put(Matrix4f m, long destAddr) {
        for (int i = 0; i < 8; i++) {
            JVMUtil.unsafe.putLong(null, destAddr + (i << 3), JVMUtil.unsafe.getLong(m, Matrix4f_m00 + (i << 3)));
        }
    }
}
