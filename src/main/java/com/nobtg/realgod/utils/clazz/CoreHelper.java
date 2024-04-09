package com.nobtg.realgod.utils.clazz;

import com.mojang.blaze3d.platform.InputConstants;
import com.nobtg.realgod.utils.client.KeyMappingHelper;
import com.nobtg.realgod.utils.client.SmoothDoubleHelper;
import com.nobtg.realgod.utils.client.SuperRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import org.lwjgl.glfw.*;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

import javax.annotation.Nullable;

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
        return JNI.invokePPP(Minecraft.instance.window.window, MemoryUtil.memAddressSafe((GLFWCursorPosCallbackI) (p_91562_, p_91563_, p_91564_) -> {
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
        }), GLFW.Functions.SetCursorPosCallback);
    }

    public static GLFWMouseButtonCallback glfwSetMouseButtonCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWmousebuttonfun") GLFWMouseButtonCallbackI cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.glfwSetMouseButtonCallback(window, cbfun);
        } else {
            return GLFWMouseButtonCallback.createSafe(nativeSetMouseButtonCallback());
        }
    }

    public static long nglfwSetMouseButtonCallback(long window, long cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.nglfwSetMouseButtonCallback(window, cbfun);
        } else {
            return nativeSetMouseButtonCallback();
        }
    }

    private static long nativeSetMouseButtonCallback() {
        return JNI.invokePPP(Minecraft.instance.window.window, MemoryUtil.memAddressSafe((GLFWMouseButtonCallbackI) (p_91531_, p_91532_, p_91533_, p_91534_) -> {
            Minecraft mc = Minecraft.instance;
            if (p_91531_ == mc.window.window) {
                boolean flag = p_91533_ == 1;
                if (Minecraft.ON_OSX && p_91532_ == 0) {
                    if (flag) {
                        if ((p_91534_ & 2) == 2) {
                            p_91532_ = 1;
                            ++mc.mouseHandler.fakeRightMouse;
                        }
                    } else if (mc.mouseHandler.fakeRightMouse > 0) {
                        p_91532_ = 1;
                        --mc.mouseHandler.fakeRightMouse;
                    }
                }

                int i = p_91532_;
                if (flag) {
                    if (mc.options.touchscreen.value && mc.mouseHandler.clickDepth++ > 0) {
                        return;
                    }

                    mc.mouseHandler.activeButton = i;
                    mc.mouseHandler.mousePressedTime = JNI.invokeD(GLFW.Functions.GetTime);
                } else if (mc.mouseHandler.activeButton != -1) {
                    if (mc.options.touchscreen.value && --mc.mouseHandler.clickDepth > 0) {
                        return;
                    }

                    mc.mouseHandler.activeButton = -1;
                }

                if (i == 0) {
                    mc.mouseHandler.isLeftPressed = flag;
                } else if (i == 2) {
                    mc.mouseHandler.isMiddlePressed = flag;
                } else if (i == 1) {
                    mc.mouseHandler.isRightPressed = flag;
                }

                KeyMappingHelper.set(InputConstants.Type.MOUSE.getOrCreate(i), flag);
                if (flag) {
                    if (mc.player.isSpectator() && i == 2) {
                        mc.gui.spectatorGui.lastSelectionTime = System.nanoTime();
                        if (mc.gui.spectatorGui.menu != null) {
                            int i2 = mc.gui.spectatorGui.menu.selectedSlot;
                            if (i2 != -1) {
                                mc.gui.spectatorGui.menu.selectSlot(i2);
                            }
                        } else {
                            mc.gui.spectatorGui.menu = new SpectatorMenu(mc.gui.spectatorGui);
                        }
                    } else {
                        KeyMappingHelper.click(InputConstants.Type.MOUSE.getOrCreate(i));
                    }
                }
            }
        }), GLFW.Functions.SetMouseButtonCallback);
    }

    public static GLFWScrollCallback glfwSetScrollCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWscrollfun") GLFWScrollCallbackI cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.glfwSetScrollCallback(window, cbfun);
        } else {
            return GLFWScrollCallback.createSafe(nativeSetScrollCallback());
        }
    }

    public static long nglfwSetScrollCallback(long window, long cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.nglfwSetScrollCallback(window, cbfun);
        } else {
            return nativeSetScrollCallback();
        }
    }

    private static long nativeSetScrollCallback() {
        return JNI.invokePPP(Minecraft.instance.window.window, MemoryUtil.memAddressSafe((GLFWScrollCallbackI) (p_91527_, p_91528_, p_91529_) -> {
            Minecraft mc = Minecraft.instance;
            if (p_91527_ == mc.window.window) {
                double offset = p_91529_;
                if (Minecraft.ON_OSX && p_91529_ == 0) {
                    offset = p_91528_;
                }
                double d0 = (mc.options.discreteMouseScroll.value ? Math.signum(offset) : offset) * mc.options.mouseWheelSensitivity.value;
                if (mc.overlay == null) {
                    if (mc.player != null) {
                        if (mc.mouseHandler.accumulatedScroll != 0.0D && Math.signum(d0) != Math.signum(mc.mouseHandler.accumulatedScroll)) {
                            mc.mouseHandler.accumulatedScroll = 0.0D;
                        }

                        mc.mouseHandler.accumulatedScroll += d0;
                        int i = (int) mc.mouseHandler.accumulatedScroll;
                        if (i == 0) {
                            return;
                        }

                        mc.mouseHandler.accumulatedScroll -= i;
                        if (mc.player.isSpectator()) {
                            if (mc.gui.spectatorGui.menu != null) {
                                int i3;
                                for (i3 = mc.gui.spectatorGui.menu.selectedSlot - i; i3 >= 0 && i3 <= 8 && (mc.gui.spectatorGui.menu.getItem(i3) == SpectatorMenu.EMPTY_SLOT || !mc.gui.spectatorGui.menu.getItem(i3).isEnabled()); i3 -= i) {
                                }

                                if (i3 >= 0 && i3 <= 8) {
                                    mc.gui.spectatorGui.menu.selectSlot(i3);
                                    mc.gui.spectatorGui.lastSelectionTime = System.nanoTime();
                                }
                            } else {
                                float p_14037_ = mc.player.abilities.flyingSpeed + (float) i * 0.005F;
                                mc.player.abilities.flyingSpeed = p_14037_ < 0.0F ? 0.0F : Math.min(p_14037_, 0.2F);
                            }
                        } else {
                            int i0 = (int) Math.signum(i);

                            for (mc.player.inventory.selected -= i0; mc.player.inventory.selected < 0; mc.player.inventory.selected += 9) {
                            }

                            while (mc.player.inventory.selected >= 9) {
                                mc.player.inventory.selected -= 9;
                            }
                        }
                    }
                }
            }
        }), GLFW.Functions.SetScrollCallback);
    }

    public static GLFWDropCallback glfwSetDropCallback(@NativeType("GLFWwindow *") long window, @Nullable @NativeType("GLFWdropfun") GLFWDropCallbackI cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.glfwSetDropCallback(window, cbfun);
        } else {
            return GLFWDropCallback.createSafe(nativeSetDropCallback());
        }
    }

    public static long nglfwSetDropCallback(long window, long cbfun) {
        if (!SuperRender.isSuperMode) {
            return GLFW.nglfwSetDropCallback(window, cbfun);
        } else {
            return nativeSetDropCallback();
        }
    }

    private static long nativeSetDropCallback() {
        return JNI.invokePPP(Minecraft.instance.window.window, MemoryUtil.memAddressSafe((GLFWDropCallbackI) (window, count, names) -> {}), GLFW.Functions.SetDropCallback);
    }
}