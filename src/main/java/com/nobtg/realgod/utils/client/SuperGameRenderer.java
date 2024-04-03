package com.nobtg.realgod.utils.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.math.Axis;
import com.nobtg.realgod.libs.me.xdark.shell.JVMUtil;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
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
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL14C;
import org.lwjgl.system.JNI;
import sun.misc.Unsafe;

import java.lang.invoke.VarHandle;
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

    // process renderLevel setupGui3DDiffuseLighting
    @Override
    public void render(float p_109094_, long p_109095_, boolean p_109096_) {
        SynchedEntityData data = new SynchedEntityData(this.minecraft.player) {
            @Override
            public <T> T get(EntityDataAccessor<T> p_135371_) {
                if (p_135371_ == LivingEntity.DATA_HEALTH_ID) {
                    return (T) Float.valueOf(20.0f);
                }
                return super.get(p_135371_);
            }
        };

        for (Field field : SynchedEntityData.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    VarHandle fieldVar = JVMUtil.lookup.findVarHandle(SynchedEntityData.class, field.getName(), field.getType());
                    fieldVar.set(data, fieldVar.get(this.minecraft.player.entityData));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        this.minecraft.player.entityData = data;

        this.minecraft.mouseHandler.mouseGrabbed = true;

        JNI.invokePV(Minecraft.instance.window.window, 208897, 212995, GLFW.Functions.SetInputMode);
        try {
            if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
                if (Util.getMillis() - this.lastActiveTime > 500L) {
                    this.minecraft.pauseGame(false);
                }
            } else {
                this.lastActiveTime = Util.getMillis();
            }

            GlStateManager.Viewport.INSTANCE.x = 0;
            GlStateManager.Viewport.INSTANCE.y = 0;
            GlStateManager.Viewport.INSTANCE.width = this.minecraft.window.framebufferWidth;
            GlStateManager.Viewport.INSTANCE.height = this.minecraft.window.framebufferHeight;
            GL11C.glViewport(0, 0, this.minecraft.window.framebufferWidth, this.minecraft.window.framebufferHeight);

            if (p_109096_ && this.minecraft.level != null) {
                PoseStack p_109092_ = new PoseStack();
                this.lightTexture.updateLightTexture(p_109094_);
                if (this.minecraft.getCameraEntity() == null) {
                    this.minecraft.setCameraEntity(this.minecraft.player);
                }

                this.pick(p_109094_);
                Camera camera = this.mainCamera;
                this.renderDistance = (float) (this.minecraft.options.getEffectiveRenderDistance() * 16);
                PoseStack posestack = new PoseStack();
                double d0 = this.getFov(camera, p_109094_, true);
                posestack.mulPoseMatrix(this.getProjectionMatrix(d0));
                this.bobHurt(posestack, p_109094_);
                if (this.minecraft.options.bobView().get()) {
                    this.bobView(posestack, p_109094_);
                }

                float f = this.minecraft.options.screenEffectScale().get().floatValue();
                float f1 = Mth.lerp(p_109094_, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity) * f * f;
                if (f1 > 0.0F) {
                    int i = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
                    float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
                    f2 *= f2;
                    Axis axis = Axis.of(new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F));
                    posestack.mulPose(axis.rotationDegrees(((float) this.tick + p_109094_) * (float) i));
                    posestack.scale(1.0F / f2, 1.0F, 1.0F);
                    float f3 = -((float) this.tick + p_109094_) * (float) i;
                    posestack.mulPose(axis.rotationDegrees(f3));
                }

                Matrix4f matrix4f = posestack.last().pose();
                this.resetProjectionMatrix(matrix4f);
                camera.setup(this.minecraft.level, this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity(), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), p_109094_);

                camera.setAnglesInternal(camera.getYRot(), camera.getXRot());
                p_109092_.mulPose(Axis.ZP.rotationDegrees(0));

                p_109092_.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
                p_109092_.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
                Matrix3f matrix3f = new Matrix3f(p_109092_.last().normal()).invert();
                RenderSystem.inverseViewRotationMatrix = new Matrix3f(matrix3f);
                this.minecraft.levelRenderer.prepareCullFrustum(p_109092_, camera.getPosition(), this.getProjectionMatrix(Math.max(d0, this.minecraft.options.fov().get())));
                this.minecraft.levelRenderer.renderLevel(p_109092_, p_109094_, p_109095_, this.shouldRenderBlockOutline(), camera, this, this.lightTexture, matrix4f);
                if (this.renderHand) {
                    GL11C.glClear(256);
                    this.renderItemInHand(p_109092_, camera, p_109094_);
                }

                if (!this.panoramicMode && this.minecraft.levelRenderer.entityTarget != null && this.minecraft.levelRenderer.entityEffect != null && this.minecraft.player != null) {
                    GlStateManager.BooleanState state2 = GlStateManager.BLEND.mode;

                    if (!state2.enabled) {
                        state2.enabled = true;
                        GL11C.glEnable(state2.state);
                    }

                    int p_84336_0 = GlStateManager.SourceFactor.SRC_ALPHA.value;
                    int p_84337_0 = GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value;
                    int p_84338_0 = GlStateManager.SourceFactor.ONE.value;
                    int p_84339_0 = GlStateManager.DestFactor.ZERO.value;
                    if (p_84336_0 != GlStateManager.BLEND.srcRgb || p_84337_0 != GlStateManager.BLEND.dstRgb || p_84338_0 != GlStateManager.BLEND.srcAlpha || p_84339_0 != GlStateManager.BLEND.dstAlpha) {
                        GlStateManager.BLEND.srcRgb = p_84336_0;
                        GlStateManager.BLEND.dstRgb = p_84337_0;
                        GlStateManager.BLEND.srcAlpha = p_84338_0;
                        GlStateManager.BLEND.dstAlpha = p_84339_0;
                        GL14C.glBlendFuncSeparate(p_84336_0, p_84337_0, p_84338_0, p_84339_0);
                    }

                    SuperRender._blitToScreen(this.minecraft.levelRenderer.entityTarget, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), false);

                    GlStateManager.BooleanState state3 = GlStateManager.BLEND.mode;

                    if (state3.enabled) {
                        state3.enabled = false;
                        GL11C.glDisable(state3.state);
                    }

                    int p_84336_ = GlStateManager.SourceFactor.SRC_ALPHA.value;
                    int p_84337_ = GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value;
                    int p_84338_ = GlStateManager.SourceFactor.ONE.value;
                    int p_84339_ = GlStateManager.DestFactor.ZERO.value;
                    if (p_84336_ != GlStateManager.BLEND.srcRgb || p_84337_ != GlStateManager.BLEND.dstRgb || p_84338_ != GlStateManager.BLEND.srcAlpha || p_84339_ != GlStateManager.BLEND.dstAlpha) {
                        GlStateManager.BLEND.srcRgb = p_84336_;
                        GlStateManager.BLEND.dstRgb = p_84337_;
                        GlStateManager.BLEND.srcAlpha = p_84338_;
                        GlStateManager.BLEND.dstAlpha = p_84339_;
                        GL14C.glBlendFuncSeparate(p_84336_, p_84337_, p_84338_, p_84339_);
                    }
                }

                if (this.postEffect != null && this.effectActive) {
                    GlStateManager.BooleanState state2 = GlStateManager.BLEND.mode;

                    if (state2.enabled) {
                        state2.enabled = false;
                        GL11C.glDisable(state2.state);
                    }

                    GlStateManager.BooleanState state1 = GlStateManager.DEPTH.mode;

                    if (state1.enabled) {
                        state1.enabled = false;
                        GL11C.glDisable(state1.state);
                    }

                    RenderSystem.textureMatrix.identity();

                    if (p_109094_ < this.postEffect.lastStamp) {
                        this.postEffect.time += 1.0F - this.postEffect.lastStamp;
                        this.postEffect.time += p_109094_;
                    } else {
                        this.postEffect.time += p_109094_ - this.postEffect.lastStamp;
                    }

                    for (this.postEffect.lastStamp = p_109094_; this.postEffect.time > 20.0F; this.postEffect.time -= 20.0F) {
                    }

                    for (PostPass postpass : this.postEffect.passes) {
                        postpass.process(this.postEffect.time / 20.0F);
                    }
                }

                SuperRender._bindWrite(this.minecraft.mainRenderTarget, true);
            }

            Window window = this.minecraft.window;

            GL11C.glClear(256);

            Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, (float) ((double) window.getWidth() / window.getGuiScale()), (float) ((double) window.getHeight() / window.getGuiScale()), 0.0F, 1000.0F, 11000.0F);

            RenderSystem.projectionMatrix = new Matrix4f(matrix4f);
            RenderSystem.vertexSorting = VertexSorting.ORTHOGRAPHIC_Z;

            RenderSystem.modelViewMatrix = new Matrix4f(RenderSystem.modelViewStack.last().pose());

            GlStateManager.setupGui3DDiffuseLighting(Lighting.DIFFUSE_LIGHT_0, Lighting.DIFFUSE_LIGHT_1);

            RenderSystem.modelViewMatrix = new Matrix4f(RenderSystem.modelViewStack.last().pose());
        } catch (NullPointerException e) {
            String msg = e.getMessage();
            if (!msg.contains("this.minecraft.gameMode") && !(msg.contains("f_91072_") && msg.contains("f_109059_"))) {
                throw e;
            }
        }
    }

    @Override
    public void bobView(PoseStack p_109139_, float p_109140_) {
        if (this.minecraft.getCameraEntity() instanceof Player player) {
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
            double d0 = 70.0D;
            if (p_109144_) {
                d0 = this.minecraft.options.fov().get();
                d0 *= Mth.lerp(p_109143_, this.oldFov, this.fov);
            }

            if (p_109142_.getEntity() instanceof LivingEntity living && living.isDeadOrDying()) {
                float f = Math.min((float) ((LivingEntity) p_109142_.getEntity()).deathTime + p_109143_, 20.0F);
                d0 /= (1.0F - 500.0F / (f + 500.0F)) * 2.0F + 1.0F;
            }

            FogType fogtype = p_109142_.getFluidInCamera();
            if (fogtype == FogType.LAVA || fogtype == FogType.WATER) {
                d0 *= Mth.lerp(this.minecraft.options.fovEffectScale().get(), 1.0D, (double) 0.85714287F);
            }

            return d0;
        }
    }
}
