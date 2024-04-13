package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

@OnlyIn(Dist.CLIENT)
public final class BufferBuilderHelper {
    public static void releaseRenderedBuffer(BufferBuilder builder) {
        if (builder.renderedBufferCount > 0 && --builder.renderedBufferCount == 0) {
            discard(builder);
        }
    }

    public static void discard(BufferBuilder builder) {
        builder.renderedBufferCount = 0;
        builder.renderedBufferPointer = 0;
        builder.nextElementByte = 0;
    }

    public static ByteBuffer bufferSlice(BufferBuilder builder, int p_231170_, int p_231171_) {
        return MemoryUtil.memSlice(builder.buffer, p_231170_, p_231171_ - p_231170_);
    }
}
