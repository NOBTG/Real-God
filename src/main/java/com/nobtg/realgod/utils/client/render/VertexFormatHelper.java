package com.nobtg.realgod.utils.client.render;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class VertexFormatHelper {
    public static VertexBuffer getImmediateDrawVertexBuffer(VertexFormat vertexFormat) {
        VertexBuffer vertexbuffer = vertexFormat.immediateDrawVertexBuffer;
        if (vertexbuffer == null) {
            vertexFormat.immediateDrawVertexBuffer = vertexbuffer = new VertexBuffer(VertexBuffer.Usage.DYNAMIC);
        }
        return vertexbuffer;
    }

    public static void _clearBufferState(VertexFormat format) {
        ImmutableList<VertexFormatElement> immutablelist = format.elements;
        for(int i = 0; i < immutablelist.size(); ++i) {
            VertexFormatElement vertexformatelement = immutablelist.get(i);
            vertexformatelement.usage.clearState.clearBufferState(vertexformatelement.index, i);
        }
    }

    public static void _setupBufferState(VertexFormat format) {
        int i = format.vertexSize;
        List<VertexFormatElement> list = format.elements;

        for(int j = 0; j < list.size(); ++j) {
            VertexFormatElement element = list.get(j);
            element.usage.setupState.setupBufferState(element.count, element.type.glType, i, format.offsets.getInt(j), element.index, j);
        }
    }

    public static boolean equals(VertexFormat format, Object p_86026_) {
        if (format == p_86026_) {
            return true;
        } else if (p_86026_ != null && format.getClass() == p_86026_.getClass()) {
            VertexFormat vertexformat = (VertexFormat)p_86026_;
            return format.vertexSize == vertexformat.vertexSize && format.elementMapping.equals(vertexformat.elementMapping);
        } else {
            return false;
        }
    }
}
