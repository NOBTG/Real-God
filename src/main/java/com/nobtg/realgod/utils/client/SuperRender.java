package com.nobtg.realgod.utils.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nobtg.realgod.utils.client.render.GlStateManagerHelper;
import com.nobtg.realgod.utils.client.render.RenderTargetHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

public final class SuperRender {
    public static boolean isSuperMode = false;

    public static void betaRenderSuper() {
        Minecraft mc = Minecraft.instance;
        long i = System.nanoTime();
        RenderTarget target = mc.mainRenderTarget;

        GL11C.glClear(16640);

        RenderTargetHelper._bindWrite(target, true);

        RenderSystem.shaderFogStart = Float.MAX_VALUE;

        GlStateManagerHelper._enableCull();

        mc.realPartialTick = mc.pause ? mc.pausePartialTick : mc.timer.partialTick;
        SuperGameRenderer.INSTANCE.render(mc.pause ? mc.pausePartialTick : mc.timer.partialTick, i, true);

        GL30C.glBindFramebuffer(36160, 0);
        RenderTargetHelper._blitToScreen(target, mc.window.framebufferWidth, mc.window.framebufferHeight, true);
    }
}
