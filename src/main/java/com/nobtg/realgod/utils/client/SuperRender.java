package com.nobtg.realgod.utils.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.lang.invoke.VarHandle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;

public final class SuperRender {
    public static boolean isSuperMode = false;

    public static void renderSuper() {
        Minecraft mc = Minecraft.getInstance();

        long i = Util.getNanos();

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

        int width = mc.window.getWidth();
        int height = mc.window.getHeight();

        _blitToScreen(target, width, height, true);
    }

    public static VertexBuffer getImmediateDrawVertexBuffer(VertexFormat format) {
        VertexBuffer vertexbuffer = format.immediateDrawVertexBuffer;
        if (vertexbuffer == null) {
            format.immediateDrawVertexBuffer = vertexbuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        }
        return vertexbuffer;
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

    //apply,storeRenderedBuffer
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

            Shader parent = shaderinstance.MODEL_VIEW_MATRIX.parent;
            Class<?> parentClass = parent.getClass();

            if (parentClass.equals(ShaderInstance.class)) {
                ((ShaderInstance) parent).dirty = true;
            } else if (parentClass.equals(EffectInstance.class)) {
                ((EffectInstance) parent).dirty = true;
            }
        }

        shaderinstance.apply();

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

        BufferBuilder.RenderedBuffer renderedBuffer = null;
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
                    ByteBuffer bytebuffer = MemoryTracker.resize(bufferbuilder.buffer, bufferbuilder.buffer.capacity() + roundUp(l));
                    bytebuffer.rewind();
                    bufferbuilder.buffer = bytebuffer;
                }

                bufferbuilder.putSortedQuadIndices(vertexformat$indextype);
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
            VertexBuffer vertexBuffer1 = getImmediateDrawVertexBuffer(renderedBuffer.drawState.format);

            if (vertexBuffer1 != BufferUploader.lastImmediateBuffer) {
                BufferUploader.lastImmediateBuffer = null;
                GL30C.glBindVertexArray(vertexBuffer1.arrayObjectId);

                BufferUploader.lastImmediateBuffer = vertexBuffer1;
            }

            if (vertexBuffer1.arrayObjectId != -1) {
                try {
                    BufferBuilder.DrawState bufferbuilder$drawstate = renderedBuffer.drawState;
                    boolean flag = false;
                    if (!vertexFormatEquals(bufferbuilder$drawstate.format, vertexBuffer1.format)) {
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

                        GL15C.glBufferData(34962, buffer, vertexBuffer1.usage.id);
                    }

                    vertexBuffer1.format = bufferbuilder$drawstate.format;

                    if (!bufferbuilder$drawstate.sequentialIndex) {
                        GL15C.glBindBuffer(34963, vertexBuffer1.indexBufferId);

                        BufferBuilder builder = (BufferBuilder) ClassHelper.getOuter(renderedBuffer);
                        ByteBuffer buffer;
                        int i = renderedBuffer.pointer + (renderedBuffer.drawState.indexOnly ? 0 : (renderedBuffer.drawState.vertexCount * renderedBuffer.drawState.format.vertexSize));
                        int j = renderedBuffer.pointer + renderedBuffer.drawState.indexBufferEnd();
                        buffer = MemoryUtil.memSlice(builder.buffer, i, j - i);

                        GL15C.glBufferData(34963, buffer, vertexBuffer1.usage.id);
                        vertexBuffer1.sequentialIndices = null;
                    } else {
                        RenderSystem.AutoStorageIndexBuffer autoBuffer = switch (bufferbuilder$drawstate.mode) {
                            case QUADS -> RenderSystem.sharedSequentialQuad;
                            case LINES -> RenderSystem.sharedSequentialLines;
                            default -> RenderSystem.sharedSequential;
                        };

                        if (autoBuffer != vertexBuffer1.sequentialIndices || !(bufferbuilder$drawstate.indexCount <= autoBuffer.indexCount)) {
                            if (autoBuffer.name == 0) {
                                autoBuffer.name = GL15C.glGenBuffers();
                            }

                            GL15C.glBindBuffer(34963, autoBuffer.name);

                            int p_157477_ = bufferbuilder$drawstate.indexCount;

                            if (!(p_157477_ <= autoBuffer.indexCount)) {
                                p_157477_ = Mth.roundToward(p_157477_ * 2, autoBuffer.indexStride);
                                VertexFormat.IndexType vertexformat$indextype = (p_157477_ & -65536) != 0 ? VertexFormat.IndexType.INT : VertexFormat.IndexType.SHORT;
                                int i = Mth.roundToward(p_157477_ * vertexformat$indextype.bytes, 4);
                                GL15C.nglBufferData(34963, i, MemoryUtil.NULL, 35048);
                                ByteBuffer bytebuffer = GL15C.glMapBuffer(34963, 35001);
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

    public static boolean vertexFormatEquals(VertexFormat format, Object p_86026_) {
        if (format == p_86026_) {
            return true;
        } else if (p_86026_ != null && format.getClass() == p_86026_.getClass()) {
            VertexFormat vertexformat = (VertexFormat) p_86026_;
            return format.vertexSize == vertexformat.vertexSize && format.elementMapping.equals(vertexformat.elementMapping);
        } else {
            return false;
        }
    }

    private static int roundUp(int p_85726_) {
        int i = 2097152;
        if (p_85726_ == 0) {
            return i;
        } else {
            if (p_85726_ < 0) {
                i *= -1;
            }

            int j = p_85726_ % i;
            return j == 0 ? p_85726_ : p_85726_ + i - j;
        }
    }
}
