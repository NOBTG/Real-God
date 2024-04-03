package com.nobtg.realgod.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import com.nobtg.realgod.utils.client.SuperRender;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderSystem.class)
public abstract class RenderSystemMixin {
    @Redirect(method = "flipFrame", at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V"))
    private static void flipFrame(long window) {
        if (SuperRender.isSuperMode) {
            SuperRender.renderSuper();
        }
        GLFW.glfwSwapBuffers(window);
    }
}
