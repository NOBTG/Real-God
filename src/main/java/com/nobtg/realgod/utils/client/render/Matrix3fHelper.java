package com.nobtg.realgod.utils.client.render;

import com.nobtg.realgod.utils.ReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@OnlyIn(Dist.CLIENT)
public final class Matrix3fHelper {
    private static final VarHandle m00;
    private static final VarHandle m01;
    private static final VarHandle m02;
    private static final VarHandle m10;
    private static final VarHandle m11;
    private static final VarHandle m12;
    private static final VarHandle m20;
    private static final VarHandle m21;
    private static final VarHandle m22;

    static {
        MethodHandles.Lookup lookup = ReflectionHelper.lookup;
        try {
            m00 = lookup.findVarHandle(Matrix3f.class, "m00", float.class);
            m01 = lookup.findVarHandle(Matrix3f.class, "m01", float.class);
            m02 = lookup.findVarHandle(Matrix3f.class, "m02", float.class);
            m10 = lookup.findVarHandle(Matrix3f.class, "m10", float.class);
            m11 = lookup.findVarHandle(Matrix3f.class, "m11", float.class);
            m12 = lookup.findVarHandle(Matrix3f.class, "m12", float.class);
            m20 = lookup.findVarHandle(Matrix3f.class, "m20", float.class);
            m21 = lookup.findVarHandle(Matrix3f.class, "m21", float.class);
            m22 = lookup.findVarHandle(Matrix3f.class, "m22", float.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void _m00(Matrix3f matrix3f, float m00) {
        Matrix3fHelper.m00.set(matrix3f, m00);
    }

    public static void _m01(Matrix3f matrix3f, float m01) {
        Matrix3fHelper.m01.set(matrix3f, m01);
    }

    public static void _m02(Matrix3f matrix3f, float m02) {
        Matrix3fHelper.m02.set(matrix3f, m02);
    }

    public static void _m10(Matrix3f matrix3f, float m10) {
        Matrix3fHelper.m10.set(matrix3f, m10);
    }

    public static void _m11(Matrix3f matrix3f, float m11) {
        Matrix3fHelper.m11.set(matrix3f, m11);
    }

    public static void _m12(Matrix3f matrix3f, float m12) {
        Matrix3fHelper.m12.set(matrix3f, m12);
    }

    public static void _m20(Matrix3f matrix3f, float m20) {
        Matrix3fHelper.m20.set(matrix3f, m20);
    }

    public static void _m21(Matrix3f matrix3f, float m21) {
        Matrix3fHelper.m21.set(matrix3f, m21);
    }

    public static void _m22(Matrix3f matrix3f, float m22) {
        Matrix3fHelper.m22.set(matrix3f, m22);
    }

    public static void memUtilIdentity(Matrix3f dest) {
        _m00(dest, 1.0f);
        _m01(dest, 0.0f);
        _m02(dest, 0.0f);
        _m10(dest, 0.0f);
        _m11(dest, 1.0f);
        _m12(dest, 0.0f);
        _m20(dest, 0.0f);
        _m21(dest, 0.0f);
        _m22(dest, 1.0f);
    }
}
