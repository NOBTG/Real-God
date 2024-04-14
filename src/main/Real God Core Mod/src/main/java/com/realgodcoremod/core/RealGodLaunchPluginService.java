package com.realgodcoremod.core;

import cpw.mods.modlauncher.TransformingClassLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;

public final class RealGodLaunchPluginService implements ILaunchPluginService {
    private static final Method buildTransformedClassNodeFor;

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
                    } else if (isAssignableFrom(methodInsn.owner, "net/minecraft/world/entity/LivingEntity")) {
                        if ((methodInsn.name.equals("getHealth") || methodInsn.name.equals("m_21223_")) && methodInsn.desc.equals("()F")) {
                            apply(methodInsn, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F");
                            modify = true;
                        } else if ((methodInsn.name.equals("isDeadOrDying") || methodInsn.name.equals("m_21224_")) && methodInsn.desc.equals("()Z")) {
                            apply(methodInsn, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                            modify = true;
                        } else if ((methodInsn.name.equals("isAlive") || methodInsn.name.equals("m_6084_")) && methodInsn.desc.equals("()Z")) {
                            apply(methodInsn, "isAlive", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                            modify = true;
                        }
                    }
                } else if (insn instanceof FieldInsnNode fieldInsn) {
                    if (fieldInsn.getOpcode() == Opcodes.GETFIELD) {
                        if (isAssignableFrom(fieldInsn.owner, "net/minecraft/client/MouseHandler")) {
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

    private static boolean isAssignableFrom(String current, String father) {
        ClassReader classReader = new ClassReader(getByte(current));

        while (true) {
            String currentName = classReader.getSuperName();
            if (currentName.equals(father)) {
                return true;
            } else if (currentName.equals("java/lang/Object")) {
                return false;
            }

            classReader = new ClassReader(getByte(currentName));
        }
    }

    private static byte[] getByte(String name) {
        try {
            return (byte[]) buildTransformedClassNodeFor.invoke(Thread.currentThread().getContextClassLoader(), name.replace('/', '.'), "computing_frames");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            buildTransformedClassNodeFor = TransformingClassLoader.class.getDeclaredMethod("buildTransformedClassNodeFor", String.class, String.class);
            buildTransformedClassNodeFor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
