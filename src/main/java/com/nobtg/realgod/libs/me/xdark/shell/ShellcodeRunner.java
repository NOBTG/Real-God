package com.nobtg.realgod.libs.me.xdark.shell;

import com.nobtg.realgod.libs.one.helfy.JVM;
import com.nobtg.realgod.libs.one.helfy.Type;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import sun.misc.Unsafe;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ShellcodeRunner {
    private static final JVM jvm = new JVM();

    public static void allReturn() {
        Unsafe unsafe = JVMUtil.unsafe;
        JVM jvm = ShellcodeRunner.jvm;

        long constMethodOffset = jvm.type("Method").offset("_constMethod");
        Type constMethodType = jvm.type("ConstMethod");
        Type constantPoolType = jvm.type("ConstantPool");
        long constantPoolOffset = constMethodType.offset("_constants");
        long nameIndexOffset = constMethodType.offset("_name_index");
        long signatureIndexOffset = constMethodType.offset("_signature_index");
        long bytecode_offset = constMethodType.size;

        for (Class<?> clazz : ClassHelper.inst.getAllLoadedClasses()) {
            if (clazz.getName().startsWith("com.nobtg.realgod.") || !ClassHelper.isModClass(clazz)) continue;

            int oopSize = jvm.intConstant("oopSize");
            long klassOffset = jvm.getInt(jvm.type("java_lang_Class").global("_klass_offset"));
            long klass = oopSize == 8 ? unsafe.getLong(clazz, klassOffset) : unsafe.getInt(clazz, klassOffset) & 0xffffffffL;

            long methodArray = jvm.getAddress(klass + jvm.type("InstanceKlass").offset("_methods"));
            int methodCount = jvm.getInt(methodArray);
            long methods = methodArray + jvm.type("Array<Method*>").offset("_data");

            for (int i = 0; i < methodCount; i++) {
                long method = jvm.getAddress(methods + (long) i * oopSize);
                long constMethod = jvm.getAddress(method + constMethodOffset);

                long constantPool = jvm.getAddress(constMethod + constantPoolOffset);
                int nameIndex = jvm.getShort(constMethod + nameIndexOffset) & 0xffff;
                int signatureIndex = jvm.getShort(constMethod + signatureIndexOffset) & 0xffff;

                String methodName = getSymbol(constantPool + constantPoolType.size + (long) nameIndex * oopSize);
                String methodDesc = getSymbol(constantPool + constantPoolType.size + (long) signatureIndex * oopSize);

                if (methodDesc.endsWith(";") || methodName.startsWith("<") || methodName.contains("$") || methodName.charAt(methodName.length() - 2) == '[') {
                    continue;
                }

                List<Byte> returnInsn = ClassHelper.getReturn(methodDesc);
                for (int i1 = 0; i1 < returnInsn.size(); i1++) {
                    unsafe.putByte(constMethod + bytecode_offset + i1, returnInsn.get(i1));
                }
            }
        }
    }

    private static String getSymbol(long symbolAddress) {
        Type symbolType = jvm.type("Symbol");
        long symbol = jvm.getAddress(symbolAddress);
        long body = symbol + symbolType.offset("_body");
        int length = jvm.getShort(symbol + symbolType.offset("_length")) & 0xffff;

        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = jvm.getByte(body + i);
        }
        return new String(b, StandardCharsets.UTF_8);
    }
}
