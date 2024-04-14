package com.nobtg.realgod.utils.client.render.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.nobtg.realgod.utils.client.render.GlStateManagerHelper;
import com.nobtg.realgod.utils.client.render.PoseStackHelper;
import com.nobtg.realgod.utils.client.render.RenderTargetHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.SortedSet;

public final class SuperLevelRenderer extends LevelRenderer {
    static {
        Minecraft mc = Minecraft.instance;
        SuperLevelRenderer preInstance = new SuperLevelRenderer(mc, mc.entityRenderDispatcher, mc.blockEntityRenderDispatcher, mc.renderBuffers);

        mc.resourceManager.registerReloadListener(preInstance);

        Unsafe unsafe = ClassHelper.unsafe;

        for (Field field : LevelRenderer.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                long offset = unsafe.objectFieldOffset(field);
                unsafe.putObject(preInstance, offset, unsafe.getObject(mc.levelRenderer, offset));
            }
        }

        INSTANCE = preInstance;
    }

    public static final SuperLevelRenderer INSTANCE;

    public SuperLevelRenderer(Minecraft p_234245_, EntityRenderDispatcher p_234246_, BlockEntityRenderDispatcher p_234247_, RenderBuffers p_234248_) {
        super(p_234245_, p_234246_, p_234247_, p_234248_);
    }

    @Override
    public void renderLevel(PoseStack p_109600_, float p_109601_, long p_109602_, boolean p_109603_, Camera p_109604_, GameRenderer p_109605_, LightTexture p_109606_, Matrix4f p_254120_) {
        RenderSystem.setShaderGameTime(this.level.getGameTime(), p_109601_);
        this.blockEntityRenderDispatcher.prepare(this.level, p_109604_, this.minecraft.hitResult);
        this.entityRenderDispatcher.prepare(this.level, p_109604_, this.minecraft.crosshairPickEntity);
        this.level.pollLightUpdates();
        this.level.getChunkSource().getLightEngine().runLightUpdates();
        Vec3 vec3 = p_109604_.getPosition();
        double d0 = vec3.x();
        double d1 = vec3.y();
        double d2 = vec3.z();
        Matrix4f matrix4f = p_109600_.last().pose();
        boolean flag = this.capturedFrustum != null;
        Frustum frustum;
        if (flag) {
            frustum = this.capturedFrustum;
            frustum.prepare(this.frustumPos.x, this.frustumPos.y, this.frustumPos.z);
        } else {
            frustum = this.cullingFrustum;
        }

        if (this.captureFrustum) {
            this.captureFrustum(matrix4f, p_254120_, vec3.x, vec3.y, vec3.z, flag ? new Frustum(matrix4f, p_254120_) : frustum);
            this.captureFrustum = false;
        }

        FogRenderer.setupColor(p_109604_, p_109601_, this.minecraft.level, this.minecraft.options.getEffectiveRenderDistance(), p_109605_.getDarkenWorldAmount(p_109601_));
        FogRenderer.levelFogColor();
        RenderSystem.clear(16640, Minecraft.ON_OSX);
        float f = p_109605_.getRenderDistance();
        boolean flag1 = this.minecraft.level.effects().isFoggyAt(Mth.floor(d0), Mth.floor(d1)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
        FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_SKY, f, flag1, p_109601_);
        RenderSystem.setShader(GameRenderer::getPositionShader);
        this.renderSky(p_109600_, p_254120_, p_109601_, p_109604_, flag1, () -> {
            FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_SKY, f, flag1, p_109601_);
        });
        FogRenderer.setupFog(p_109604_, FogRenderer.FogMode.FOG_TERRAIN, Math.max(f, 32.0F), flag1, p_109601_);
        this.setupRender(p_109604_, frustum, flag, this.minecraft.player.isSpectator());
        this.compileChunks(p_109604_);
        this.renderChunkLayer(RenderType.solid(), p_109600_, d0, d1, d2, p_254120_);
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).setBlurMipmap(false, this.minecraft.options.mipmapLevels().get() > 0);
        this.renderChunkLayer(RenderType.cutoutMipped(), p_109600_, d0, d1, d2, p_254120_);
        this.minecraft.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).restoreLastBlurMipmap();
        this.renderChunkLayer(RenderType.cutout(), p_109600_, d0, d1, d2, p_254120_);
        if (this.level.effects().constantAmbientLight()) {
            Lighting.setupNetherLevel(p_109600_.last().pose());
        } else {
            Lighting.setupLevel(p_109600_.last().pose());
        }

        this.renderedEntities = 0;
        this.culledEntities = 0;
        if (this.itemEntityTarget != null) {
            this.itemEntityTarget.clear(Minecraft.ON_OSX);
            this.itemEntityTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }

        if (this.weatherTarget != null) {
            this.weatherTarget.clear(Minecraft.ON_OSX);
        }

        if (this.shouldShowEntityOutlines()) {
            this.entityTarget.clear(Minecraft.ON_OSX);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }

        boolean flag2 = false;
        MultiBufferSource.BufferSource multibuffersource$buffersource = this.renderBuffers.bufferSource();

        for (Entity entity : this.level.entitiesForRendering()) {
            if (this.entityRenderDispatcher.shouldRender(entity, frustum, d0, d1, d2) || entity.hasIndirectPassenger(this.minecraft.player)) {
                BlockPos blockpos = entity.blockPosition();
                if ((this.level.isOutsideBuildHeight(blockpos.getY()) || this.isChunkCompiled(blockpos)) && (entity != p_109604_.getEntity() || p_109604_.isDetached() || p_109604_.getEntity() instanceof LivingEntity && ((LivingEntity) p_109604_.getEntity()).isSleeping()) && (!(entity instanceof LocalPlayer) || p_109604_.getEntity() == entity || (entity == minecraft.player && !minecraft.player.isSpectator()))) {
                    ++this.renderedEntities;
                    if (entity.tickCount == 0) {
                        entity.xOld = entity.getX();
                        entity.yOld = entity.getY();
                        entity.zOld = entity.getZ();
                    }

                    MultiBufferSource multibuffersource;
                    if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(entity)) {
                        flag2 = true;
                        OutlineBufferSource outlinebuffersource = this.renderBuffers.outlineBufferSource();
                        multibuffersource = outlinebuffersource;
                        int i = entity.getTeamColor();
                        outlinebuffersource.setColor(FastColor.ARGB32.red(i), FastColor.ARGB32.green(i), FastColor.ARGB32.blue(i), 255);
                    } else {
                        if (this.shouldShowEntityOutlines() && entity.hasCustomOutlineRendering(this.minecraft.player)) {
                            flag2 = true;
                        }
                        multibuffersource = multibuffersource$buffersource;
                    }

                    this.renderEntity(entity, d0, d1, d2, p_109601_, p_109600_, multibuffersource);
                }
            }
        }

        multibuffersource$buffersource.endLastBatch();
        this.checkPoseStack(p_109600_);
        multibuffersource$buffersource.endBatch(RenderType.entitySolid(TextureAtlas.LOCATION_BLOCKS));
        multibuffersource$buffersource.endBatch(RenderType.entityCutout(TextureAtlas.LOCATION_BLOCKS));
        multibuffersource$buffersource.endBatch(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));
        multibuffersource$buffersource.endBatch(RenderType.entitySmoothCutout(TextureAtlas.LOCATION_BLOCKS));

        for (LevelRenderer.RenderChunkInfo levelrenderer$renderchunkinfo : this.renderChunksInFrustum) {
            List<BlockEntity> list = levelrenderer$renderchunkinfo.chunk.getCompiledChunk().getRenderableBlockEntities();
            if (!list.isEmpty()) {
                for (BlockEntity blockentity1 : list) {
                    if (!frustum.isVisible(blockentity1.getRenderBoundingBox())) continue;
                    BlockPos blockpos4 = blockentity1.getBlockPos();
                    MultiBufferSource multibuffersource1 = multibuffersource$buffersource;
                    p_109600_.pushPose();
                    p_109600_.translate((double) blockpos4.getX() - d0, (double) blockpos4.getY() - d1, (double) blockpos4.getZ() - d2);
                    SortedSet<BlockDestructionProgress> sortedset = this.destructionProgress.get(blockpos4.asLong());
                    if (sortedset != null && !sortedset.isEmpty()) {
                        int j = sortedset.last().getProgress();
                        if (j >= 0) {
                            PoseStack.Pose posestack$pose = p_109600_.last();
                            VertexConsumer vertexconsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(j)), posestack$pose.pose(), posestack$pose.normal(), 1.0F);
                            multibuffersource1 = (p_234298_) -> {
                                VertexConsumer vertexconsumer3 = multibuffersource$buffersource.getBuffer(p_234298_);
                                return p_234298_.affectsCrumbling() ? VertexMultiConsumer.create(vertexconsumer, vertexconsumer3) : vertexconsumer3;
                            };
                        }
                    }
                    if (this.shouldShowEntityOutlines() && blockentity1.hasCustomOutlineRendering(this.minecraft.player)) {
                        flag2 = true;
                    }

                    this.blockEntityRenderDispatcher.render(blockentity1, p_109601_, p_109600_, multibuffersource1);
                    p_109600_.popPose();
                }
            }
        }

        synchronized (this.globalBlockEntities) {
            for (BlockEntity blockentity : this.globalBlockEntities) {
                if (!frustum.isVisible(blockentity.getRenderBoundingBox())) continue;
                BlockPos blockpos3 = blockentity.getBlockPos();
                p_109600_.pushPose();
                p_109600_.translate((double) blockpos3.getX() - d0, (double) blockpos3.getY() - d1, (double) blockpos3.getZ() - d2);
                if (this.shouldShowEntityOutlines() && blockentity.hasCustomOutlineRendering(this.minecraft.player)) {
                    flag2 = true;
                }
                this.blockEntityRenderDispatcher.render(blockentity, p_109601_, p_109600_, multibuffersource$buffersource);
                p_109600_.popPose();
            }
        }

        this.checkPoseStack(p_109600_);
        multibuffersource$buffersource.endBatch(RenderType.solid());
        multibuffersource$buffersource.endBatch(RenderType.endPortal());
        multibuffersource$buffersource.endBatch(RenderType.endGateway());
        multibuffersource$buffersource.endBatch(Sheets.solidBlockSheet());
        multibuffersource$buffersource.endBatch(Sheets.cutoutBlockSheet());
        multibuffersource$buffersource.endBatch(Sheets.bedSheet());
        multibuffersource$buffersource.endBatch(Sheets.shulkerBoxSheet());
        multibuffersource$buffersource.endBatch(Sheets.signSheet());
        multibuffersource$buffersource.endBatch(Sheets.hangingSignSheet());
        multibuffersource$buffersource.endBatch(Sheets.chestSheet());
        this.renderBuffers.outlineBufferSource().endOutlineBatch();
        if (flag2) {
            this.entityEffect.process(p_109601_);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }

        for (Long2ObjectMap.Entry<SortedSet<BlockDestructionProgress>> entry : this.destructionProgress.long2ObjectEntrySet()) {
            BlockPos blockpos2 = BlockPos.of(entry.getLongKey());
            double d3 = (double) blockpos2.getX() - d0;
            double d4 = (double) blockpos2.getY() - d1;
            double d5 = (double) blockpos2.getZ() - d2;
            if (!(d3 * d3 + d4 * d4 + d5 * d5 > 1024.0D)) {
                SortedSet<BlockDestructionProgress> sortedset1 = entry.getValue();
                if (sortedset1 != null && !sortedset1.isEmpty()) {
                    int k = sortedset1.last().getProgress();
                    p_109600_.pushPose();
                    p_109600_.translate((double) blockpos2.getX() - d0, (double) blockpos2.getY() - d1, (double) blockpos2.getZ() - d2);
                    PoseStack.Pose posestack$pose1 = p_109600_.last();
                    VertexConsumer vertexconsumer1 = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(k)), posestack$pose1.pose(), posestack$pose1.normal(), 1.0F);
                    net.minecraftforge.client.model.data.ModelData modelData = level.getModelDataManager().getAt(blockpos2);
                    this.minecraft.getBlockRenderer().renderBreakingTexture(this.level.getBlockState(blockpos2), blockpos2, this.level, p_109600_, vertexconsumer1, modelData == null ? net.minecraftforge.client.model.data.ModelData.EMPTY : modelData);
                    p_109600_.popPose();
                }
            }
        }

        this.checkPoseStack(p_109600_);
        HitResult hitresult = this.minecraft.hitResult;
        if (p_109603_ && hitresult != null && hitresult.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos1 = ((BlockHitResult) hitresult).getBlockPos();
            BlockState blockstate = this.level.getBlockState(blockpos1);
            if (!blockstate.isAir() && this.level.getWorldBorder().isWithinBounds(blockpos1)) {
                VertexConsumer vertexconsumer2 = multibuffersource$buffersource.getBuffer(RenderType.lines());
                this.renderHitOutline(p_109600_, vertexconsumer2, p_109604_.getEntity(), d0, d1, d2, blockpos1, blockstate);
            }
        }

        this.minecraft.debugRenderer.render(p_109600_, multibuffersource$buffersource, d0, d1, d2);
        multibuffersource$buffersource.endLastBatch();
        PoseStack posestack = RenderSystem.getModelViewStack();
        RenderSystem.applyModelViewMatrix();
        multibuffersource$buffersource.endBatch(Sheets.translucentCullBlockSheet());
        multibuffersource$buffersource.endBatch(Sheets.bannerSheet());
        multibuffersource$buffersource.endBatch(Sheets.shieldSheet());
        multibuffersource$buffersource.endBatch(RenderType.armorGlint());
        multibuffersource$buffersource.endBatch(RenderType.armorEntityGlint());
        multibuffersource$buffersource.endBatch(RenderType.glint());
        multibuffersource$buffersource.endBatch(RenderType.glintDirect());
        multibuffersource$buffersource.endBatch(RenderType.glintTranslucent());
        multibuffersource$buffersource.endBatch(RenderType.entityGlint());
        multibuffersource$buffersource.endBatch(RenderType.entityGlintDirect());
        multibuffersource$buffersource.endBatch(RenderType.waterMask());
        this.renderBuffers.crumblingBufferSource().endBatch();
        if (this.transparencyChain != null) {
            multibuffersource$buffersource.endBatch(RenderType.lines());
            multibuffersource$buffersource.endBatch();
            assert this.translucentTarget != null;
            RenderTargetHelper.clear(this.translucentTarget);
            RenderTargetHelper.copyDepthFrom(this.translucentTarget, this.minecraft.mainRenderTarget);
            this.renderChunkLayer(RenderType.translucent(), p_109600_, d0, d1, d2, p_254120_);
            this.renderChunkLayer(RenderType.tripwire(), p_109600_, d0, d1, d2, p_254120_);
            this.particlesTarget.clear(Minecraft.ON_OSX);
            this.particlesTarget.copyDepthFrom(this.minecraft.getMainRenderTarget());
            RenderStateShard.PARTICLES_TARGET.setupRenderState();
            this.minecraft.particleEngine.render(p_109600_, multibuffersource$buffersource, p_109606_, p_109604_, p_109601_, frustum);
            RenderStateShard.PARTICLES_TARGET.clearRenderState();
        } else {
            if (this.translucentTarget != null) {
                RenderTargetHelper.clear(this.translucentTarget);
            }

            this.renderChunkLayer(RenderType.translucent(), p_109600_, d0, d1, d2, p_254120_);
            multibuffersource$buffersource.endBatch(RenderType.lines());
            multibuffersource$buffersource.endBatch();
            this.renderChunkLayer(RenderType.tripwire(), p_109600_, d0, d1, d2, p_254120_);
            this.minecraft.particleEngine.render(p_109600_, multibuffersource$buffersource, p_109606_, p_109604_, p_109601_, frustum);
        }
        PoseStackHelper.pushPose(posestack);
        posestack.mulPoseMatrix(p_109600_.last().pose());
        RenderSystem.applyModelViewMatrix();
        if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
            if (this.transparencyChain != null) {
                RenderTargetHelper.clear(this.cloudsTarget);
                RenderStateShard.CLOUDS_TARGET.setupRenderState();
                this.renderClouds(p_109600_, p_254120_, p_109601_, d0, d1, d2);
                RenderStateShard.CLOUDS_TARGET.clearRenderState();
            } else {
                RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
                this.renderClouds(p_109600_, p_254120_, p_109601_, d0, d1, d2);
            }
        }

        if (this.transparencyChain != null) {
            RenderStateShard.WEATHER_TARGET.setupRenderState();
            this.renderSnowAndRain(p_109606_, p_109601_, d0, d1, d2);
            this.renderWorldBorder(p_109604_);
            RenderStateShard.WEATHER_TARGET.clearRenderState();
            this.transparencyChain.process(p_109601_);
            this.minecraft.getMainRenderTarget().bindWrite(false);
        } else {
            RenderSystem.depthMask(false);
            this.renderSnowAndRain(p_109606_, p_109601_, d0, d1, d2);
            this.renderWorldBorder(p_109604_);
            RenderSystem.depthMask(true);
        }

        PoseStackHelper.popPose(posestack);
        RenderSystem.applyModelViewMatrix();
        multibuffersource$buffersource.endLastBatch();
        GlStateManagerHelper._depthMask(true);
        GlStateManagerHelper._disableBlend();
        RenderSystem.shaderFogStart = Float.MAX_VALUE;
    }
}
