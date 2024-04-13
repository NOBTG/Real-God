package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public final class VertexBufferHelper {
    public static void bind(VertexBuffer buffer) {
        BufferUploaderHelper.invalidate();
        GL30C.glBindVertexArray(buffer.arrayObjectId);
    }

    public static void draw(VertexBuffer buffer) {
        GL11C.nglDrawElements(buffer.mode.asGLMode, buffer.indexCount, getIndexType(buffer).asGLType, 0L);
    }

    private static VertexFormat.IndexType getIndexType(VertexBuffer buffer) {
        RenderSystem.AutoStorageIndexBuffer buf = buffer.sequentialIndices;
        return buf != null ? buf.type : buffer.indexType;
    }


    public static void upload(VertexBuffer buffer, BufferBuilder.RenderedBuffer p_231222_) {
        if (!isInvalid(buffer)) {
            try {
                BufferBuilder.DrawState bufferbuilder$drawstate = p_231222_.drawState;
                buffer.format = uploadVertexBuffer(buffer, bufferbuilder$drawstate, RenderedBufferHelper.vertexBuffer(p_231222_));
                buffer.sequentialIndices = uploadIndexBuffer(buffer, bufferbuilder$drawstate, RenderedBufferHelper.indexBuffer(p_231222_));
                buffer.indexCount = bufferbuilder$drawstate.indexCount;
                buffer.indexType = bufferbuilder$drawstate.indexType;
                buffer.mode = bufferbuilder$drawstate.mode;
            } finally {
                RenderedBufferHelper.release(p_231222_);
            }
        }
    }

    public static boolean isInvalid(VertexBuffer buffer) {
        return buffer.arrayObjectId == -1;
    }

    private static VertexFormat uploadVertexBuffer(VertexBuffer buffer, BufferBuilder.DrawState p_231219_, ByteBuffer p_231220_) {
        boolean flag = false;
        if (!VertexFormatHelper.equals(p_231219_.format, buffer.format)) {
            if (buffer.format != null) {
                VertexFormatHelper._clearBufferState(buffer.format);
            }

            GL15C.glBindBuffer(34962, buffer.vertexBufferId);
            VertexFormatHelper._setupBufferState(p_231219_.format);
            flag = true;
        }

        if (!p_231219_.indexOnly) {
            if (!flag) {
                GL15C.glBindBuffer(34962, buffer.vertexBufferId);
            }

            GlStateManagerHelper._glBufferData(34962, p_231220_, buffer.usage.id);
        }

        return p_231219_.format;
    }

    private static RenderSystem.AutoStorageIndexBuffer uploadIndexBuffer(VertexBuffer buffer, BufferBuilder.DrawState p_231224_, ByteBuffer p_231225_) {
        if (!p_231224_.sequentialIndex) {
            GL15C.glBindBuffer(34963, buffer.indexBufferId);
            GlStateManagerHelper._glBufferData(34963, p_231225_, buffer.usage.id);
            return null;
        } else {
            RenderSystem.AutoStorageIndexBuffer buf = switch (p_231224_.mode) {
                case QUADS -> RenderSystem.sharedSequentialQuad;
                case LINES -> RenderSystem.sharedSequentialLines;
                default -> RenderSystem.sharedSequential;
            };

            if (buf != buffer.sequentialIndices || !(p_231224_.indexCount <= buf.indexCount)) {
                if (buf.name == 0) {
                    buf.name = GlStateManagerHelper._glGenBuffers();
                }

                GL15C.glBindBuffer(34963, buf.name);

                int p_157477_ = p_231224_.indexCount;
                if (!(p_157477_ <= buf.indexCount)) {
                    p_157477_ = -Math.floorDiv(-(p_157477_ * 2), buf.indexStride) * buf.indexStride;
                    VertexFormat.IndexType vertexformat$indextype = (p_157477_ & -65536) != 0 ? VertexFormat.IndexType.INT : VertexFormat.IndexType.SHORT;
                    int i = -Math.floorDiv(-(p_157477_ * vertexformat$indextype.bytes), 4) * 4;
                    GL15C.nglBufferData(34963, i, MemoryUtil.NULL, 34963);
                    ByteBuffer bytebuffer = GlStateManagerHelper._glMapBuffer(34963, 35001);
                    if (bytebuffer != null) {
                        buf.type = vertexformat$indextype;

                        it.unimi.dsi.fastutil.ints.IntConsumer intconsumer = switch (buf.type) {
                            case SHORT -> (p_157482_) -> bytebuffer.putShort((short) p_157482_);
                            default -> bytebuffer::putInt;
                        };

                        for (int j = 0; j < p_157477_; j += buf.indexStride) {
                            buf.generator.accept(intconsumer, j * buf.vertexStride / buf.indexStride);
                        }

                        GL15C.glUnmapBuffer(34963);
                        buf.indexCount = p_157477_;
                    }
                }
            }

            return buf;
        }
    }
}
