package com.nobtg.realgod.mixins;

import com.nobtg.realgod.utils.client.SuperRender;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Inject(method = "allChanged", at = @At("HEAD"), cancellable = true)
    private void allChanged(CallbackInfo ci) {
        if (SuperRender.isSuperMode)
            ci.cancel();
    }
}
