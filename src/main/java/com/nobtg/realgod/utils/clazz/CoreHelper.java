package com.nobtg.realgod.utils.clazz;

import com.mojang.blaze3d.platform.InputConstants;
import com.nobtg.realgod.utils.client.SmoothDoubleHelper;
import com.nobtg.realgod.utils.client.SuperRender;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.system.JNI;
import org.lwjgl.system.NativeType;

import javax.annotation.Nullable;

import static org.lwjgl.system.MemoryUtil.memAddressSafe;

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
        KeyMapping.releaseAll();
        return SuperRender.isSuperMode || mouseHandler.mouseGrabbed;
    }

    public static GLFWCursorPosCallback glfwSetCursorPosCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWcursorposfun") GLFWCursorPosCallbackI cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.glfwSetCursorPosCallback(window, cbfun);
        } else {
            return GLFWCursorPosCallback.createSafe(nativeSetCursorPosCallback());
        }
    }

    public static long nglfwSetCursorPosCallback(long window, long cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.nglfwSetCursorPosCallback(window, cbfun);
        } else {
            return nativeSetCursorPosCallback();
        }
    }

    private static long nativeSetCursorPosCallback() {
        return JNI.invokePPP(Minecraft.instance.window.window, memAddressSafe(new GLFWCursorPosCallback() {
            @Override
            public void invoke(long p_91562_, double p_91563_, double p_91564_) {
                Minecraft mc = Minecraft.instance;
                if (p_91562_ == mc.window.window) {
                    if (mc.mouseHandler.ignoreFirstMove) {
                        mc.mouseHandler.xpos = p_91563_;
                        mc.mouseHandler.ypos = p_91564_;
                        mc.mouseHandler.ignoreFirstMove = false;
                    }

                    if (mc.mouseHandler.mouseGrabbed && mc.windowActive) {
                        mc.mouseHandler.accumulatedDX += p_91563_ - mc.mouseHandler.xpos;
                        mc.mouseHandler.accumulatedDY += p_91564_ - mc.mouseHandler.ypos;
                    }

                    double d0 = JNI.invokeD(GLFW.Functions.GetTime);
                    double d1 = d0 - mc.mouseHandler.lastMouseEventTime;
                    mc.mouseHandler.lastMouseEventTime = d0;
                    if (mc.mouseHandler.mouseGrabbed && mc.windowActive) {
                        double d4 = mc.options.sensitivity.value * 0.6000000238418579 + 0.20000000298023224;
                        double d5 = d4 * d4 * d4;
                        double d6 = d5 * 8.0;
                        double d2;
                        double d3;
                        if (mc.options.smoothCamera) {
                            double d7 = SmoothDoubleHelper.getNewDeltaValue(mc.mouseHandler.smoothTurnX, mc.mouseHandler.accumulatedDX * d6, d1 * d6);
                            double d8 = SmoothDoubleHelper.getNewDeltaValue(mc.mouseHandler.smoothTurnY, mc.mouseHandler.accumulatedDY * d6, d1 * d6);
                            d2 = d7;
                            d3 = d8;
                        } else if (mc.options.cameraType.firstPerson && mc.player.isScoping()) {
                            SmoothDoubleHelper.reset(mc.mouseHandler.smoothTurnX);
                            SmoothDoubleHelper.reset(mc.mouseHandler.smoothTurnY);
                            d2 = mc.mouseHandler.accumulatedDX * d5;
                            d3 = mc.mouseHandler.accumulatedDY * d5;
                        } else {
                            SmoothDoubleHelper.reset(mc.mouseHandler.smoothTurnX);
                            SmoothDoubleHelper.reset(mc.mouseHandler.smoothTurnY);
                            d2 = mc.mouseHandler.accumulatedDX * d6;
                            d3 = mc.mouseHandler.accumulatedDY * d6;
                        }

                        mc.mouseHandler.accumulatedDX = 0.0;
                        mc.mouseHandler.accumulatedDY = 0.0;
                        int i = 1;
                        if (mc.options.invertYMouse.value) {
                            i = -1;
                        }

                        if (mc.player != null) {
                            float f = (float) (d3 * (double) i) * 0.15F;
                            float f1 = (float) d2 * 0.15F;
                            mc.player.xRot = mc.player.xRot + f;
                            mc.player.yRot = mc.player.yRot + f1;
                            mc.player.xRot = mc.player.xRot < -90.0F ? -90.0F : Math.min(mc.player.xRot, 90.0F);
                            mc.player.xRotO += f;
                            mc.player.yRotO += f1;
                            mc.player.xRotO = mc.player.xRotO < -90.0F ? -90.0F : Math.min(mc.player.xRotO, 90.0F);
                        }
                    } else {
                        mc.mouseHandler.accumulatedDX = 0.0;
                        mc.mouseHandler.accumulatedDY = 0.0;
                    }

                    mc.mouseHandler.xpos = p_91563_;
                    mc.mouseHandler.ypos = p_91564_;
                }
            }
        }), GLFW.Functions.SetCursorPosCallback);
        InputConstants
    }



}
