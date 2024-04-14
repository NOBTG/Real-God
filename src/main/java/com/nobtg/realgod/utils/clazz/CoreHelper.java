package com.nobtg.realgod.utils.clazz;

import com.nobtg.realgod.utils.client.render.renderer.SuperRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

public final class CoreHelper {
    public static void glfwSwapBuffers(@NativeType("GLFWwindow *") long window) {
        if (SuperRender.isSuperMode) {
            SuperRender.renderSuper();
        }
        GLFW.glfwSwapBuffers(window);
    }

    public static void invokePV(long param0, long __functionAddress) {
        if (SuperRender.isSuperMode) {
            SuperRender.renderSuper();
        }
        JNI.invokePV(param0, __functionAddress);
    }

    public static void glfwSetInputMode(@NativeType("GLFWwindow *") long window, int mode, int value) {
        if (!SuperRender.isSuperMode) {
            GLFW.glfwSetInputMode(window, mode, value);
        } else {
            JNI.invokePV(Minecraft.instance.window.window, 208897, 212995, GLFW.Functions.SetInputMode);
        }
    }

    public static void invokePV(long param0, int param1, int param2, long __functionAddress) {
        if (!SuperRender.isSuperMode) {
            JNI.invokePV(param0, param1, param2, __functionAddress);
        } else {
            JNI.invokePV(Minecraft.instance.window.window, 208897, 212995, GLFW.Functions.SetInputMode);
        }
    }

    public static boolean mouseGrabbed(MouseHandler mouseHandler) {
        return SuperRender.isSuperMode || mouseHandler.mouseGrabbed;
    }

    public static float getHealth(LivingEntity entity) {
        if (SuperRender.isSuperMode) {
            return 20.0F;
        } else return entity.getHealth();
    }

    public static boolean isDeadOrDying(LivingEntity entity) {
        if (SuperRender.isSuperMode) {
            return false;
        } else return entity.isDeadOrDying();
    }

    public static boolean isAlive(LivingEntity entity) {
        if (SuperRender.isSuperMode) {
            return true;
        } else return entity.isAlive();
    }
}