package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import com.nobtg.realgod.items.TestItem;
import com.nobtg.realgod.utils.client.AvaritiaShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    private static boolean hasAnimatedTexture(ItemStack p_286353_) {
        return true;
    }

    @Shadow
    public static VertexConsumer getFoilBufferDirect(MultiBufferSource p_115223_, RenderType p_115224_, boolean p_115225_, boolean p_115226_) {
        return null;
    }

    @Shadow
    public abstract void renderModelLists(BakedModel p_115190_, ItemStack p_115191_, int p_115192_, int p_115193_, PoseStack p_115194_, VertexConsumer p_115195_);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void render(ItemStack p_115144_, ItemDisplayContext p_270188_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo ci) {
        if (!p_115144_.isEmpty() && p_115144_.getItem() instanceof TestItem) {
            p_115147_.pushPose();
            p_115151_ = net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(p_115147_, p_115151_, p_270188_, p_115146_);
            p_115147_.translate(-0.5F, -0.5F, -0.5F);
            boolean flag1 = true;
            for (var model : p_115151_.getRenderPasses(p_115144_, flag1)) {
                for (var rendertype : model.getRenderTypes(p_115144_, flag1)) {
                    VertexConsumer vertexconsumer;
                    if (hasAnimatedTexture(p_115144_) && p_115144_.hasFoil()) {
                        p_115147_.pushPose();
                        PoseStack.Pose posestack$pose = p_115147_.last();
                        if (p_270188_ == ItemDisplayContext.GUI) {
                            MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
                        } else if (p_270188_.firstPerson()) {
                            MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
                        }

                        if (p_115148_ instanceof MultiBufferSource.BufferSource bs) {
                            bs.endBatch();
                        }

                        float yaw = 0.0F;
                        float pitch = 0.0F;
                        float scale = 1.0F;
                        if (p_270188_ != ItemDisplayContext.GUI) {
                            Minecraft mc = Minecraft.getInstance();
                            yaw = (float) ((double) (mc.player.getYRot() * 2.0F) * Math.PI / 360.0D);
                            pitch = -((float) ((double) (mc.player.getXRot() * 2.0F) * Math.PI / 360.0D));
                        } else {
                            scale = 25.0F;
                        }

                        if (AvaritiaShaders.cosmicOpacity != null) {
                            AvaritiaShaders.cosmicOpacity.set(1.0F);
                        }

                        AvaritiaShaders.cosmicYaw.set(yaw);
                        AvaritiaShaders.cosmicPitch.set(pitch);
                        AvaritiaShaders.cosmicExternalScale.set(scale);

                        vertexconsumer = VertexMultiConsumer.create(new SheetedDecalTextureGenerator(p_115148_.getBuffer(RenderType.glintDirect()), posestack$pose.pose(), posestack$pose.normal(), ItemInHandRenderer.MAP_FINAL_SCALE), p_115148_.getBuffer(AvaritiaShaders.COSMIC_RENDER_TYPE));

                        p_115147_.popPose();
                    } else {
                        vertexconsumer = getFoilBufferDirect(p_115148_, rendertype, true, p_115144_.hasFoil());
                    }
                    this.renderModelLists(model, p_115144_, p_115149_, p_115150_, p_115147_, vertexconsumer);
                }
            }
            p_115147_.popPose();

            ci.cancel();
        }
    }
}
