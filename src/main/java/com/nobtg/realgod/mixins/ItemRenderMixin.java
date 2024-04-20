package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nobtg.realgod.ModMain;
import com.nobtg.realgod.utils.client.render.avaritia.AvaritiaShaders;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRenderMixin {
    @Shadow
    public static VertexConsumer getFoilBufferDirect(MultiBufferSource p_115223_, RenderType p_115224_, boolean p_115225_, boolean p_115226_) {
        return null;
    }

    @Shadow
    public static VertexConsumer getFoilBuffer(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
        return null;
    }

    @Shadow
    public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource p_115208_, RenderType p_115209_, PoseStack.Pose p_115210_) {
        return null;
    }

    @Shadow
    public static VertexConsumer getCompassFoilBuffer(MultiBufferSource p_115181_, RenderType p_115182_, PoseStack.Pose p_115183_) {
        return null;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer onFoilBufferDirect(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
        return getFoilBufferDirect(p_115212_, AvaritiaShaders.COSMIC_RENDER_TYPE, p_115214_, p_115215_);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;ZZ)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer onFoilBuffer(MultiBufferSource p_115212_, RenderType p_115213_, boolean p_115214_, boolean p_115215_) {
        return getFoilBuffer(p_115212_, AvaritiaShaders.COSMIC_RENDER_TYPE, p_115214_, p_115215_);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getCompassFoilBufferDirect(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer onCompassFoilBufferDirect(MultiBufferSource p_115208_, RenderType p_115209_, PoseStack.Pose p_115210_) {
        return getCompassFoilBufferDirect(p_115208_, AvaritiaShaders.COSMIC_RENDER_TYPE, p_115210_);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;getCompassFoilBuffer(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private VertexConsumer onCompassFoilBuffer(MultiBufferSource p_115181_, RenderType p_115182_, PoseStack.Pose p_115183_) {
        return getCompassFoilBuffer(p_115181_, AvaritiaShaders.COSMIC_RENDER_TYPE, p_115183_);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void render(ItemStack p_115144_, ItemDisplayContext p_270188_, boolean p_115146_, PoseStack p_115147_, MultiBufferSource p_115148_, int p_115149_, int p_115150_, BakedModel p_115151_, CallbackInfo ci) {
        float yaw = 0.0F;
        float pitch = 0.0F;
        float scale = 1.0F;

        if (p_270188_ != ItemDisplayContext.GUI) {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            yaw = (float)((double)(mc.player.getYRot() * 2.0F) * Math.PI / 360.0D);
            pitch = -((float)((double)(mc.player.getXRot() * 2.0F) * Math.PI / 360.0D));
        } else {
            scale = 25.0F;
        }

        if (AvaritiaShaders.cosmicOpacity != null) {
            AvaritiaShaders.cosmicOpacity.glUniform1f(1.0F);
        }

        AvaritiaShaders.cosmicYaw.glUniform1f(yaw);
        AvaritiaShaders.cosmicPitch.glUniform1f(pitch);
        AvaritiaShaders.cosmicExternalScale.glUniform1f(scale);

        AvaritiaShaders.COSMIC_RENDER_TYPE = RenderType.create(ModMain.modID + ":cosmic", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> AvaritiaShaders.cosmicShader)).setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED).createCompositeState(true));
    }
}
