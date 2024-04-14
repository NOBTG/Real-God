package com.nobtg.realgod.mixins;

import com.nobtg.realgod.utils.client.render.renderer.SuperRender;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyMapping.class)
public final class KeyMappingMixin {
    @Inject(method = "release", at = @At("HEAD"), cancellable = true)
    private void release(CallbackInfo ci) {
        if (SuperRender.isSuperMode) {
            ci.cancel();
        }
    }
}
