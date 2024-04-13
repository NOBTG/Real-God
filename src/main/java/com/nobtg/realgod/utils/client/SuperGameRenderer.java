package com.nobtg.realgod.utils.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.nobtg.realgod.utils.client.render.GlStateManagerHelper;
import com.nobtg.realgod.utils.client.render.Matrix4fHelper;
import com.nobtg.realgod.utils.client.render.PoseStackHelper;
import com.nobtg.realgod.utils.client.render.RenderTargetHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11C;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@OnlyIn(Dist.CLIENT)
public final class SuperGameRenderer extends GameRenderer {
    static {
        Minecraft mc = Minecraft.getInstance();
        SuperGameRenderer preInstance = new SuperGameRenderer(mc, mc.entityRenderDispatcher.itemInHandRenderer, mc.resourceManager, mc.renderBuffers);

        mc.resourceManager.registerReloadListener(preInstance.createReloadListener());
        preInstance.preloadUiShader(mc.vanillaPackResources.asProvider());

        Unsafe unsafe = JVMUtil.unsafe;

        for (Field field : GameRenderer.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                long offset = unsafe.objectFieldOffset(field);
                unsafe.putObject(preInstance, offset, unsafe.getObject(mc.gameRenderer, offset));
            }
        }

        INSTANCE = preInstance;
    }

    public static final SuperGameRenderer INSTANCE;

    public SuperGameRenderer(Minecraft p_234219_, ItemInHandRenderer p_234220_, ResourceManager p_234221_, RenderBuffers p_234222_) {
        super(p_234219_, p_234220_, p_234221_, p_234222_);
    }

    @Override
    public void render(float p_109094_, long p_109095_, boolean p_109096_) {
        try {
            Minecraft mc = Minecraft.instance;

            if (mc.windowActive && !mc.options.pauseOnLostFocus && (mc.options.touchscreen.value || mc.mouseHandler.isRightPressed)) {
                this.lastActiveTime = System.nanoTime() / 1000000L;
            }

            GlStateManagerHelper._viewport(0, 0, mc.window.framebufferWidth, mc.window.framebufferHeight);

            if (p_109096_ && mc.level != null) {

                this.renderLevel(p_109094_, p_109095_, new PoseStack());

                if (!panoramicMode && mc.levelRenderer.entityTarget != null && mc.levelRenderer.entityEffect != null && mc.player != null) {
                    GlStateManagerHelper._enableBlend();
                    GlStateManagerHelper._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ZERO.value, GlStateManager.DestFactor.ONE.value);
                    RenderTargetHelper._blitToScreen(mc.levelRenderer.entityTarget, mc.window.framebufferWidth, mc.window.framebufferHeight ,false);
                    GlStateManagerHelper._disableBlend();
                    GlStateManagerHelper._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
                }

                RenderTargetHelper._bindWrite(mc.mainRenderTarget, true);
            }

            Window window = mc.window;
            GL11C.glClear(256);

            Matrix4f matrix4f = new Matrix4f();
            Matrix4fHelper.setOrtho(matrix4f, 0.0F, (float) ((double) window.framebufferWidth / window.guiScale), (float) ((double) window.framebufferHeight / window.guiScale), 0.0F, 1000.0F, 11000.0F);

            RenderSystem.projectionMatrix = new Matrix4f(matrix4f);
            RenderSystem.vertexSorting = VertexSorting.ORTHOGRAPHIC_Z;

            PoseStack posestack = RenderSystem.modelViewStack;
            PoseStackHelper.pushPose(posestack);
            PoseStackHelper.setIdentity(posestack);
            PoseStackHelper.translate(posestack, 0.0D, 0.0D, -10000.0F);
            RenderSystem.modelViewMatrix = new Matrix4f(PoseStackHelper.last(RenderSystem.modelViewStack).pose);

            Lighting.setupFor3DItems();

            GuiGraphics guigraphics = new GuiGraphics(mc, this.renderBuffers.bufferSource);

            if (p_109096_ && mc.level != null) {
                if (!mc.options.hideGui || mc.screen != null) {
                    this.renderItemActivationAnimation(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), p_109094_);
                    mc.gui.render(guigraphics, p_109094_);
                    GL11C.glClear(256);
                }
            }

            GlStateManagerHelper._disableDepthTest();
            guigraphics.bufferSource.endBatch();
            GlStateManagerHelper._enableDepthTest();

            PoseStackHelper.popPose(posestack);

            RenderSystem.modelViewMatrix = new Matrix4f(RenderSystem.modelViewStack.last().pose());
        } catch (NullPointerException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains(ClassHelper.reMapName("gameMode", Minecraft.instance.getClass().getName()))) {
                throw e;
            }
        }
    }

    @Override
    public void bobView(PoseStack p_109139_, float p_109140_) {
        Minecraft mc = Minecraft.instance;
        if (mc.getCameraEntity() instanceof Player player) {
            float f = player.walkDist - player.walkDistO;
            float f1 = -(player.walkDist + f * p_109140_);
            float f2 = Mth.lerp(p_109140_, player.oBob, player.bob);
            p_109139_.translate(Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F, -Math.abs(Mth.cos(f1 * (float) Math.PI) * f2), 0.0F);
            p_109139_.mulPose(Axis.ZP.rotationDegrees(Mth.sin(f1 * (float) Math.PI) * f2 * 3.0F));
            p_109139_.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F));
        }
    }

    @Override
    public double getFov(Camera p_109142_, float p_109143_, boolean p_109144_) {
        if (this.panoramicMode) {
            return 90.0D;
        } else {
            Minecraft mc = Minecraft.instance;
            double d0 = 70.0D;
            if (p_109144_) {
                d0 = mc.options.fov().get();
                d0 *= Mth.lerp(p_109143_, this.oldFov, this.fov);
            }

            if (p_109142_.getEntity() instanceof LivingEntity living && living.isDeadOrDying()) {
                float f = Math.min((float) ((LivingEntity) p_109142_.getEntity()).deathTime + p_109143_, 20.0F);
                d0 /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
            }

            FogType fogtype = p_109142_.getFluidInCamera();
            if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
                d0 *= Mth.lerp(mc.options.fovEffectScale().get(), 1.0D, (double) 0.85714287F);
            }

            return d0;
        }
    }
}
