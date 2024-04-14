package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.platform.GlDebug;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GlDebug.class)
public final class GlDebugMixin {
    @Redirect(method = "printDebugLog", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"))
    private static void printDebugLog(Logger instance, String s, Object o) {
        instance.debug(s, o);
    }
}
