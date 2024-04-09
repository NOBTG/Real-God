package com.nobtg.realgod.utils.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import it.unimi.dsi.fastutil.ints.IntConsumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.lang.invoke.VarHandle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class SuperRender {
    public static boolean isSuperMode = false;

    public static void renderSuper() {
        Minecraft mc = Minecraft.instance;

        long i = System.nanoTime();

        GL11C.glClear(16640);

        RenderTarget target = mc.mainRenderTarget;

        _bindWrite(target, true);

        RenderSystem.shaderFogStart = Float.MAX_VALUE;

        GlStateManager.BooleanState state = GlStateManager.CULL.enable;

        if (!state.enabled) {
            state.enabled = true;
            GL11C.glEnable(state.state);
        }

        mc.realPartialTick = mc.pause ? mc.pausePartialTick : mc.timer.partialTick;

        SuperGameRenderer.INSTANCE.render(mc.pause ? mc.pausePartialTick : mc.timer.partialTick, i, true);

        GL30C.glBindFramebuffer(36160, 0);

        int width = mc.window.framebufferWidth;
        int height = mc.window.framebufferHeight;

        _blitToScreen(target, width, height, true);
    }

    public static void _bindWrite(RenderTarget target, boolean p_83962_) {
        GL30C.glBindFramebuffer(36160, target.frameBufferId);

        if (p_83962_) {
            GlStateManager.Viewport.INSTANCE.x = 0;
            GlStateManager.Viewport.INSTANCE.y = 0;
            GlStateManager.Viewport.INSTANCE.width = target.viewWidth;
            GlStateManager.Viewport.INSTANCE.height = target.viewHeight;
            GL11C.glViewport(0, 0, target.viewWidth, target.viewHeight);
        }
    }

    public static void _blitToScreen(RenderTarget target, int width, int height, boolean p_83974_) {
        if (!GlStateManager.COLOR_MASK.red || !GlStateManager.COLOR_MASK.green || !GlStateManager.COLOR_MASK.blue || GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = true;
            GlStateManager.COLOR_MASK.green = true;
            GlStateManager.COLOR_MASK.blue = true;
            GlStateManager.COLOR_MASK.alpha = false;
            GL11C.glColorMask(true, true, true, false);
        }

        GlStateManager.BooleanState state1 = GlStateManager.DEPTH.mode;

        if (state1.enabled) {
            state1.enabled = false;
            GL11C.glDisable(state1.state);
        }

        if (GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = false;
            GL11C.glDepthMask(false);
        }

        GlStateManager.Viewport.INSTANCE.x = 0;
        GlStateManager.Viewport.INSTANCE.y = 0;
        GlStateManager.Viewport.INSTANCE.width = width;
        GlStateManager.Viewport.INSTANCE.height = height;
        GL11C.glViewport(0, 0, width, height);

        if (p_83974_) {
            GlStateManager.BooleanState state2 = GlStateManager.BLEND.mode;

            if (state2.enabled) {
                state2.enabled = false;
                GL11C.glDisable(state2.state);
            }
        }

        ShaderInstance shaderinstance = Minecraft.instance.gameRenderer.blitShader;

        shaderinstance.samplerMap.put("DiffuseSampler", target.colorTextureId);
        shaderinstance.dirty = true;

        Matrix4f matrix4f = new Matrix4f();
        VirtualMatrix4f.setOrtho(matrix4f, 0.0F, (float) width, (float) height, 0.0F, 1000.0F, 3000.0F, false);

        RenderSystem.projectionMatrix = new Matrix4f(matrix4f);
        RenderSystem.vertexSorting = VertexSorting.ORTHOGRAPHIC_Z;

        VarHandle position;
        VarHandle mark;
        try {
            position = JVMUtil.lookup.findVarHandle(Buffer.class, "position", int.class);
            mark = JVMUtil.lookup.findVarHandle(Buffer.class, "mark", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            Buffer buffer = shaderinstance.MODEL_VIEW_MATRIX.floatValues;

            if (((int) (mark.get(buffer))) > 0) mark.set(buffer, -1);
            position.set(buffer, 0);

            Matrix4f matrix4f1 = new Matrix4f();
            VirtualMatrix4f.translation(matrix4f1, 0.0F, 0.0F, -2000.0F);
            VirtualMatrix4f.get(matrix4f1, shaderinstance.MODEL_VIEW_MATRIX.floatValues);

            shaderinstance.MODEL_VIEW_MATRIX.dirty = true;

            Shader parent = shaderinstance.MODEL_VIEW_MATRIX.parent;
            Class<?> parentClass = parent.getClass();

            if (parentClass.equals(ShaderInstance.class)) {
                ((ShaderInstance) parent).dirty = true;
            } else if (parentClass.equals(EffectInstance.class)) {
                ((EffectInstance) parent).dirty = true;
            }
        }

        if (shaderinstance.PROJECTION_MATRIX != null) {
            Buffer buffer = shaderinstance.PROJECTION_MATRIX.floatValues;

            if (((int) (mark.get(buffer))) > 0) mark.set(buffer, -1);
            position.set(buffer, 0);

            VirtualMatrix4f.get(matrix4f, shaderinstance.PROJECTION_MATRIX.floatValues);

            shaderinstance.PROJECTION_MATRIX.dirty = true;

            assert shaderinstance.MODEL_VIEW_MATRIX != null;
            Shader parent = shaderinstance.MODEL_VIEW_MATRIX.parent;
            Class<?> parentClass = parent.getClass();

            if (parentClass.equals(ShaderInstance.class)) {
                ((ShaderInstance) parent).dirty = true;
            } else if (parentClass.equals(EffectInstance.class)) {
                ((EffectInstance) parent).dirty = true;
            }
        }

        {
            shaderinstance.dirty = false;
            ShaderInstance.lastAppliedShader = shaderinstance;

            boolean equals;
            if (shaderinstance.blend == BlendMode.lastApplied) {
                equals = true;
            } else if (!(BlendMode.lastApplied instanceof BlendMode)) {
                equals = false;
            } else {
                BlendMode blendmode = BlendMode.lastApplied;
                if (shaderinstance.blend.blendFunc != blendmode.blendFunc) {
                    equals = false;
                } else if (shaderinstance.blend.dstAlphaFactor != blendmode.dstAlphaFactor) {
                    equals = false;
                } else if (shaderinstance.blend.dstColorFactor != blendmode.dstColorFactor) {
                    equals = false;
                } else if (shaderinstance.blend.opaque != blendmode.opaque) {
                    equals = false;
                } else if (shaderinstance.blend.separateBlend != blendmode.separateBlend) {
                    equals = false;
                } else if (shaderinstance.blend.srcAlphaFactor != blendmode.srcAlphaFactor) {
                    equals = false;
                } else {
                    equals = shaderinstance.blend.srcColorFactor == blendmode.srcColorFactor;
                }
            }

            if (!equals) {
                if (BlendMode.lastApplied == null || shaderinstance.blend.opaque != BlendMode.lastApplied.opaque) {
                    BlendMode.lastApplied = shaderinstance.blend;
                    boolean Return = false;
                    if (shaderinstance.blend.opaque) {
                        GlStateManager.BooleanState state2 = GlStateManager.BLEND.mode;

                        if (state2.enabled) {
                            state2.enabled = false;
                            GL11C.glDisable(state2.state);
                        }
                        Return = true;
                    }

                    if (!Return) {

                        GlStateManager.BooleanState state2 = GlStateManager.BLEND.mode;

                        if (!state2.enabled) {
                            state2.enabled = true;
                            GL11C.glDisable(state2.state);
                        }
                    }
                }

                GL14C.glBlendEquation(shaderinstance.blend.blendFunc);
                if (shaderinstance.blend.separateBlend) {
                    if (shaderinstance.blend.srcColorFactor != GlStateManager.BLEND.srcRgb || shaderinstance.blend.dstColorFactor != GlStateManager.BLEND.dstRgb || shaderinstance.blend.srcAlphaFactor != GlStateManager.BLEND.srcAlpha || shaderinstance.blend.dstAlphaFactor != GlStateManager.BLEND.dstAlpha) {
                        GlStateManager.BLEND.srcRgb = shaderinstance.blend.srcColorFactor;
                        GlStateManager.BLEND.dstRgb = shaderinstance.blend.dstColorFactor;
                        GlStateManager.BLEND.srcAlpha = shaderinstance.blend.srcAlphaFactor;
                        GlStateManager.BLEND.dstAlpha = shaderinstance.blend.dstAlphaFactor;
                        GL14C.glBlendFuncSeparate(shaderinstance.blend.srcColorFactor, shaderinstance.blend.dstColorFactor, shaderinstance.blend.srcAlphaFactor, shaderinstance.blend.dstAlphaFactor);
                    }
                } else {
                    if (shaderinstance.blend.srcColorFactor != GlStateManager.BLEND.srcRgb || shaderinstance.blend.dstColorFactor != GlStateManager.BLEND.dstRgb) {
                        GlStateManager.BLEND.srcRgb = shaderinstance.blend.srcColorFactor;
                        GlStateManager.BLEND.dstRgb = shaderinstance.blend.dstColorFactor;
                        GL11C.glBlendFunc(shaderinstance.blend.srcColorFactor, shaderinstance.blend.dstColorFactor);
                    }
                }
            }

            if (shaderinstance.programId != ShaderInstance.lastProgramId) {
                GL20C.glUseProgram(shaderinstance.programId);
                ShaderInstance.lastProgramId = shaderinstance.programId;
            }

            int i = GlStateManager.activeTexture + '\u84c0';

            for (int j = 0; j < shaderinstance.samplerLocations.size(); ++j) {
                String s = shaderinstance.samplerNames.get(j);
                if (shaderinstance.samplerMap.get(s) != null) {
                    MemoryStack stack = MemoryStack.stackGet();
                    int stackPointer = stack.getPointer();
                    int k;
                    try {
                        stack.nASCII(s, true);
                        long nameEncoded = stack.getPointerAddress();
                        k = GL20C.nglGetUniformLocation(shaderinstance.programId, nameEncoded);
                    } finally {
                        stack.setPointer(stackPointer);
                    }

                    GL20C.glUniform1i(k, j);

                    if (GlStateManager.activeTexture != j) {
                        GlStateManager.activeTexture = j;
                        GL13C.glActiveTexture(j);
                    }

                    Object object = shaderinstance.samplerMap.get(s);
                    int l = -1;
                    if (object instanceof RenderTarget renderTarget) {
                        l = renderTarget.colorTextureId;
                    } else if (object instanceof AbstractTexture texture) {
                        if (texture.id == -1) {
                            if (SharedConstants.IS_RUNNING_IN_IDE) {
                                int[] aint = new int[ThreadLocalRandom.current().nextInt(15) + 1];
                                GLCapabilities capabilities;

                                try {
                                    capabilities = ((GLCapabilities) JVMUtil.lookup.findStaticVarHandle(Class.forName("org.lwjgl.opengl.GL$ICDStatic$WriteOnce"), "caps", GLCapabilities.class).get());
                                } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
                                    throw new RuntimeException(e);
                                }

                                JNI.callPV(aint.length, aint, capabilities.glGenTextures);

                                int g;
                                MemoryStack stack1 = MemoryStack.stackGet();
                                int stackPointer1 = stack1.getPointer();
                                try {
                                    IntBuffer textures = stack1.callocInt(1);
                                    GL11C.nglGenTextures(1, MemoryUtil.memAddress(textures));
                                    g = textures.get(0);
                                } finally {
                                    stack1.setPointer(stackPointer1);
                                }

                                for (GlStateManager.TextureState glstatemanager$texturestate : GlStateManager.TEXTURES) {
                                    for (int i1 : aint) {
                                        if (glstatemanager$texturestate.binding == i1) {
                                            glstatemanager$texturestate.binding = -1;
                                            break;
                                        }
                                    }
                                }

                                JNI.callPV(aint.length, aint, capabilities.glDeleteTextures);
                                texture.id = g;
                            } else {
                                MemoryStack stack1 = MemoryStack.stackGet();
                                int stackPointer1 = stack1.getPointer();
                                try {
                                    IntBuffer textures = stack1.callocInt(1);
                                    GL11C.nglGenTextures(1, MemoryUtil.memAddress(textures));
                                    texture.id = textures.get(0);
                                } finally {
                                    stack1.setPointer(stackPointer1);
                                }
                            }
                        }

                        l = texture.id;
                    } else if (object instanceof Integer integer) {
                        l = integer;
                    }

                    if (l != -1) {
                        if (l != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
                            GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = l;
                            GL11C.glBindTexture(3553, l);
                        }
                    }
                }
            }

            if (GlStateManager.activeTexture != i - '\u84c0') {
                GlStateManager.activeTexture = i - '\u84c0';
                GL13C.glActiveTexture(i);
            }

            for (Uniform uniform : shaderinstance.uniforms) {
                uniform.dirty = false;
                if (uniform.type <= 3) {
                    position.set(uniform.intValues, 0);
                    mark.set(uniform.intValues, -1);
                    switch (uniform.type) {
                        case 0:
                            GL20C.nglUniform1iv(uniform.location, uniform.intValues.remaining(), MemoryUtil.memAddress(uniform.intValues));
                            break;
                        case 1:
                            GL20C.nglUniform2iv(uniform.location, uniform.intValues.remaining() >> 1, MemoryUtil.memAddress(uniform.intValues));
                            break;
                        case 2:
                            GL20C.nglUniform3iv(uniform.location, uniform.intValues.remaining() / 3, MemoryUtil.memAddress(uniform.intValues));
                            break;
                        case 3:
                            GL20C.nglUniform4iv(uniform.location, uniform.intValues.remaining() >> 2, MemoryUtil.memAddress(uniform.intValues));
                            break;
                    }
                } else if (uniform.type <= 7) {
                    position.set(uniform.floatValues, 0);
                    mark.set(uniform.floatValues, -1);
                    switch (uniform.type) {
                        case 4:
                            GL20C.nglUniform1fv(uniform.location, uniform.floatValues.remaining(), MemoryUtil.memAddress(uniform.floatValues));
                            break;
                        case 5:
                            GL20C.nglUniform2fv(uniform.location, uniform.floatValues.remaining() >> 1, MemoryUtil.memAddress(uniform.floatValues));
                            break;
                        case 6:
                            GL20C.nglUniform3fv(uniform.location, uniform.floatValues.remaining() / 3, MemoryUtil.memAddress(uniform.floatValues));
                            break;
                        case 7:
                            GL20C.nglUniform4fv(uniform.location, uniform.floatValues.remaining() >> 2, MemoryUtil.memAddress(uniform.floatValues));
                            break;
                    }
                } else {
                    position.set(uniform.floatValues, 0);
                    try {
                        JVMUtil.lookup.findVarHandle(Buffer.class, "limit", int.class).set(uniform.floatValues, JVMUtil.lookup.findVarHandle(Buffer.class, "capacity", int.class).get(uniform.floatValues));
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    mark.set(uniform.floatValues, -1);
                    switch (uniform.type) {
                        case 8:
                            GL20C.nglUniformMatrix2fv(uniform.location, uniform.floatValues.remaining() >> 2, false, MemoryUtil.memAddress(uniform.floatValues));
                            break;
                        case 9:
                            GL20C.nglUniformMatrix3fv(uniform.location, uniform.floatValues.remaining() / 9, false, MemoryUtil.memAddress(uniform.floatValues));
                            break;
                        case 10:
                            GL20C.nglUniformMatrix4fv(uniform.location, uniform.floatValues.remaining() >> 4, false, MemoryUtil.memAddress(uniform.floatValues));
                    }
                }
            }
        }

        float f = (float) width;
        float f1 = (float) height;
        float f2 = (float) target.viewWidth / (float) target.width;
        float f3 = (float) target.viewHeight / (float) target.height;

        Tesselator tesselator = RenderSystem.RENDER_THREAD_TESSELATOR;

        BufferBuilder bufferbuilder = tesselator.builder;

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, f1, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(f, f1, 0.0D).uv(f2, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(f, 0.0D, 0.0D).uv(f2, f3).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, f3).color(255, 255, 255, 255).endVertex();

        VertexBuffer vertexBuffer;

        BufferBuilder.RenderedBuffer renderedBuffer;
        {
            int i = switch (bufferbuilder.mode) {
                case LINE_STRIP, DEBUG_LINES, DEBUG_LINE_STRIP, TRIANGLES, TRIANGLE_STRIP, TRIANGLE_FAN ->
                        bufferbuilder.vertices;
                case LINES, QUADS -> bufferbuilder.vertices / 4 * 6;
            };

            int j = !bufferbuilder.indexOnly ? bufferbuilder.vertices * bufferbuilder.format.vertexSize : 0;
            VertexFormat.IndexType vertexformat$indextype = (i & -65536) != 0 ? VertexFormat.IndexType.INT : VertexFormat.IndexType.SHORT;
            boolean flag;
            int k;
            if (bufferbuilder.sortingPoints != null) {
                int l = Mth.roundToward(i * vertexformat$indextype.bytes, 4);

                if (bufferbuilder.nextElementByte + l > bufferbuilder.buffer.capacity()) {
                    ByteBuffer bytebuffer = MemoryTracker.resize(bufferbuilder.buffer, bufferbuilder.buffer.capacity() + (l == 0 ? 2097152 : (l < 0 ? l - l % -2097152 : l + 2097152 - l % 2097152)));
                    position.set(bytebuffer, 0);
                    mark.set(bytebuffer, -1);
                    bufferbuilder.buffer = bytebuffer;
                }

                if (bufferbuilder.sortingPoints != null && bufferbuilder.sorting != null) {
                    int[] aint = bufferbuilder.sorting.sort(bufferbuilder.sortingPoints);
                    IntConsumer intconsumer = bufferbuilder.intConsumer(bufferbuilder.nextElementByte, vertexformat$indextype);

                    for(int i2 : aint) {
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride);
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride + 1);
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride + 2);
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride + 2);
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride + 3);
                        intconsumer.accept(i2 * bufferbuilder.mode.primitiveStride);
                    }

                }

                flag = false;
                bufferbuilder.nextElementByte += l;
                k = j + l;
            } else {
                flag = true;
                k = j;
            }

            int i1 = bufferbuilder.renderedBufferPointer;
            bufferbuilder.renderedBufferPointer += k;
            ++bufferbuilder.renderedBufferCount;
            renderedBuffer = bufferbuilder.new RenderedBuffer(i1, new BufferBuilder.DrawState(bufferbuilder.format, bufferbuilder.vertices, i, bufferbuilder.mode, vertexformat$indextype, bufferbuilder.indexOnly, flag));

            bufferbuilder.building = false;
            bufferbuilder.vertices = 0;
            bufferbuilder.currentElement = null;
            bufferbuilder.elementIndex = 0;
            bufferbuilder.sortingPoints = null;
            bufferbuilder.sorting = null;
            bufferbuilder.indexOnly = false;
        }

        if (renderedBuffer.drawState.vertexCount == 0) {
            if (!renderedBuffer.released) {
                BufferBuilder builder = (BufferBuilder) ClassHelper.getOuter(renderedBuffer);

                if (builder.renderedBufferCount > 0 && --builder.renderedBufferCount == 0) {
                    builder.renderedBufferPointer = 0;
                    builder.nextElementByte = 0;
                }

                renderedBuffer.released = true;
            }

            vertexBuffer = null;
        } else {
            if (renderedBuffer.drawState.format.immediateDrawVertexBuffer == null) {
                renderedBuffer.drawState.format.immediateDrawVertexBuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
            }
            VertexBuffer vertexBuffer1 = renderedBuffer.drawState.format.immediateDrawVertexBuffer;

            if (vertexBuffer1 != BufferUploader.lastImmediateBuffer) {
                BufferUploader.lastImmediateBuffer = null;
                GL30C.glBindVertexArray(vertexBuffer1.arrayObjectId);

                BufferUploader.lastImmediateBuffer = vertexBuffer1;
            }

            if (vertexBuffer1.arrayObjectId != -1) {
                try {
                    BufferBuilder.DrawState bufferbuilder$drawstate = renderedBuffer.drawState;
                    boolean flag = false;

                    if (!(bufferbuilder$drawstate.format == vertexBuffer1.format || (vertexBuffer1.format != null && bufferbuilder$drawstate.format.getClass() == vertexBuffer1.format.getClass() && (bufferbuilder$drawstate.format.vertexSize == vertexBuffer1.format.vertexSize && bufferbuilder$drawstate.format.elementMapping.equals(vertexBuffer1.format.elementMapping))))) {
                        if (vertexBuffer1.format != null) {
                            ImmutableList<VertexFormatElement> immutablelist = vertexBuffer1.format.elements;
                            for (int i = 0; i < vertexBuffer1.format.elements.size(); ++i) {
                                VertexFormatElement vertexformatelement = immutablelist.get(i);
                                vertexformatelement.usage.clearState.clearBufferState(vertexformatelement.index, i);
                            }
                        }

                        GL15C.glBindBuffer(34962, vertexBuffer1.vertexBufferId);
                        int i = bufferbuilder$drawstate.format.vertexSize;
                        List<VertexFormatElement> list = bufferbuilder$drawstate.format.elements;

                        for (int j = 0; j < list.size(); ++j) {
                            VertexFormatElement vertexformatelement = list.get(j);
                            vertexformatelement.usage.setupState.setupBufferState(vertexformatelement.count, vertexformatelement.type.glType, i, bufferbuilder$drawstate.format.offsets.getInt(j), vertexformatelement.index, j);
                        }
                        flag = true;
                    }

                    if (!bufferbuilder$drawstate.indexOnly) {
                        if (!flag) {
                            GL15C.glBindBuffer(34962, vertexBuffer1.vertexBufferId);
                        }

                        BufferBuilder builder = (BufferBuilder) ClassHelper.getOuter(renderedBuffer);
                        ByteBuffer buffer;
                        int i = renderedBuffer.pointer;
                        int j = renderedBuffer.pointer + (renderedBuffer.drawState.vertexCount * renderedBuffer.drawState.format.vertexSize);
                        buffer = MemoryUtil.memSlice(builder.buffer, i, j - i);

                        GL15C.nglBufferData(34962, buffer.remaining(), MemoryUtil.memAddress(buffer), vertexBuffer1.usage.id);
                    }

                    vertexBuffer1.format = bufferbuilder$drawstate.format;

                    if (!bufferbuilder$drawstate.sequentialIndex) {
                        GL15C.glBindBuffer(34963, vertexBuffer1.indexBufferId);

                        BufferBuilder builder = (BufferBuilder) ClassHelper.getOuter(renderedBuffer);
                        ByteBuffer buffer;
                        int i = renderedBuffer.pointer + (renderedBuffer.drawState.indexOnly ? 0 : (renderedBuffer.drawState.vertexCount * renderedBuffer.drawState.format.vertexSize));
                        int j = renderedBuffer.pointer + renderedBuffer.drawState.indexBufferEnd();
                        buffer = MemoryUtil.memSlice(builder.buffer, i, j - i);

                        GL15C.nglBufferData(34963, buffer.remaining(), MemoryUtil.memAddress(buffer), vertexBuffer1.usage.id);
                        vertexBuffer1.sequentialIndices = null;
                    } else {
                        RenderSystem.AutoStorageIndexBuffer autoBuffer = switch (bufferbuilder$drawstate.mode) {
                            case QUADS -> RenderSystem.sharedSequentialQuad;
                            case LINES -> RenderSystem.sharedSequentialLines;
                            default -> RenderSystem.sharedSequential;
                        };

                        if (autoBuffer != vertexBuffer1.sequentialIndices || !(bufferbuilder$drawstate.indexCount <= autoBuffer.indexCount)) {
                            if (autoBuffer.name == 0) {
                                MemoryStack stack = MemoryStack.stackGet();
                                int stackPointer = stack.getPointer();
                                try {
                                    IntBuffer buffers = stack.callocInt(1);
                                    GL15C.nglGenBuffers(1, MemoryUtil.memAddress(buffers));
                                    autoBuffer.name = buffers.get(0);
                                } finally {
                                    stack.setPointer(stackPointer);
                                }
                            }

                            GL15C.glBindBuffer(34963, autoBuffer.name);

                            int p_157477_ = bufferbuilder$drawstate.indexCount;

                            if (!(p_157477_ <= autoBuffer.indexCount)) {
                                p_157477_ = Mth.roundToward(p_157477_ * 2, autoBuffer.indexStride);
                                VertexFormat.IndexType vertexformat$indextype = (p_157477_ & -65536) != 0 ? VertexFormat.IndexType.INT : VertexFormat.IndexType.SHORT;
                                int i = Mth.roundToward(p_157477_ * vertexformat$indextype.bytes, 4);
                                GL15C.nglBufferData(34963, i, MemoryUtil.NULL, 35048);

                                ByteBuffer bytebuffer = MemoryUtil.memByteBufferSafe(GL15C.nglMapBuffer(34963, 35001), GL15C.glGetBufferParameteri(34963, GL15C.GL_BUFFER_SIZE));

                                if (bytebuffer != null) {
                                    autoBuffer.type = vertexformat$indextype;

                                    it.unimi.dsi.fastutil.ints.IntConsumer intconsumer = switch (autoBuffer.type) {
                                        case SHORT -> p_157482_ -> bytebuffer.putShort((short) p_157482_);
                                        default -> bytebuffer::putInt;
                                    };

                                    for (int j = 0; j < p_157477_; j += autoBuffer.indexStride) {
                                        autoBuffer.generator.accept(intconsumer, j * autoBuffer.vertexStride / autoBuffer.indexStride);
                                    }

                                    GL15C.glUnmapBuffer(34963);
                                    autoBuffer.indexCount = p_157477_;
                                }
                            }
                        }

                        vertexBuffer1.sequentialIndices = autoBuffer;
                    }

                    vertexBuffer1.indexCount = bufferbuilder$drawstate.indexCount;
                    vertexBuffer1.indexType = bufferbuilder$drawstate.indexType;
                    vertexBuffer1.mode = bufferbuilder$drawstate.mode;
                } finally {
                    if (!renderedBuffer.released) {
                        BufferBuilder builder = (BufferBuilder) ClassHelper.getOuter(renderedBuffer);
                        if (builder.renderedBufferCount > 0 && --builder.renderedBufferCount == 0) {
                            builder.renderedBufferPointer = 0;
                            builder.nextElementByte = 0;
                        }
                        renderedBuffer.released = true;
                    }
                }
            }

            vertexBuffer = vertexBuffer1;
        }

        if (vertexBuffer != null) {
            VertexFormat.IndexType type = vertexBuffer.sequentialIndices != null ? vertexBuffer.sequentialIndices.type : vertexBuffer.indexType;
            GL11C.nglDrawElements(vertexBuffer.mode.asGLMode, vertexBuffer.indexCount, type.asGLType, 0L);
        }

        GL20C.glUseProgram(0);
        ShaderInstance.lastProgramId = -1;
        ShaderInstance.lastAppliedShader = null;
        int i = GlStateManager.activeTexture + '\u84c0';

        for (int j = 0; j < shaderinstance.samplerLocations.size(); ++j) {
            if (shaderinstance.samplerMap.get(shaderinstance.samplerNames.get(j)) != null) {
                if (GlStateManager.activeTexture != j) {
                    GlStateManager.activeTexture = j;
                    GL13C.glActiveTexture(('\u84c0' + j));
                }
                if (0 != GlStateManager.TEXTURES[GlStateManager.activeTexture].binding) {
                    GlStateManager.TEXTURES[GlStateManager.activeTexture].binding = 0;
                    GL11C.glBindTexture(3553, 0);
                }
            }
        }

        if (GlStateManager.activeTexture != i - '\u84c0') {
            GlStateManager.activeTexture = i - '\u84c0';
            GL13C.glActiveTexture(i);
        }

        if (!GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = true;
            GL11C.glDepthMask(true);
        }

        if (!GlStateManager.COLOR_MASK.red || !GlStateManager.COLOR_MASK.green || !GlStateManager.COLOR_MASK.blue || !GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = true;
            GlStateManager.COLOR_MASK.green = true;
            GlStateManager.COLOR_MASK.blue = true;
            GlStateManager.COLOR_MASK.alpha = true;
            GL11C.glColorMask(true, true, true, true);
        }
    }
}
