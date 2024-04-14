package com.nobtg.realgod.utils.client.render.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
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
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FogType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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

        Unsafe unsafe = ClassHelper.unsafe;

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

            if (mc.level != null) {

                this.renderLevel0(p_109094_, p_109095_, new PoseStack());

                if (!panoramicMode && mc.levelRenderer.entityTarget != null && mc.levelRenderer.entityEffect != null && mc.player != null) {
                    GlStateManagerHelper._enableBlend();
                    GlStateManagerHelper._blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ZERO.value, GlStateManager.DestFactor.ONE.value);
                    RenderTargetHelper._blitToScreen(mc.levelRenderer.entityTarget, mc.window.framebufferWidth, mc.window.framebufferHeight, false);
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

            GlStateManagerHelper._disableDepthTest();
            guigraphics.bufferSource.endBatch();
            GlStateManagerHelper._enableDepthTest();

            PoseStackHelper.popPose(posestack);

            RenderSystem.modelViewMatrix = new Matrix4f(RenderSystem.modelViewStack.last().pose());
        } catch (NullPointerException e) {
            String msg = e.getMessage();
            if (msg != null && (msg.contains("f_91072_") || msg.contains("this.minecraft.gameMode"))) {
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

    public void renderLevel0(float p_109090_, long p_109091_, PoseStack p_109092_) {
        Minecraft mc = Minecraft.instance;
        assert mc.player != null;
        assert mc.level != null;

        this.lightTexture.updateLightTexture(p_109090_);

        if (mc.cameraEntity == null) {
            mc.cameraEntity = mc.player;
        }

        this.pick(p_109090_);

        boolean flag = this.shouldRenderBlockOutline();
        Camera camera = this.mainCamera;
        this.renderDistance = (float) ((mc.options.serverRenderDistance > 0 ? Math.min(mc.options.renderDistance.value, mc.options.serverRenderDistance) : mc.options.renderDistance.value) * 16);
        PoseStack posestack = new PoseStack();

        double d0 = this.getFov(camera, p_109090_, true);
        posestack.mulPoseMatrix(this.getProjectionMatrix(d0));
        this.bobHurt(posestack, p_109090_);
        if (mc.options.bobView().get()) {
            this.bobView(posestack, p_109090_);
        }

        float f = (float) (double) mc.options.screenEffectScale.value;
        float f1 = Mth.lerp(p_109090_, mc.player.oSpinningEffectIntensity, mc.player.spinningEffectIntensity) * f * f;
        if (f1 > 0.0F) {
            int i = mc.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
            float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
            f2 *= f2;
            Axis axis = Axis.of(new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F));
            posestack.mulPose(axis.rotationDegrees(((float) this.tick + p_109090_) * (float) i));
            posestack.scale(1.0F / f2, 1.0F, 1.0F);
            float f3 = -((float) this.tick + p_109090_) * (float) i;
            posestack.mulPose(axis.rotationDegrees(f3));
        }

        Matrix4f matrix4f = posestack.last().pose();

        this.resetProjectionMatrix(matrix4f);
        camera.setup(mc.level, mc.getCameraEntity() == null ? mc.player : mc.getCameraEntity(), !mc.options.getCameraType().isFirstPerson(), mc.options.getCameraType().isMirrored(), p_109090_);

        camera.setAnglesInternal(camera.getYRot(), camera.getXRot());

        p_109092_.mulPose(Axis.ZP.rotationDegrees(0));
        p_109092_.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
        p_109092_.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));

        RenderSystem.inverseViewRotationMatrix = new Matrix3f(new Matrix3f(p_109092_.last().normal()).invert());

        SuperLevelRenderer.INSTANCE.prepareCullFrustum(p_109092_, camera.getPosition(), this.getProjectionMatrix(Math.max(d0, mc.options.fov.value)));
        SuperLevelRenderer.INSTANCE.renderLevel(p_109092_, p_109090_, p_109091_, flag, camera, this, this.lightTexture, matrix4f);

        if (this.renderHand) {
            GL11C.glClear(256);
            this.renderItemInHand(p_109092_, camera, p_109090_);
        }
    }
}
