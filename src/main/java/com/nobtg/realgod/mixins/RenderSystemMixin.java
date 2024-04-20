package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RenderSystem.class)
public final class RenderSystemMixin {
//    @Inject(method = "flipFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V"))
//    private static void flipFrame(long p_69496_, CallbackInfo ci) {
//        if (SuperRender.isSuperMode) {
//            SuperRender.renderSuper();
//        }
//    }
}
