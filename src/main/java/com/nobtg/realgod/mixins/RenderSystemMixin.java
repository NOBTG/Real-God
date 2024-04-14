package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nobtg.realgod.utils.client.render.renderer.SuperRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public final class RenderSystemMixin {
    @Inject(method = "flipFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V"))
    private static void flipFrame(long p_69496_, CallbackInfo ci) {
        if (SuperRender.isSuperMode) {
            SuperRender.renderSuper();
        }
    }
}
