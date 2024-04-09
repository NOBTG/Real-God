package com.nobtg.realgod;

import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.sun.tools.attach.*;
import cpw.mods.modlauncher.TransformingClassLoader;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

public final class Launch {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        ClassHelper.inst = inst;
        ClassFileTransformer transformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className!= null && className.equals("cpw/mods/modlauncher/TransformingClassLoader")) {
                    ClassNode newCn = new ClassNode(Opcodes.ASM9);
                    byte[] bytes0;
                    try (InputStream input = loader.getResourceAsStream(className + ".class")) {
                        assert input != null;
                        bytes0 = input.readAllBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    ClassReader newCr = new ClassReader(bytes0);
                    newCr.accept(newCn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

                    ClassNode cn = new ClassNode(Opcodes.ASM9);
                    ClassReader cr = new ClassReader(classfileBuffer);
                    cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

                    for (MethodNode method : cn.methods) {
                        if (method.name.equals("buildTransformedClassNodeFor") && method.desc.equals("(Ljava/lang/String;Ljava/lang/String;)[B")) {
                            for (MethodNode newMethod : newCn.methods) {
                                if (method.name.equals(newMethod.name)) {
                                    method.instructions = newMethod.instructions;
                                }
                            }

                            for (AbstractInsnNode instruction : method.instructions) {
                                if (instruction instanceof MethodInsnNode methodInsnNode) {
                                    if (methodInsnNode.name.equals("getMaybeTransformedClassBytes") && methodInsnNode.owner.equals("cpw/mods/cl/ModuleClassLoader")) {
                                        InsnList newInstructions = new InsnList();
                                        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                                        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                                        newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
                                        newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/cl/ModuleClassLoader", "getMaybeTransformedClassBytes", "(Ljava/lang/String;Ljava/lang/String;)[B", false));
                                        newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/nobtg/realgod/utils/clazz/ClassHelper", "buildTransformedClassNodeFor", "([B)[B", false));
                                        newInstructions.add(new InsnNode(Opcodes.ARETURN));
                                        method.instructions.insertBefore(methodInsnNode, newInstructions);
                                        method.instructions.remove(methodInsnNode);
                                    }
                                }
                            }
                        }
                    }

                    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                    cn.accept(cw);
                    return cw.toByteArray();
                } else return null;
            }
        };
        inst.addTransformer(transformer, true);
        try {
            inst.retransformClasses(TransformingClassLoader.class);
        } catch (UnmodifiableClassException e) {
            throw new RuntimeException(e);
        }
        inst.removeTransformer(transformer);
    }

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (descriptor.id().equals(args[0])) {
                descriptor.provider().attachVirtualMachine(descriptor).loadAgent(ClassHelper.getJarPath(Launch.class));
                return;
            }
        }
    }
}
