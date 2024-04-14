package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

public final class RenderTargetHelper {
    public static void _blitToScreen(RenderTarget target, int p_83972_, int p_83973_, boolean p_83974_) {
        GlStateManagerHelper._colorMask(true, true, true, false);
        GlStateManagerHelper._disableDepthTest();
        GlStateManagerHelper._depthMask(false);
        GlStateManagerHelper._viewport(0, 0, p_83972_, p_83973_);

        if (p_83974_) {
            GlStateManagerHelper._disableBlend();
        }

        Minecraft minecraft = Minecraft.instance;
        ShaderInstance shaderinstance = minecraft.gameRenderer.blitShader;

        ShaderInstanceHelper.setSampler(shaderinstance, "DiffuseSampler", target.colorTextureId);

        Matrix4f matrix4f = new Matrix4f();
        Matrix4fHelper.setOrtho(matrix4f, 0.0F, (float) p_83972_, (float) p_83973_, 0.0F, 1000.0F, 3000.0F);

        RenderSystem.projectionMatrix = new Matrix4f(matrix4f);
        RenderSystem.vertexSorting = VertexSorting.ORTHOGRAPHIC_Z;

        if (shaderinstance.MODEL_VIEW_MATRIX != null) {
            Matrix4f matrix4f1 = new Matrix4f();
            Matrix4fHelper.translation(matrix4f1, 0.0F, 0.0F, -2000.0F);
            UniformHelper.set(shaderinstance.MODEL_VIEW_MATRIX, matrix4f1);
        }

        if (shaderinstance.PROJECTION_MATRIX != null) {
            UniformHelper.set(shaderinstance.PROJECTION_MATRIX, matrix4f);
        }

        ShaderInstanceHelper.apply(shaderinstance);

        float f = (float) p_83972_;
        float f1 = (float) p_83973_;
        float f2 = (float) target.viewWidth / (float) target.width;
        float f3 = (float) target.viewHeight / (float) target.height;

        BufferBuilder bufferbuilder = RenderSystem.RENDER_THREAD_TESSELATOR.builder;

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, f1, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(f, f1, 0.0D).uv(f2, 0.0F).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(f, 0.0D, 0.0D).uv(f2, f3).color(255, 255, 255, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, f3).color(255, 255, 255, 255).endVertex();

        BufferUploaderHelper.draw(bufferbuilder.end());

        ShaderInstanceHelper.clear(shaderinstance);
        GlStateManagerHelper._depthMask(true);
        GlStateManagerHelper._colorMask(true, true, true, true);
    }

    public static void _bindWrite(RenderTarget target, boolean p_83962_) {
        GL30C.glBindFramebuffer(36160, target.frameBufferId);
        if (p_83962_) {
            GlStateManagerHelper._viewport(0, 0, target.viewWidth, target.viewHeight);
        }
    }

    public static void clear(RenderTarget target) {
        _bindWrite(target, true);
        GL11C.glClearColor(target.clearChannels[0], target.clearChannels[1], target.clearChannels[2], target.clearChannels[3]);
        int i = 16384;
        if (target.useDepth) {
            GL11C.glClearDepth(1.0D);
            i |= 256;
        }

        GL11C.glClear(i);
        GL30C.glBindFramebuffer(36160, 0);
    }

    public static void copyDepthFrom(RenderTarget target, RenderTarget p_83946_) {
        GL30C.glBindFramebuffer(36008, p_83946_.frameBufferId);
        GL30C.glBindFramebuffer(36009, target.frameBufferId);
        GL30C.glBlitFramebuffer(0, 0, p_83946_.width, p_83946_.height, 0, 0, target.width, target.height, 256, 9728);
        GL30C.glBindFramebuffer(36160, 0);
    }
}
