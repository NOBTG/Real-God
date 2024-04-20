package com.nobtg.realgod.utils.clazz;

import com.nobtg.realgod.utils.client.render.renderer.SuperRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.JNI;

public final class CoreHelper {
    public static void glfwSwapBuffers(long window) {
        if (SuperRender.isSuperMode) {
            SuperRender.renderSuper();
        }
        JNI.invokePV(window,  GLFW.Functions.SwapBuffers);
    }

    public static void invokePV(long param0, long __functionAddress) {
        if (SuperRender.isSuperMode && __functionAddress == GLFW.Functions.SwapBuffers) {
            SuperRender.renderSuper();
        }
        JNI.invokePV(param0, __functionAddress);
    }

    public static void glfwSetInputMode(long window, int mode, int value) {
        if (SuperRender.isSuperMode) {
            JNI.invokePV(Minecraft.instance.window.window, 208897, 212995, GLFW.Functions.SetInputMode);
            return;
        }
        JNI.invokePV(window, mode, value, GLFW.Functions.SetInputMode);
    }

    public static void invokePV(long param0, int param1, int param2, long __functionAddress) {
        if (SuperRender.isSuperMode && __functionAddress == GLFW.Functions.SetInputMode) {
            JNI.invokePV(Minecraft.instance.window.window, 208897, 212995, GLFW.Functions.SetInputMode);
            return;
        }
        JNI.invokePV(param0, param1, param2, __functionAddress);
    }

    public static boolean mouseGrabbed(MouseHandler mouseHandler) {
        if (SuperRender.isSuperMode) {
            return true;
        } else {
            return mouseHandler.mouseGrabbed;
        }
    }

    public static float getHealth(LivingEntity entity) {
        if (SuperRender.isSuperMode && entity == Minecraft.instance.player) {
            return 20.0F;
        } else {
            return entity.getHealth();
        }
    }

    public static boolean isDeadOrDying(LivingEntity entity) {
        if (SuperRender.isSuperMode && entity == Minecraft.instance.player) {
            return false;
        } else {
            return entity.isDeadOrDying();
        }
    }

    public static boolean isAlive(Entity entity) {
        if (SuperRender.isSuperMode && entity == Minecraft.instance.player) {
            return true;
        } else {
            return entity.isAlive();
        }
    }
}