package com.nobtg.realgod.utils.client.render;

import com.nobtg.realgod.utils.ReflectionHelper;
import com.nobtg.realgod.utils.client.render.memory.MemUtilNIOHelper;
import com.nobtg.realgod.utils.client.render.memory.MemUtilUnsafeHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.FloatBuffer;

@OnlyIn(Dist.CLIENT)
public final class Matrix4fHelper {
    private static final VarHandle properties;
    private static final VarHandle m00;
    private static final VarHandle m01;
    private static final VarHandle m02;
    private static final VarHandle m03;
    private static final VarHandle m10;
    private static final VarHandle m11;
    private static final VarHandle m12;
    private static final VarHandle m13;
    private static final VarHandle m20;
    private static final VarHandle m21;
    private static final VarHandle m22;
    private static final VarHandle m23;
    private static final VarHandle m30;
    private static final VarHandle m31;
    private static final VarHandle m32;
    private static final VarHandle m33;
    private static final Object memUtilInstance;

    static {
        MethodHandles.Lookup lookup = ReflectionHelper.lookup;
        try {
            properties = lookup.findVarHandle(Matrix4f.class, "properties", int.class);
            m00 = lookup.findVarHandle(Matrix4f.class, "m00", float.class);
            m01 = lookup.findVarHandle(Matrix4f.class, "m01", float.class);
            m02 = lookup.findVarHandle(Matrix4f.class, "m02", float.class);
            m03 = lookup.findVarHandle(Matrix4f.class, "m03", float.class);
            m10 = lookup.findVarHandle(Matrix4f.class, "m10", float.class);
            m11 = lookup.findVarHandle(Matrix4f.class, "m11", float.class);
            m12 = lookup.findVarHandle(Matrix4f.class, "m12", float.class);
            m13 = lookup.findVarHandle(Matrix4f.class, "m13", float.class);
            m20 = lookup.findVarHandle(Matrix4f.class, "m20", float.class);
            m21 = lookup.findVarHandle(Matrix4f.class, "m21", float.class);
            m22 = lookup.findVarHandle(Matrix4f.class, "m22", float.class);
            m23 = lookup.findVarHandle(Matrix4f.class, "m23", float.class);
            m30 = lookup.findVarHandle(Matrix4f.class, "m30", float.class);
            m31 = lookup.findVarHandle(Matrix4f.class, "m31", float.class);
            m32 = lookup.findVarHandle(Matrix4f.class, "m32", float.class);
            m33 = lookup.findVarHandle(Matrix4f.class, "m33", float.class);

            Class<?> clazz = Class.forName("org.joml.MemUtil");
            memUtilInstance = lookup.findStaticVarHandle(clazz, "INSTANCE", clazz).get();
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static int properties(Matrix4f matrix4f) {
        return (int) properties.get(matrix4f);
    }

    public static float m00(Matrix4f matrix4f) {
        return (float) m00.get(matrix4f);
    }

    public static float m01(Matrix4f matrix4f) {
        return (float) m01.get(matrix4f);
    }

    public static float m02(Matrix4f matrix4f) {
        return (float) m02.get(matrix4f);
    }

    public static float m03(Matrix4f matrix4f) {
        return (float) m03.get(matrix4f);
    }

    public static float m10(Matrix4f matrix4f) {
        return (float) m10.get(matrix4f);
    }

    public static float m11(Matrix4f matrix4f) {
        return (float) m11.get(matrix4f);
    }

    public static float m12(Matrix4f matrix4f) {
        return (float) m12.get(matrix4f);
    }

    public static float m13(Matrix4f matrix4f) {
        return (float) m13.get(matrix4f);
    }

    public static float m20(Matrix4f matrix4f) {
        return (float) m20.get(matrix4f);
    }

    public static float m21(Matrix4f matrix4f) {
        return (float) m21.get(matrix4f);
    }

    public static float m22(Matrix4f matrix4f) {
        return (float) m22.get(matrix4f);
    }

    public static float m23(Matrix4f matrix4f) {
        return (float) m23.get(matrix4f);
    }

    public static float m30(Matrix4f matrix4f) {
        return (float) m30.get(matrix4f);
    }

    public static float m31(Matrix4f matrix4f) {
        return (float) m31.get(matrix4f);
    }

    public static float m32(Matrix4f matrix4f) {
        return (float) m32.get(matrix4f);
    }

    public static float m33(Matrix4f matrix4f) {
        return (float) m33.get(matrix4f);
    }

    public static void m00(Matrix4f matrix4f, float m00) {
        Matrix4fHelper.m00.set(matrix4f, m00);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m00 != 1.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m01(Matrix4f matrix4f, float m01) {
        Matrix4fHelper.m01.set(matrix4f, m01);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m01 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m02(Matrix4f matrix4f, float m02) {
        Matrix4fHelper.m02.set(matrix4f, m02);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m02 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m03(Matrix4f matrix4f, float m03) {
        Matrix4fHelper.m03.set(matrix4f, m03);
        if (m03 != 0.0f) {
            properties.set(matrix4f, 0);
        }
    }

    public static void m10(Matrix4f matrix4f, float m10) {
        Matrix4fHelper.m10.set(matrix4f, m10);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m10 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m11(Matrix4f matrix4f, float m11) {
        Matrix4fHelper.m11.set(matrix4f, m11);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m11 != 1.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m12(Matrix4f matrix4f, float m12) {
        Matrix4fHelper.m12.set(matrix4f, m12);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m12 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m13(Matrix4f matrix4f, float m13) {
        Matrix4fHelper.m13.set(matrix4f, m13);
        if (m13 != 0.0f) {
            properties.set(matrix4f, 0);
        }
    }

    public static void m20(Matrix4f matrix4f, float m20) {
        Matrix4fHelper.m20.set(matrix4f, m20);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m20 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m21(Matrix4f matrix4f, float m21) {
        Matrix4fHelper.m21.set(matrix4f, m21);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m21 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m22(Matrix4f matrix4f, float m22) {
        Matrix4fHelper.m22.set(matrix4f, m22);
        properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_ORTHONORMAL);
        if (m22 != 1.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION));
        }
    }

    public static void m23(Matrix4f matrix4f, float m23) {
        Matrix4fHelper.m23.set(matrix4f, m23);
        if (m23 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_AFFINE | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL));
        }
    }

    public static void m30(Matrix4f matrix4f, float m30) {
        Matrix4fHelper.m30.set(matrix4f, m30);
        if (m30 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        }
    }

    public static void m31(Matrix4f matrix4f, float m31) {
        Matrix4fHelper.m31.set(matrix4f, m31);
        if (m31 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        }
    }

    public static void m32(Matrix4f matrix4f, float m32) {
        Matrix4fHelper.m32.set(matrix4f, m32);
        if (m32 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_PERSPECTIVE));
        }
    }

    public static void m33(Matrix4f matrix4f, float m33) {
        Matrix4fHelper.m33.set(matrix4f, m33);
        if (m33 != 0.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~Matrix4fc.PROPERTY_PERSPECTIVE);
        }
        if (m33 != 1.0f) {
            properties.set(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL | Matrix4fc.PROPERTY_AFFINE));
        }
    }

    public static void _m00(Matrix4f matrix4f, float m00) {
        Matrix4fHelper.m00.set(matrix4f, m00);
    }

    public static void _m01(Matrix4f matrix4f, float m01) {
        Matrix4fHelper.m01.set(matrix4f, m01);
    }

    public static void _m02(Matrix4f matrix4f, float m02) {
        Matrix4fHelper.m02.set(matrix4f, m02);
    }

    public static void _m03(Matrix4f matrix4f, float m03) {
        Matrix4fHelper.m03.set(matrix4f, m03);
    }

    public static void _m10(Matrix4f matrix4f, float m10) {
        Matrix4fHelper.m10.set(matrix4f, m10);
    }

    public static void _m11(Matrix4f matrix4f, float m11) {
        Matrix4fHelper.m11.set(matrix4f, m11);
    }

    public static void _m12(Matrix4f matrix4f, float m12) {
        Matrix4fHelper.m12.set(matrix4f, m12);
    }

    public static void _m13(Matrix4f matrix4f, float m13) {
        Matrix4fHelper.m13.set(matrix4f, m13);
    }

    public static void _m20(Matrix4f matrix4f, float m20) {
        Matrix4fHelper.m20.set(matrix4f, m20);
    }

    public static void _m21(Matrix4f matrix4f, float m21) {
        Matrix4fHelper.m21.set(matrix4f, m21);
    }

    public static void _m22(Matrix4f matrix4f, float m22) {
        Matrix4fHelper.m22.set(matrix4f, m22);
    }

    public static void _m23(Matrix4f matrix4f, float m23) {
        Matrix4fHelper.m23.set(matrix4f, m23);
    }

    public static void _m30(Matrix4f matrix4f, float m30) {
        Matrix4fHelper.m30.set(matrix4f, m30);
    }

    public static void _m31(Matrix4f matrix4f, float m31) {
        Matrix4fHelper.m31.set(matrix4f, m31);
    }

    public static void _m32(Matrix4f matrix4f, float m32) {
        Matrix4fHelper.m32.set(matrix4f, m32);
    }

    public static void _m33(Matrix4f matrix4f, float m33) {
        Matrix4fHelper.m33.set(matrix4f, m33);
    }

    public static void _properties(Matrix4f matrix4f, int properties) {
        Matrix4fHelper.properties.set(matrix4f, properties);
    }

    public static void setOrtho(Matrix4f matrix4f, float left, float right, float bottom, float top, float zNear, float zFar) {
        setOrtho(matrix4f, left, right, bottom, top, zNear, zFar, false);
    }

    public static void setOrtho(Matrix4f matrix4f, float left, float right, float bottom, float top, float zNear, float zFar, boolean zZeroToOne) {
        if ((((int) properties.get(matrix4f)) & Matrix4fc.PROPERTY_IDENTITY) == 0) {
            memUtilIdentity(matrix4f);
        }
        _m00(matrix4f, 2.0f / (right - left));
        _m11(matrix4f, 2.0f / (top - bottom));
        _m22(matrix4f, (zZeroToOne ? 1.0f : 2.0f) / (zNear - zFar));
        _m30(matrix4f, (right + left) / (left - right));
        _m31(matrix4f, (top + bottom) / (bottom - top));
        _m32(matrix4f, (zZeroToOne ? zNear : (zFar + zNear)) / (zNear - zFar));
        _properties(matrix4f, Matrix4fc.PROPERTY_AFFINE);
    }

    public static void translation(Matrix4f matrix4f, float x, float y, float z) {
        if ((((int) properties.get(matrix4f)) & Matrix4fc.PROPERTY_IDENTITY) == 0) {
            memUtilIdentity(matrix4f);
        }
        _m30(matrix4f, x);
        _m31(matrix4f, y);
        _m32(matrix4f, z);
        _properties(matrix4f, Matrix4fc.PROPERTY_AFFINE | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL);
    }

    public static void get(Matrix4f matrix4f, FloatBuffer buffer) {
        try {
            Class<?> clazz = memUtilInstance.getClass();
            if (clazz.equals(Class.forName("org.joml.MemUtil$MemUtilNIO"))) {
                MemUtilNIOHelper.put(matrix4f, BufferHelper.position(buffer), buffer);
            } else if (clazz.equals(Class.forName("org.joml.MemUtil$MemUtilUnsafe"))) {
                MemUtilUnsafeHelper.put(matrix4f, BufferHelper.position(buffer), buffer);
            } else throw new AssertionError();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void memUtilIdentity(Matrix4f matrix4f) {
        _m00(matrix4f, 1.0f);
        _m01(matrix4f, 0.0f);
        _m02(matrix4f, 0.0f);
        _m03(matrix4f, 0.0f);
        _m10(matrix4f, 0.0f);
        _m11(matrix4f, 1.0f);
        _m12(matrix4f, 0.0f);
        _m13(matrix4f, 0.0f);
        _m20(matrix4f, 0.0f);
        _m21(matrix4f, 0.0f);
        _m22(matrix4f, 1.0f);
        _m23(matrix4f, 0.0f);
        _m30(matrix4f, 0.0f);
        _m31(matrix4f, 0.0f);
        _m32(matrix4f, 0.0f);
        _m33(matrix4f, 1.0f);
    }

    public static void identity(Matrix4f matrix4f) {
        if ((((int) properties.get(matrix4f)) & Matrix4fc.PROPERTY_IDENTITY) != 0) {
            return;
        }
        _m00(matrix4f, 1.0f);
        _m01(matrix4f, 0.0f);
        _m02(matrix4f, 0.0f);
        _m03(matrix4f, 0.0f);
        _m10(matrix4f, 0.0f);
        _m11(matrix4f, 1.0f);
        _m12(matrix4f, 0.0f);
        _m13(matrix4f, 0.0f);
        _m20(matrix4f, 0.0f);
        _m21(matrix4f, 0.0f);
        _m22(matrix4f, 1.0f);
        _m23(matrix4f, 0.0f);
        _m30(matrix4f, 0.0f);
        _m31(matrix4f, 0.0f);
        _m32(matrix4f, 0.0f);
        _m33(matrix4f, 1.0f);
        _properties(matrix4f, Matrix4fc.PROPERTY_IDENTITY | Matrix4fc.PROPERTY_AFFINE | Matrix4fc.PROPERTY_TRANSLATION | Matrix4fc.PROPERTY_ORTHONORMAL);
    }

    public static void translate(Matrix4f matrix4f, float x, float y, float z) {
        if ((((int) properties.get(matrix4f)) & Matrix4fc.PROPERTY_IDENTITY) != 0) {
            translation(matrix4f, x, y, z);
        }
        translateGeneric(matrix4f, x, y, z);
    }

    private static void translateGeneric(Matrix4f matrix4f, float x, float y, float z) {
        _m30(matrix4f, Math.fma(m00(matrix4f), x, Math.fma(m10(matrix4f), y, Math.fma(m20(matrix4f), z, m30(matrix4f)))));
        _m31(matrix4f, Math.fma(m01(matrix4f), x, Math.fma(m11(matrix4f), y, Math.fma(m21(matrix4f), z, m31(matrix4f)))));
        _m32(matrix4f, Math.fma(m02(matrix4f), x, Math.fma(m12(matrix4f), y, Math.fma(m22(matrix4f), z, m32(matrix4f)))));
        _m33(matrix4f, Math.fma(m03(matrix4f), x, Math.fma(m13(matrix4f), y, Math.fma(m23(matrix4f), z, m33(matrix4f)))));
        _properties(matrix4f, ((int) properties.get(matrix4f)) & ~(Matrix4fc.PROPERTY_PERSPECTIVE | Matrix4fc.PROPERTY_IDENTITY));
    }
}
