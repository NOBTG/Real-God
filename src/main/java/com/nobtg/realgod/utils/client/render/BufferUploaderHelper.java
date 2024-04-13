package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class BufferUploaderHelper {
    public static void draw(BufferBuilder.RenderedBuffer p_231210_) {
        VertexBuffer vertexbuffer = upload(p_231210_);
        if (vertexbuffer != null) {
            VertexBufferHelper.draw(vertexbuffer);
        }
    }

    private static VertexBuffer upload(BufferBuilder.RenderedBuffer p_231214_) {
        if (RenderedBufferHelper.isEmpty(p_231214_)) {
            RenderedBufferHelper.release(p_231214_);
            return null;
        } else {
            VertexBuffer vertexbuffer = bindImmediateBuffer(p_231214_.drawState.format);
            VertexBufferHelper.upload(vertexbuffer, p_231214_);
            return vertexbuffer;
        }
    }

    private static VertexBuffer bindImmediateBuffer(VertexFormat p_231207_) {
        VertexBuffer vertexbuffer = VertexFormatHelper.getImmediateDrawVertexBuffer(p_231207_);
        bindImmediateBuffer(vertexbuffer);
        return vertexbuffer;
    }

    private static void bindImmediateBuffer(VertexBuffer p_231205_) {
        if (p_231205_ != BufferUploader.lastImmediateBuffer) {
            VertexBufferHelper.bind(p_231205_);
            BufferUploader.lastImmediateBuffer = p_231205_;
        }
    }

    public static void invalidate() {
        BufferUploader.lastImmediateBuffer = null;
    }
}
