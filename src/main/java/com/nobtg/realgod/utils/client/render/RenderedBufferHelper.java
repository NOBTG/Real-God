package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public final class RenderedBufferHelper {
    public static boolean isEmpty(BufferBuilder.RenderedBuffer buffer) {
        return buffer.drawState.vertexCount == 0;
    }

    public static void release(BufferBuilder.RenderedBuffer buffer) {
        if (!buffer.released) {
            BufferBuilderHelper.releaseRenderedBuffer((BufferBuilder) ClassHelper.getOuter(buffer));
            buffer.released = true;
        }
    }

    public static ByteBuffer vertexBuffer(BufferBuilder.RenderedBuffer buffer) {
        int j = buffer.pointer + (buffer.drawState.vertexCount * buffer.drawState.format.vertexSize);
        return BufferBuilderHelper.bufferSlice((BufferBuilder) ClassHelper.getOuter(buffer), buffer.pointer, j);
    }

    public static ByteBuffer indexBuffer(BufferBuilder.RenderedBuffer buffer) {
        int i = buffer.pointer + (buffer.drawState.indexOnly ? 0 : (buffer.drawState.vertexCount * buffer.drawState.format.vertexSize));
        int j = buffer.pointer + (buffer.drawState.indexOnly ? 0 : (buffer.drawState.vertexCount * buffer.drawState.format.vertexSize)) + (buffer.drawState.sequentialIndex ? 0 : buffer.drawState.indexCount * buffer.drawState.indexType.bytes);
        return BufferBuilderHelper.bufferSlice((BufferBuilder) ClassHelper.getOuter(buffer), i, j);
    }
}
