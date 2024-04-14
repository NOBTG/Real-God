package com.nobtg.realgod.utils.client.render.renderer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nobtg.realgod.utils.client.render.GlStateManagerHelper;
import com.nobtg.realgod.utils.client.render.RenderTargetHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFWDropCallback;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public final class SuperRender {
    public static boolean isSuperMode = false;

    public static void renderSuper() {
        Minecraft mc = Minecraft.instance;
        long j = System.nanoTime();
        RenderTarget target = mc.mainRenderTarget;

        GL11C.glClear(16640);

        RenderTargetHelper._bindWrite(target, true);

        RenderSystem.shaderFogStart = Float.MAX_VALUE;

        GlStateManagerHelper._enableCull();

        mc.realPartialTick = mc.pause ? mc.pausePartialTick : mc.timer.partialTick;
        SuperGameRenderer.INSTANCE.render(mc.pause ? mc.pausePartialTick : mc.timer.partialTick, j, true);

        GL30C.glBindFramebuffer(36160, 0);
        RenderTargetHelper._blitToScreen(target, mc.window.framebufferWidth, mc.window.framebufferHeight, true);

        InputConstants.setupMouseCallbacks(Minecraft.instance.window.window, (p_91591_, p_91592_, p_91593_) -> Minecraft.instance.execute(() -> Minecraft.instance.mouseHandler.onMove(p_91591_, p_91592_, p_91593_)), (p_91566_, p_91567_, p_91568_, p_91569_) -> {
            Minecraft.instance.execute(() -> Minecraft.instance.mouseHandler.onPress(p_91566_, p_91567_, p_91568_, p_91569_));
        }, (p_91576_, p_91577_, p_91578_) -> {
            Minecraft.instance.execute(() -> Minecraft.instance.mouseHandler.onScroll(p_91576_, p_91577_, p_91578_));
        }, (p_91536_, p_91537_, p_91538_) -> {
            Path[] apath = new Path[p_91537_];

            for(int i = 0; i < p_91537_; ++i) {
                apath[i] = Paths.get(GLFWDropCallback.getName(p_91538_, i));
            }

            Minecraft.instance.execute(() -> {
                Minecraft.instance.mouseHandler.onDrop(p_91536_, Arrays.asList(apath));
            });
        });
    }
}
