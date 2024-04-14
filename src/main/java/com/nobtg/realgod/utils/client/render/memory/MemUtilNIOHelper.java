package com.nobtg.realgod.utils.client.render.memory;

import com.nobtg.realgod.utils.client.render.Matrix4fHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public final class MemUtilNIOHelper {
    public static void put(Matrix4f m, int offset, FloatBuffer dest) {
        if (offset == 0) {
            put0(m, dest);
        } else {
            putN(m, offset, dest);
        }
    }

    public static void put0(Matrix4f m, FloatBuffer dest) {
        dest.put(0, Matrix4fHelper.m00(m))
                .put(1, Matrix4fHelper.m01(m))
                .put(2, Matrix4fHelper.m02(m))
                .put(3, Matrix4fHelper.m03(m))
                .put(4, Matrix4fHelper.m10(m))
                .put(5, Matrix4fHelper.m11(m))
                .put(6, Matrix4fHelper.m12(m))
                .put(7, Matrix4fHelper.m13(m))
                .put(8, Matrix4fHelper.m20(m))
                .put(9, Matrix4fHelper.m21(m))
                .put(10, Matrix4fHelper.m22(m))
                .put(11, Matrix4fHelper.m23(m))
                .put(12, Matrix4fHelper.m30(m))
                .put(13, Matrix4fHelper.m31(m))
                .put(14, Matrix4fHelper.m32(m))
                .put(15, Matrix4fHelper.m33(m));
    }

    public static void putN(Matrix4f m, int offset, FloatBuffer dest) {
        dest.put(offset, Matrix4fHelper.m00(m))
                .put(offset + 1, Matrix4fHelper.m01(m))
                .put(offset + 2, Matrix4fHelper.m02(m))
                .put(offset + 3, Matrix4fHelper.m03(m))
                .put(offset + 4, Matrix4fHelper.m10(m))
                .put(offset + 5, Matrix4fHelper.m11(m))
                .put(offset + 6, Matrix4fHelper.m12(m))
                .put(offset + 7, Matrix4fHelper.m13(m))
                .put(offset + 8, Matrix4fHelper.m20(m))
                .put(offset + 9, Matrix4fHelper.m21(m))
                .put(offset + 10, Matrix4fHelper.m22(m))
                .put(offset + 11, Matrix4fHelper.m23(m))
                .put(offset + 12, Matrix4fHelper.m30(m))
                .put(offset + 13, Matrix4fHelper.m31(m))
                .put(offset + 14, Matrix4fHelper.m32(m))
                .put(offset + 15, Matrix4fHelper.m33(m));
    }
}
