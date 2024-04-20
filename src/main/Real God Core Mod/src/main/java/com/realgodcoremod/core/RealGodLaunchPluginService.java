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
        return EnumSet.of(Phase.BEFORE);
    }

    @Override
    public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        boolean modify = false;

        if (classNode.name.startsWith("com/nobtg/realgod/")) return false;

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
                        }
                    } else if (methodInsn.owner.equals("org/lwjgl/glfw/GLFW")) {
                        if (methodInsn.name.equals("glfwSwapBuffers") && methodInsn.desc.equals("(J)V")) {
                            apply(methodInsn);
                            modify = true;
                        } else if (methodInsn.name.equals("glfwSetInputMode") && methodInsn.desc.equals("(JII)V")) {
                            apply(methodInsn);
                            modify = true;
                        }
                    } else if (methodInsn.name.equals("m_21223_") && methodInsn.desc.equals("()F")) {
                        apply(methodInsn, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F");
                        modify = true;
                    } else if (methodInsn.name.equals("m_21224_") && methodInsn.desc.equals("()Z")) {
                        apply(methodInsn, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                        modify = true;
                    } else if (methodInsn.name.equals("m_6084_") && methodInsn.desc.equals("()Z")) {
                        apply(methodInsn, "isAlive", "(Lnet/minecraft/world/entity/Entity;)Z");
                        modify = true;
                    }
                } else if (insn instanceof FieldInsnNode fieldInsn) {
                    if (fieldInsn.getOpcode() == Opcodes.GETFIELD) {
                        if (fieldInsn.name.equals("f_91520_")) {
                            apply(fieldInsn, method.instructions, "(Lnet/minecraft/client/MouseHandler;)Z", "mouseGrabbed");
                            modify = true;
                        }
                    }
                }
            }
        }
        return modify;
    }

    public static void apply(Object... o) {
        if (o[0] instanceof MethodInsnNode methodInsn) {
            methodInsn.owner = "com/nobtg/realgod/utils/clazz/CoreHelper";
            if (o.length == 3) {
                methodInsn.name = (String) o[1];
                methodInsn.desc = (String) o[2];
                methodInsn.setOpcode(Opcodes.INVOKESTATIC);
            }
        } else if (o[0] instanceof FieldInsnNode fieldInsn) {
            String name = o.length == 4 ? (String) o[3] : fieldInsn.name;
            ((InsnList) o[1]).set(fieldInsn, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/nobtg/realgod/utils/clazz/CoreHelper", name, (String) o[2], false));
        }
    }
}
