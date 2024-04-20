package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.nobtg.realgod.utils.ReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.*;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@OnlyIn(Dist.CLIENT)
public final class GlStateManagerHelper {
    public static void _colorMask(boolean p_84301_, boolean p_84302_, boolean p_84303_, boolean p_84304_) {
        if (p_84301_ != GlStateManager.COLOR_MASK.red || p_84302_ != GlStateManager.COLOR_MASK.green || p_84303_ != GlStateManager.COLOR_MASK.blue || p_84304_ != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = p_84301_;
            GlStateManager.COLOR_MASK.green = p_84302_;
            GlStateManager.COLOR_MASK.blue = p_84303_;
            GlStateManager.COLOR_MASK.alpha = p_84304_;
            GL11C.glColorMask(p_84301_, p_84302_, p_84303_, p_84304_);
        }
    }

    public static void _disableDepthTest() {
        disable(GlStateManager.DEPTH.mode);
    }

    public static void disable(GlStateManager.BooleanState state) {
        setEnabled(state, false);
    }

    public static void enable(GlStateManager.BooleanState state) {
        setEnabled(state, true);
    }

    public static void setEnabled(GlStateManager.BooleanState state, boolean p_84591_) {
        if (p_84591_ != state.enabled) {
            state.enabled = p_84591_;
            if (p_84591_) {
                GL11C.glEnable(state.state);
            } else {
                GL11C.glDisable(state.state);
            }
        }
    }

    public static void _depthMask(boolean p_84299_) {
        if (p_84299_ != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = p_84299_;
            GL11C.glDepthMask(p_84299_);
        }
    }

    public static void _viewport(int p_84431_, int p_84432_, int p_84433_, int p_84434_) {
        GlStateManager.Viewport.INSTANCE.x = p_84431_;
        GlStateManager.Viewport.INSTANCE.y = p_84432_;
        GlStateManager.Viewport.INSTANCE.width = p_84433_;
        GlStateManager.Viewport.INSTANCE.height = p_84434_;
        GL11C.glViewport(p_84431_, p_84432_, p_84433_, p_84434_);
    }

    public static void _disableBlend() {
        disable(GlStateManager.BLEND.mode);
    }

    public static int _getActiveTexture() {
        return GlStateManager.activeTexture + '\u84c0';
    }

    public static void _activeTexture(int p_84539_) {
        if (GlStateManager.activeTexture != p_84539_ - '\u84c0') {
            GlStateManager.activeTexture = p_84539_ - '\u84c0';
            GL13C.glActiveTexture(p_84539_);
        }
    }

    public static int _glGetUniformLocation(int p_84346_, CharSequence p_84347_) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(p_84347_, true);
            long nameEncoded = stack.getPointerAddress();
            return GL20C.nglGetUniformLocation(p_84346_, nameEncoded);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    public static void _bindTexture(int p_84545_) {
        if (p_84545_ != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = p_84545_;
            GL11C.glBindTexture(3553, p_84545_);
        }
    }

    public static void _glUniformMatrix2(int p_84270_, boolean p_84271_, FloatBuffer p_84272_) {
        GL20C.nglUniformMatrix2fv(p_84270_, BufferHelper.remaining(p_84272_) >> 2, p_84271_, MemoryUtil.memAddress(p_84272_));
    }

    public static void _glUniformMatrix3(int p_84355_, boolean p_84356_, FloatBuffer p_84357_) {
        GL20C.nglUniformMatrix3fv(p_84355_, BufferHelper.remaining(p_84357_) / 9, p_84356_, MemoryUtil.memAddress(p_84357_));
    }

    public static void _glUniformMatrix4(int p_84408_, boolean p_84409_, FloatBuffer p_84410_) {
        GL20C.nglUniformMatrix4fv(p_84408_, BufferHelper.remaining(p_84410_) >> 4, p_84409_, MemoryUtil.memAddress(p_84410_));
    }

    public static void _glUniform1(int p_84264_, IntBuffer p_84265_) {
        GL20C.nglUniform1iv(p_84264_, BufferHelper.remaining(p_84265_), MemoryUtil.memAddress(p_84265_));
    }

    public static void _glUniform1(int p_84349_, FloatBuffer p_84350_) {
        GL20C.nglUniform1fv(p_84349_, BufferHelper.remaining(p_84350_), MemoryUtil.memAddress(p_84350_));
    }

    public static void _glUniform2(int p_84352_, IntBuffer p_84353_) {
        GL20C.nglUniform2iv(p_84352_, BufferHelper.remaining(p_84353_) >> 1, MemoryUtil.memAddress(p_84353_));
    }

    public static void _glUniform2(int p_84402_, FloatBuffer p_84403_) {
        GL20C.nglUniform2fv(p_84402_, BufferHelper.remaining(p_84403_) >> 1, MemoryUtil.memAddress(p_84403_));
    }

