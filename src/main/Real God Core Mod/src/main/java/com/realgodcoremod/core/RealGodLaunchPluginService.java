package com.realgodcoremod.core;

import cpw.mods.cl.ModuleClassLoader;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import sun.misc.Unsafe;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class RealGodLaunchPluginService implements ILaunchPluginService {
    private static final VarHandle packageLookup;
    private static final VarHandle parentLoaders;
    private static final MethodHandle getClassBytes;
    private static final MethodHandle classNameToModuleName;
    private static final MethodHandle loadFromModule;
    private static final ModuleClassLoader targetClassLoader;
    private static final Map<String, byte[]> byteCache = new HashMap<>();

    static {
        Field lookupF;
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            lookupF = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");

            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(lookupF), unsafe.staticFieldOffset(lookupF));
        try {
            packageLookup = lookup.findVarHandle(ModuleClassLoader.class, "packageLookup", Map.class);
            parentLoaders = lookup.findVarHandle(ModuleClassLoader.class, "parentLoaders", Map.class);
            getClassBytes = lookup.findVirtual(ModuleClassLoader.class, "getClassBytes", MethodType.methodType(byte[].class, ModuleReader.class, ModuleReference.class, String.class));
            classNameToModuleName = lookup.findVirtual(ModuleClassLoader.class, "classNameToModuleName", MethodType.methodType(String.class, String.class));
            loadFromModule = lookup.findVirtual(ModuleClassLoader.class, "loadFromModule", MethodType.methodType(Object.class, String.class, BiFunction.class));
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        targetClassLoader = Thread.currentThread().getContextClassLoader() instanceof ModuleClassLoader moduleClassLoader
                ? moduleClassLoader
                : (ModuleClassLoader) Thread.getAllStackTraces().keySet().stream()
                .map(Thread::getContextClassLoader)
                .filter(cl -> cl instanceof ModuleClassLoader)
                .findAny()
                .orElseThrow();
    }

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
        if (classNode.name.startsWith("com/nobtg/realgod/")) {
            return false;
        }

        boolean modify = false;

        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode insn : method.instructions) {
                modify = modify(method, insn);
            }
        }

        return modify;
    }

    private static boolean modify(MethodNode method, AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode methodInsn) {
            if (methodInsn.owner.equals("org/lwjgl/system/JNI")) {
                if (methodInsn.name.equals("invokePV") && methodInsn.desc.equals("(JIIJ)V")) {
                    apply(methodInsn);
                    return true;
                } else if (methodInsn.name.equals("invokePV") && methodInsn.desc.equals("(JJ)V")) {
                    apply(methodInsn);
                    return true;
                }
            } else if (methodInsn.owner.equals("org/lwjgl/glfw/GLFW")) {
                if (methodInsn.name.equals("glfwSwapBuffers") && methodInsn.desc.equals("(J)V")) {
                    apply(methodInsn);
                    return true;
                } else if (methodInsn.name.equals("glfwSetInputMode") && methodInsn.desc.equals("(JII)V")) {
                    apply(methodInsn);
                    return true;
                }
            } else if (isAssignableFrom(methodInsn.owner, "net/minecraft/world/entity/LivingEntity")) {
                if (methodInsn.name.equals("m_21223_") && methodInsn.desc.equals("()F")) {
                    apply(methodInsn, "getHealth", "(Lnet/minecraft/world/entity/LivingEntity;)F");
                    return true;
                } else if (methodInsn.name.equals("m_21224_") && methodInsn.desc.equals("()Z")) {
                    apply(methodInsn, "isDeadOrDying", "(Lnet/minecraft/world/entity/LivingEntity;)Z");
                    return true;
                }
            } else if (isAssignableFrom(methodInsn.owner, "net/minecraft/world/entity/Entity")) {
                if (methodInsn.name.equals("m_6084_") && methodInsn.desc.equals("()Z")) {
                    apply(methodInsn, "isAlive", "(Lnet/minecraft/world/entity/Entity;)Z");
                    return true;
                }
            }
        } else if (insn instanceof FieldInsnNode fieldInsn) {
            if (fieldInsn.getOpcode() == Opcodes.GETFIELD) {
                if (isAssignableFrom(fieldInsn.owner, "net/minecraft/client/MouseHandler")) {
                    if (fieldInsn.name.equals("f_91520_")) {
                        apply(fieldInsn, method.instructions, "(Lnet/minecraft/client/MouseHandler;)Z", "mouseGrabbed");
                        return true;
                    }
                }
            }
        }
        return false;
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

    private static byte[] getClassBytes(String aname) {
        byte[] bytes = byteCache.get(aname);

        if (bytes != null) {
            return bytes;
        }

        Throwable suppressed = null;
        String name = aname.replace('/', '.');

        try {
            String pname = name.substring(0, name.lastIndexOf('.'));
            if (((Map<String, ResolvedModule>) packageLookup.get(targetClassLoader)).containsKey(pname)) {
                bytes = (byte[]) loadFromModule.invoke(targetClassLoader, classNameToModuleName.invoke(targetClassLoader, name), (BiFunction<ModuleReader, ModuleReference, Object>) (reader, ref) -> {
                    try {
                        return getClassBytes.invoke(targetClassLoader, reader, ref, name);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Map<String, ClassLoader> parentLoadersMap = (Map<String, ClassLoader>) parentLoaders.get(targetClassLoader);
                if (parentLoadersMap.containsKey(pname)) {
                    try (InputStream is = parentLoadersMap.get(pname).getResourceAsStream(aname + ".class")) {
                        if (is != null) {
                            bytes = is.readAllBytes();
                        }
                    }
                }
            }
        } catch (Throwable e) {
            suppressed = e;
        }

        if (bytes == null || bytes.length == 0) {
            ClassNotFoundException e = new ClassNotFoundException(name);
            if (suppressed != null) e.addSuppressed(suppressed);
            throw new RuntimeException(e);
        }

        byteCache.put(name, bytes);

        return bytes;
    }

    private static boolean isAssignableFrom(String current, String father) {
        try {
            while (true) {
                if (current.equals(father)) {
                    return true;
                } else if (current.equals("java/lang/Object")) {
                    return false;
                } else {
                    current = new ClassReader(getClassBytes(current)).getSuperName();
                }
            }
        } catch (RuntimeException e) {
            if (e.getCause() instanceof ClassNotFoundException) {
                return false;
            } else {
                throw e;
            }
        }
    }
}
