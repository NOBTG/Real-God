package com.realgodcoremod.core;

import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.EnumSet;

public final class RealGodLaunchPluginService implements ILaunchPluginService {
    @Override
    public String name() {
        return "RealGodILaunchPluginService";
    }

    @Override
    public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty) {
        return EnumSet.of(Phase.AFTER);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        if (reason.equals("classloading")) {
            boolean modify = false;

            if (classNode.name.equals("com/nobtg/realgod/utils/clazz/CoreHelper")) return false;

            for (MethodNode method : classNode.methods) {
                for (AbstractInsnNode insn : method.instructions) {
                    if (insn instanceof MethodInsnNode methodInsn) {
                        if (methodInsn.owner.equals("org/lwjgl/system/JNI")) {
                            if (methodInsn.name.equals("invokePV") && methodInsn.desc.equals("(JIIJ)V")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("invokePV") && methodInsn.desc.equals("(JJ)V")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("invokePPP") && methodInsn.desc.equals("(JJJ)J")) {
                                apply(methodInsn);
                                modify = true;
                            }
                        } else if (methodInsn.owner.equals("org/lwjgl/glfw/GLFW")) {
                            if (methodInsn.name.equals("glfwSwapBuffers") && methodInsn.desc.equals("(J)V")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("glfwSetInputMode") && methodInsn.desc.equals("(JII)V")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("glfwSetCursorPosCallback") && methodInsn.desc.equals("(JLorg/lwjgl/glfw/GLFWCursorPosCallbackI;)Lorg/lwjgl/glfw/GLFWCursorPosCallback;")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("nglfwSetCursorPosCallback") && methodInsn.desc.equals("(JJ)J")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("glfwSetMouseButtonCallback") && methodInsn.desc.equals("(JLorg/lwjgl/glfw/GLFWMouseButtonCallbackI;)Lorg/lwjgl/glfw/GLFWMouseButtonCallback;")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("nglfwSetMouseButtonCallback") && methodInsn.desc.equals("(JJ)J")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("glfwSetScrollCallback") && methodInsn.desc.equals("(JLorg/lwjgl/glfw/GLFWScrollCallbackI;)Lorg/lwjgl/glfw/GLFWScrollCallback;")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("nglfwSetScrollCallback") && methodInsn.desc.equals("(JJ)J")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("glfwSetDropCallback") && methodInsn.desc.equals("(JLorg/lwjgl/glfw/GLFWDropCallbackI;)Lorg/lwjgl/glfw/GLFWDropCallback;")) {
                                apply(methodInsn);
                                modify = true;
                            } else if (methodInsn.name.equals("nglfwSetDropCallback") && methodInsn.desc.equals("(JJ)J")) {
                                apply(methodInsn);
                                modify = true;
                            }
                        }
                    } else if (insn instanceof FieldInsnNode fieldInsn) {
                        if (fieldInsn.getOpcode() == Opcodes.GETFIELD) {
                            if (fieldInsn.owner.equals("net/minecraft/client/MouseHandler")) {
                                if (fieldInsn.name.equals("mouseGrabbed") || fieldInsn.name.equals("f_91520_")) {
                                    apply(fieldInsn, method.instructions, "(Lnet/minecraft/client/MouseHandler;)Z", "mouseGrabbed");
                                    modify = true;
                                }
                            }
                        }
                    }
                }
            }
            return modify;
        } else return false;
    }

    public static void apply(Object... o) {
        if (o[0] instanceof MethodInsnNode methodInsn) {
            methodInsn.owner = "com/nobtg/realgod/utils/clazz/CoreHelper";
        } else if (o[0] instanceof FieldInsnNode fieldInsn) {
            String name = o.length == 4 ? (String) o[3] : fieldInsn.name;
            ((InsnList) o[1]).set(fieldInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/nobtg/realgod/utils/clazz/CoreHelper", name, (String) o[2], false));
        }
    }
}
