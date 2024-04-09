package com.nobtg.realgod.utils.client;

import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.Buffer;
import java.nio.FloatBuffer;

public final class VirtualMatrix4f {
    public static final VarHandle properties;
    public static final VarHandle m00;
    public static final VarHandle m01;
    public static final VarHandle m02;
    public static final VarHandle m03;
    public static final VarHandle m10;
    public static final VarHandle m11;
    public static final VarHandle m12;
    public static final VarHandle m13;
    public static final VarHandle m20;
    public static final VarHandle m21;
    public static final VarHandle m22;
    public static final VarHandle m23;
    public static final VarHandle m30;
    public static final VarHandle m31;
    public static final VarHandle m32;
    public static final VarHandle m33;
    public static final MethodHandle identity;
    public static final MethodHandle put;
    public static final Object memUtilInstance;
    public static final VarHandle position;

    static {
        try {
            properties = JVMUtil.lookup.findVarHandle(Matrix4f.class, "properties", int.class);
            m00 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m00", float.class);
            m01 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m01", float.class);
            m02 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m02", float.class);
            m03 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m03", float.class);
            m10 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m10", float.class);
            m11 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m11", float.class);
            m12 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m12", float.class);
            m13 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m13", float.class);
            m20 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m20", float.class);
            m21 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m21", float.class);
            m22 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m22", float.class);
            m23 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m23", float.class);
            m30 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m30", float.class);
            m31 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m31", float.class);
            m32 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m32", float.class);
            m33 = JVMUtil.lookup.findVarHandle(Matrix4f.class, "m33", float.class);
            Class<?> memUtil = Class.forName("org.joml.MemUtil");

            memUtilInstance = JVMUtil.lookup.findStaticVarHandle(memUtil, "INSTANCE", memUtil).get();

            identity = JVMUtil.lookup.findVirtual(memUtil, "identity", MethodType.methodType(Void.TYPE, Matrix4f.class));
            put = JVMUtil.lookup.findVirtual(memUtil, "put", MethodType.methodType(Void.TYPE, Matrix4f.class, int.class, FloatBuffer.class));

            position = JVMUtil.lookup.findVarHandle(Buffer.class, "position", int.class);
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static int properties(Matrix4f matrix) {
        return (int) properties.get(matrix);
    }

    public static void _properties(Matrix4f matrix, int properties) {
        VirtualMatrix4f.properties.set(matrix, properties);
    }

    public static float m00(Matrix4f matrix) {
        return (float) m00.get(matrix);
    }

    public static float m01(Matrix4f matrix) {
        return (float) m01.get(matrix);
    }

    public static float m02(Matrix4f matrix) {
        return (float) m02.get(matrix);
    }

    public static float m03(Matrix4f matrix) {
        return (float) m03.get(matrix);
    }

    public static float m10(Matrix4f matrix) {
        return (float) m10.get(matrix);
    }

    public static float m11(Matrix4f matrix) {
        return (float) m11.get(matrix);
    }

    public static float m12(Matrix4f matrix) {
        return (float) m12.get(matrix);
    }

    public static float m13(Matrix4f matrix) {
        return (float) m13.get(matrix);
    }

    public static float m20(Matrix4f matrix) {
        return (float) m20.get(matrix);
    }

    public static float m21(Matrix4f matrix) {
        return (float) m21.get(matrix);
    }

    public static float m22(Matrix4f matrix) {
        return (float) m22.get(matrix);
    }

    public static float m23(Matrix4f matrix) {
        return (float) m23.get(matrix);
    }

    public static float m30(Matrix4f matrix) {
        return (float) m30.get(matrix);
    }

    public static float m31(Matrix4f matrix) {
        return (float) m31.get(matrix);
    }

    public static float m32(Matrix4f matrix) {
        return (float) m32.get(matrix);
    }

    public static float m33(Matrix4f matrix) {
        return (float) m33.get(matrix);
    }

    public static Matrix4f m00(Matrix4f matrix, float m00) {
        VirtualMatrix4f.m00.set(matrix, m00);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m00 != 1.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m01(Matrix4f matrix, float m01) {
        VirtualMatrix4f.m01.set(matrix, m01);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m01 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m02(Matrix4f matrix, float m02) {
        VirtualMatrix4f.m02.set(matrix, m02);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m02 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m03(Matrix4f matrix, float m03) {
        VirtualMatrix4f.m03.set(matrix, m03);
        if (m03 != 0.0f)
            properties.set(matrix, 0);
        return matrix;
    }

    public static Matrix4f m10(Matrix4f matrix, float m10) {
        VirtualMatrix4f.m10.set(matrix, m10);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m10 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m11(Matrix4f matrix, float m11) {
        VirtualMatrix4f.m11.set(matrix, m11);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m11 != 1.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m12(Matrix4f matrix, float m12) {
        VirtualMatrix4f.m12.set(matrix, m12);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m12 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m13(Matrix4f matrix, float m13) {
        VirtualMatrix4f.m13.set(matrix, m13);
        if (m13 != 0.0f)
            properties.set(matrix, 0);
        return matrix;
    }

    public static Matrix4f m20(Matrix4f matrix, float m20) {
        VirtualMatrix4f.m20.set(matrix, m20);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m20 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m21(Matrix4f matrix, float m21) {
        VirtualMatrix4f.m21.set(matrix, m21);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m21 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m22(Matrix4f matrix, float m22) {
        VirtualMatrix4f.m22.set(matrix, m22);
        properties.set(matrix, ((int) (properties.get(matrix))) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m22 != 1.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        return matrix;
    }

    public static Matrix4f m23(Matrix4f matrix, float m23) {
        VirtualMatrix4f.m23.set(matrix, m23);
        if (m23 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_AFFINE | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL));
        return matrix;
    }

    public static Matrix4f m30(Matrix4f matrix, float m30) {
        VirtualMatrix4f.m30.set(matrix, m30);
        if (m30 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        return matrix;
    }

    public static Matrix4f m31(Matrix4f matrix, float m31) {
        VirtualMatrix4f.m31.set(matrix, m31);
        if (m31 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        return matrix;
    }

    public static Matrix4f m32(Matrix4f matrix, float m32) {
        VirtualMatrix4f.m32.set(matrix, m32);
        if (m32 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        return matrix;
    }

    public static Matrix4f m33(Matrix4f matrix, float m33) {
        VirtualMatrix4f.m33.set(matrix, m33);
        if (m33 != 0.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_PERSPECTIVE));
        if (m33 != 1.0f)
            properties.set(matrix, ((int) (properties.get(matrix))) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL | Matrix4fc.PROPERTY_AFFINE));
        return matrix;
    }

    public static void _m00(Matrix4f matrix, float m00) {
        VirtualMatrix4f.m00.set(matrix, m00);
    }

    public static void _m01(Matrix4f matrix, float m01) {
        VirtualMatrix4f.m01.set(matrix, m01);
    }

    public static void _m02(Matrix4f matrix, float m02) {
        VirtualMatrix4f.m02.set(matrix, m02);
    }

    public static void _m03(Matrix4f matrix, float m03) {
        VirtualMatrix4f.m03.set(matrix, m03);
    }

    public static void _m10(Matrix4f matrix, float m10) {
        VirtualMatrix4f.m10.set(matrix, m10);
    }

    public static void _m11(Matrix4f matrix, float m11) {
        VirtualMatrix4f.m11.set(matrix, m11);
    }

    public static void _m12(Matrix4f matrix, float m12) {
        VirtualMatrix4f.m12.set(matrix, m12);
    }

    public static void _m13(Matrix4f matrix, float m13) {
        VirtualMatrix4f.m13.set(matrix, m13);
    }

    public static void _m20(Matrix4f matrix, float m20) {
        VirtualMatrix4f.m20.set(matrix, m20);
    }

    public static void _m21(Matrix4f matrix, float m21) {
        VirtualMatrix4f.m21.set(matrix, m21);
    }

    public static void _m22(Matrix4f matrix, float m22) {
        VirtualMatrix4f.m22.set(matrix, m22);
    }

    public static void _m23(Matrix4f matrix, float m23) {
        VirtualMatrix4f.m23.set(matrix, m23);
    }

    public static void _m30(Matrix4f matrix, float m30) {
        VirtualMatrix4f.m30.set(matrix, m30);
    }

    public static void _m31(Matrix4f matrix, float m31) {
        VirtualMatrix4f.m31.set(matrix, m31);
    }

    public static void _m32(Matrix4f matrix, float m32) {
        VirtualMatrix4f.m32.set(matrix, m32);
    }

    public static void _m33(Matrix4f matrix, float m33) {
        VirtualMatrix4f.m33.set(matrix, m33);
    }

    public static void translation(Matrix4f matrix, float x, float y, float z) {
        if ((((int) (properties.get(matrix))) & Matrix4fc.PROPERTY_IDENTITY) == 0) {
            try {
                identity.invoke(memUtilInstance, matrix);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        _m30(matrix, x);
        _m31(matrix, y);
        _m32(matrix, z);
        _properties(matrix, Matrix4fc.PROPERTY_AFFINE | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL);
    }

    public static void get(Matrix4f matrix, FloatBuffer buffer) {
        try {
            put.invoke(memUtilInstance, matrix, position.get(buffer), buffer);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void setOrtho(Matrix4f matrix, float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne) {
        if ((((int) (properties.get(matrix))) & Matrix4fc.PROPERTY_IDENTITY) == 0) {
            try {
                identity.invoke(memUtilInstance, matrix);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
        _m00(matrix, 2.0f / (right - left));
        _m11(matrix, 2.0f / (top - bottom));
        _m22(matrix, (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar));
        _m30(matrix, (right + left) / (left - right));
        _m31(matrix, (top + bottom) / (bottom - top));
        _m32(matrix, (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar));
        _properties(matrix, Matrix4fc.PROPERTY_AFFINE);
    }
}