    public static void _glUniform3(int p_84405_, IntBuffer p_84406_) {
        GL20C.nglUniform3iv(p_84405_, BufferHelper.remaining(p_84406_) / 3, MemoryUtil.memAddress(p_84406_));
    }

    public static void _glUniform3(int p_84436_, FloatBuffer p_84437_) {
        GL20C.nglUniform3fv(p_84436_, BufferHelper.remaining(p_84437_) / 3, MemoryUtil.memAddress(p_84437_));
    }

    public static void _glUniform4(int p_84439_, IntBuffer p_84440_) {
        GL20C.nglUniform4iv(p_84439_, BufferHelper.remaining(p_84440_) >> 2, MemoryUtil.memAddress(p_84440_));
    }

    public static void _glUniform4(int p_84462_, FloatBuffer p_84463_) {
        GL20C.nglUniform4fv(p_84462_, BufferHelper.remaining(p_84463_) >> 2, MemoryUtil.memAddress(p_84463_));
    }

    public static void _genTextures(int[] p_84306_) {
        JNI.callPV(p_84306_.length, p_84306_, getICD().glGenTextures);
    }

    public static int _genTexture() {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            IntBuffer textures = stack.callocInt(1);
            GL11C.nglGenTextures(1, MemoryUtil.memAddress(textures));
            return textures.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    public static void _deleteTextures(int[] p_84366_) {
        for (GlStateManager.TextureState glstatemanager$texturestate : GlStateManager.TEXTURES) {
            for (int i : p_84366_) {
                if (glstatemanager$texturestate.binding == i) {
                    glstatemanager$texturestate.binding = -1;
                    break;
                }
            }
        }

        JNI.callPV(p_84366_.length, p_84366_, getICD().glDeleteTextures);
    }

    private static GLCapabilities getICD() {
        try {
            return ((GLCapabilities) ReflectionHelper.lookup.findStaticVarHandle(Class.forName("org.lwjgl.opengl.GL$ICDStatic$WriteOnce"), "caps", GLCapabilities.class).get());
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void _glBufferData(int p_84257_, ByteBuffer p_84258_, int p_84259_) {
        GL15C.nglBufferData(p_84257_, BufferHelper.remaining(p_84258_), MemoryUtil.memAddress(p_84258_), p_84259_);
    }

    public static int _glGenBuffers() {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            IntBuffer buffers = stack.callocInt(1);
            GL15C.nglGenBuffers(1, MemoryUtil.memAddress(buffers));
            return buffers.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    public static ByteBuffer _glMapBuffer(int p_157091_, int p_157092_) {
        long __result = GL15C.nglMapBuffer(p_157091_, p_157092_);
        return MemoryUtil.memByteBufferSafe(__result, glGetBufferParameteri(p_157091_, GL15C.GL_BUFFER_SIZE));
    }

    private static int glGetBufferParameteri(@NativeType("GLenum") int target, @NativeType("GLenum") int pname) {
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            IntBuffer params = stack.callocInt(1);
            GL15C.nglGetBufferParameteriv(target, pname, MemoryUtil.memAddress(params));
            return params.get(0);
        } finally {
            stack.setPointer(stackPointer);
        }
    }

    public static void _enableBlend() {
        enable(GlStateManager.BLEND.mode);
    }

    public static void _blendFuncSeparate(int p_84336_, int p_84337_, int p_84338_, int p_84339_) {
        if (p_84336_ != GlStateManager.BLEND.srcRgb || p_84337_ != GlStateManager.BLEND.dstRgb || p_84338_ != GlStateManager.BLEND.srcAlpha || p_84339_ != GlStateManager.BLEND.dstAlpha) {
            GlStateManager.BLEND.srcRgb = p_84336_;
            GlStateManager.BLEND.dstRgb = p_84337_;
            GlStateManager.BLEND.srcAlpha = p_84338_;
            GlStateManager.BLEND.dstAlpha = p_84339_;
            GL14C.glBlendFuncSeparate(p_84336_, p_84337_, p_84338_, p_84339_);
        }
    }

    public static void _blendFunc(int p_84329_, int p_84330_) {
        if (p_84329_ != GlStateManager.BLEND.srcRgb || p_84330_ != GlStateManager.BLEND.dstRgb) {
            GlStateManager.BLEND.srcRgb = p_84329_;
            GlStateManager.BLEND.dstRgb = p_84330_;
            GL11C.glBlendFunc(p_84329_, p_84330_);
        }
    }

    public static void _enableCull() {
        enable(GlStateManager.CULL.enable);
    }

    public static void _enableDepthTest() {
        enable(GlStateManager.DEPTH.mode);
    }
}
