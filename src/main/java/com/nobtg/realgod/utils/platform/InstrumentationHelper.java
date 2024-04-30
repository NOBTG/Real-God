package com.nobtg.realgod.utils.platform;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

public final class InstrumentationHelper {
    private static Instrumentation inst;
    private static final int pointerLength;
    private static final Unsafe unsafe;
    private static final MethodHandle enqueue;
    private static final MethodHandle instrumentationConstructor;

    static {
        try {
            boolean is64Bit = Integer.parseInt(System.getProperty("sun.arch.data.model")) == 64;
            pointerLength = is64Bit ? 8 : 4;
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
            Field implLookup = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            MethodHandles.Lookup lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(implLookup), unsafe.staticFieldOffset(implLookup));
            enqueue = lookup.findStatic(Class.forName("sun.tools.attach.VirtualMachineImpl"), "enqueue", MethodType.methodType(void.class, long.class, byte[].class, String.class, String.class, Object[].class));
            instrumentationConstructor = lookup.findConstructor(Class.forName("sun.instrument.InstrumentationImpl"), MethodType.methodType(void.class, long.class, boolean.class, boolean.class));
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    public static Instrumentation getInstrumentation() {
        try {
            Optional<Instrumentation> optInst = getInstrumentationWithSystem();
            if (optInst.isEmpty()) {
                allowAttachSelfAndInject();
            } else {
                inst = optInst.get();
            }
            return inst;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void agentmain(String args, Instrumentation inst) {
        InstrumentationHelper.inst = inst;
    }

    private static void allowAttachSelfAndInject() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException, URISyntaxException {
        Field unsafeF = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeF.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeF.get(null);

        Class<?> vmClass = Class.forName("sun.tools.attach.HotSpotVirtualMachine");

        Field allowAttachSelf = vmClass.getDeclaredField("ALLOW_ATTACH_SELF");
        Field currentPid = vmClass.getDeclaredField("CURRENT_PID");

        unsafe.putObject(unsafe.staticFieldBase(allowAttachSelf), unsafe.staticFieldOffset(allowAttachSelf), true);

        VirtualMachine.attach(String.valueOf(unsafe.getLong(unsafe.staticFieldBase(currentPid), unsafe.staticFieldOffset(currentPid)))).loadAgent(new File(InstrumentationHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath());
    }

    private static Optional<Instrumentation> agentForWindows() throws Throwable {
        long JPLISAgent = unsafe.allocateMemory(0x1000);

        byte[] buf = new byte[]{72, -125, -20, 40, 72, -125, -28, -16, 72, 49, -55, 101, 72, -117, 65, 96, 72, -117, 64, 24, 72, -117, 112, 32, 72, -83, 72, -106, 72, -83, 72, -117, 88, 32, 77, 49, -64, 68, -117, 67, 60, 76, -119, -62, 72, 1, -38, 68, -117, -126, -120, 0, 0, 0, 73, 1, -40, 72, 49, -10, 65, -117, 112, 32, 72, 1, -34, 72, 49, -55, 73, -71, 71, 101, 116, 80, 114, 111, 99, 65, 72, -1, -63, 72, 49, -64, -117, 4, -114, 72, 1, -40, 76, 57, 8, 117, -17, 72, 49, -10, 65, -117, 112, 36, 72, 1, -34, 102, -117, 12, 78, 72, 49, -10, 65, -117, 112, 28, 72, 1, -34, 72, 49, -46, -117, 20, -114, 72, 1, -38, 72, -119, -41, -71, 97, 114, 121, 65, 81, 72, -71, 76, 111, 97, 100, 76, 105, 98, 114, 81, 72, -119, -30, 72, -119, -39, 72, -125, -20, 48, -1, -41, 72, -125, -60, 48, 72, -125, -60, 16, 72, -119, -58, -71, 108, 108, 0, 0, 81, -71, 106, 118, 109, 0, 81, 72, -119, -31, 72, -125, -20, 48, -1, -42, 72, -125, -60, 48, 72, -125, -60, 16, 73, -119, -57, 72, 49, -55, 72, -71, 118, 97, 86, 77, 115, 0, 0, 0, 81, 72, -71, 114, 101, 97, 116, 101, 100, 74, 97, 81, 72, -71, 74, 78, 73, 95, 71, 101, 116, 67, 81, 72, -119, -30, 76, -119, -7, 72, -125, -20, 40, -1, -41, 72, -125, -60, 40, 72, -125, -60, 24, 73, -119, -57, 72, -125, -20, 40, 72, -119, -31, -70, 1, 0, 0, 0, 73, -119, -56, 73, -125, -64, 8, 72, -125, -20, 40, 65, -1, -41, 72, -125, -60, 40, 72, -117, 9, 72, -125, -20, 32, 84, 72, -119, -30, 77, 49, -64, 76, -117, 57, 77, -117, 127, 32, 73, -119, -50, 65, -1, -41, 76, -119, -15, 72, -70, 72, 71, 70, 69, 68, 67, 66, 65, 65, -72, 0, 2, 1, 48, 77, -117, 62, 77, -117, 127, 48, 72, -125, -20, 32, 65, -1, -41, 72, -125, -60, 32, 76, -119, -15, 77, -117, 62, 77, -117, 127, 40, 65, -1, -41, 72, -125, -60, 120, -61};
        byte[] stub = new byte[]{72, 71, 70, 69, 68, 67, 66, 65};

        if (pointerLength == 4) {
            buf = new byte[]{-112, -112, -112, 51, -55, 100, -95, 48, 0, 0, 0, -117, 64, 12, -117, 112, 20, -83, -106, -83, -117, 88, 16, -117, 83, 60, 3, -45, -117, 82, 120, 3, -45, 51, -55, -117, 114, 32, 3, -13, 65, -83, 3, -61, -127, 56, 71, 101, 116, 80, 117, -12, -127, 120, 4, 114, 111, 99, 65, 117, -21, -127, 120, 8, 100, 100, 114, 101, 117, -30, -117, 114, 36, 3, -13, 102, -117, 12, 78, 73, -117, 114, 28, 3, -13, -117, 20, -114, 3, -45, 82, 51, -55, 81, 104, 97, 114, 121, 65, 104, 76, 105, 98, 114, 104, 76, 111, 97, 100, 84, 83, -1, -46, -125, -60, 12, 89, 80, 102, -71, 51, 50, 81, 104, 106, 118, 109, 0, 84, -1, -48, -117, -40, -125, -60, 12, 90, 51, -55, 81, 106, 115, 104, 118, 97, 86, 77, 104, 101, 100, 74, 97, 104, 114, 101, 97, 116, 104, 71, 101, 116, 67, 104, 74, 78, 73, 95, 84, 83, -1, -46, -119, 69, -16, 84, 106, 1, 84, 89, -125, -63, 16, 81, 84, 89, 106, 1, 81, -1, -48, -117, -63, -125, -20, 48, 106, 0, 84, 89, -125, -63, 16, 81, -117, 0, 80, -117, 24, -117, 67, 16, -1, -48, -117, 67, 24, 104, 0, 2, 1, 48, 104, 68, 67, 66, 65, -125, -20, 4, -1, -48, -125, -20, 12, -117, 67, 20, -1, -48, -125, -60, 92, -61};
            stub = new byte[]{68, 67, 66, 65};
        }

        long l = JPLISAgent + pointerLength;
        byte[] byteTarget = new byte[pointerLength];

        for (int i = 0; i < byteTarget.length; i++) {
            byteTarget[i] = (byte) (l >> (i * 8));
        }

        for (int i = 0; i < buf.length; i++) {
            boolean bl = true;

            for (int j = 0; j < stub.length; j++) {
                if (i + j < buf.length && buf[i + j] == stub[j]) {
                } else {
                    bl = false;
                }
            }

            if (bl) {
                System.arraycopy(byteTarget, 0, buf, i, byteTarget.length);
            }
        }

        try {
            enqueue.invoke(-1, buf, "enqueue", "enqueue");
        } catch (Throwable e) {
            e.printStackTrace();
            return Optional.empty();
        }

        long native_jvmtienv = unsafe.getLong(JPLISAgent + pointerLength);

        if (pointerLength == 4) {
            unsafe.putByte(native_jvmtienv + 201L, (byte) 2);
        } else {
            unsafe.putByte(native_jvmtienv + 361L, (byte) 2);
        }

        Optional<Instrumentation> inst;

        try {
            inst = Optional.ofNullable((Instrumentation) instrumentationConstructor.invoke(JPLISAgent, true, true));
        } catch (Throwable error) {
            error.printStackTrace();
            throw error;
        }

        return inst;
    }

    private static Optional<Instrumentation> getInstrumentationWithSystem() throws Throwable {
        String operSys = System.getProperty("os.name").toLowerCase();
        if (operSys.contains("win")) {
            return agentForWindows();
        } else if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix")) {
            return agentForLinux();
        } else if (operSys.contains("mac")) {
            return Optional.empty();
        } else if (operSys.contains("sunos")) {
            return Optional.empty();
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Instrumentation> agentForLinux() throws Exception {
        FileReader fin = new FileReader("/proc/self/maps");
        BufferedReader reader = new BufferedReader(fin);
        String line;
        long RandomAccessFile_length = 0, JNI_GetCreatedJavaVMs = 0;
        while ((line = reader.readLine()) != null) {
            String[] splits = line.trim().split(" ");
            if (line.endsWith("libjava.so") && RandomAccessFile_length == 0) {
                String[] addr_range = splits[0].split("-");
                long libbase = Long.parseLong(addr_range[0], 16);
                String elfpath = splits[splits.length - 1];
                RandomAccessFile_length = find_symbol(elfpath, "Java_java_io_RandomAccessFile_length", libbase);
            } else if (line.endsWith("libjvm.so") && JNI_GetCreatedJavaVMs == 0) {
                String[] addr_range = splits[0].split("-");
                long libbase = Long.parseLong(addr_range[0], 16);
                String elfpath = splits[splits.length - 1];
                JNI_GetCreatedJavaVMs = find_symbol(elfpath, "JNI_GetCreatedJavaVMs", libbase);
            }

            if (JNI_GetCreatedJavaVMs != 0 && RandomAccessFile_length != 0) {
                break;
            }
        }
        fin.close();

        RandomAccessFile fout = new RandomAccessFile("/proc/self/mem", "rw");
        byte[] stack_align = {0x55, 0x48, (byte) 0x89, (byte) 0xe5, 0x48, (byte) 0xc7, (byte) 0xc0, 0xf, 0, 0, 0, 0x48, (byte) 0xf7, (byte) 0xd0};

        byte[] movabs_rax = {0x48, (byte) 0xb8};
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(0, JNI_GetCreatedJavaVMs);

        byte[] b = {0x48, (byte) 0x83, (byte) 0xEC, 0x40, 0x48, 0x31, (byte) 0xF6, 0x48, (byte) 0xFF, (byte) 0xC6, 0x48, (byte) 0x8D, 0x54, 0x24, 0x04, 0x48, (byte) 0x8D, 0x7C, 0x24, 0x08, (byte) 0xFF, (byte) 0xD0, 0x48, (byte) 0x8B, 0x7C, 0x24, 0x08, 0x48, (byte) 0x8D, 0x74, 0x24, 0x10, (byte) 0xBA, 0x00, 0x02, 0x01, 0x30, 0x48, (byte) 0x8B, 0x07, (byte) 0xFF, 0x50, 0x30, 0x48, (byte) 0x8B, 0x44, 0x24, 0x10, 0x48, (byte) 0x83, (byte) 0xC4, 0x40, (byte) 0xC9, (byte) 0xC3};

        int shellcode_len = b.length + 8 + movabs_rax.length + stack_align.length;
        long landingpad = RandomAccessFile_length;

        byte[] backup = new byte[shellcode_len];
        fout.seek(landingpad);
        fout.read(backup);

        fout.seek(landingpad);
        fout.write(stack_align);
        fout.write(movabs_rax);
        fout.write(buffer.array());
        fout.write(b);
        fout.close();

        long native_jvmtienv = fout.length();
        System.out.printf("native_jvmtienv %x\n", native_jvmtienv);

        fout = new RandomAccessFile("/proc/self/mem", "rw");
        fout.seek(RandomAccessFile_length);
        fout.write(backup);
        fout.close();

        unsafe.putByte(native_jvmtienv + 361, (byte) 2);
        long JPLISAgent = unsafe.allocateMemory(0x1000);
        unsafe.putLong(JPLISAgent + 8, native_jvmtienv);
        Instrumentation inst = null;
        try {
            inst = (Instrumentation) instrumentationConstructor.invoke(JPLISAgent, true, false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        fout.getFD();
        return Optional.ofNullable(inst);
    }

    private static long find_symbol(String elfpath, String sym, long libbase) throws IOException {
        long func_ptr = 0;
        RandomAccessFile fin = new RandomAccessFile(elfpath, "r");

        byte[] e_ident = new byte[16];
        fin.read(e_ident);
        fin.readShort();
        fin.readShort();
        fin.readInt();
        fin.readLong();
        fin.readLong();
        long e_shoff = Long.reverseBytes(fin.readLong());
        fin.readInt();
        fin.readShort();
        fin.readShort();
        fin.readShort();
        short e_shentsize = Short.reverseBytes(fin.readShort());
        short e_shnum = Short.reverseBytes(fin.readShort());
        fin.readShort();

        int sh_type;
        long sh_offset = 0;
        long sh_size = 0;
        int sh_link = 0;
        long sh_entsize = 0;

        for (int i = 0; i < e_shnum; ++i) {
            fin.seek(e_shoff + i * 64);
            fin.readInt();
            sh_type = Integer.reverseBytes(fin.readInt());
            fin.readLong();
            fin.readLong();
            sh_offset = Long.reverseBytes(fin.readLong());
            sh_size = Long.reverseBytes(fin.readLong());
            sh_link = Integer.reverseBytes(fin.readInt());
            fin.readInt();
            fin.readLong();
            sh_entsize = Long.reverseBytes(fin.readLong());
            if (sh_type == 11) {
                break;
            }
        }

        int symtab_shdr_sh_link = sh_link;
        long symtab_shdr_sh_size = sh_size;
        long symtab_shdr_sh_entsize = sh_entsize;
        long symtab_shdr_sh_offset = sh_offset;

        fin.seek(e_shoff + (long) symtab_shdr_sh_link * e_shentsize);
        fin.readInt();
        fin.readInt();
        fin.readLong();
        fin.readLong();
        sh_offset = Long.reverseBytes(fin.readLong());
        fin.readLong();
        fin.readInt();
        fin.readInt();
        fin.readLong();
        fin.readLong();

        long symstr_shdr_sh_offset = sh_offset;

        long cnt = symtab_shdr_sh_entsize > 0 ? symtab_shdr_sh_size / symtab_shdr_sh_entsize : 0;
        for (long i = 0; i < cnt; ++i) {
            fin.seek(symtab_shdr_sh_offset + symtab_shdr_sh_entsize * i);
            int st_name = Integer.reverseBytes(fin.readInt());
            byte st_info = fin.readByte();
            fin.readByte();
            fin.readShort();
            long st_value = Long.reverseBytes(fin.readLong());
            fin.readLong();
            if (st_value == 0 || st_name == 0 || ((st_info & 0xf) != 2 && (st_info & 0xf) != 10)) {
                continue;
            }

            fin.seek(symstr_shdr_sh_offset + st_name);
            StringBuilder name = new StringBuilder();
            byte ch;
            while ((ch = fin.readByte()) != 0) {
                name.append((char) ch);
            }

            if (sym.contentEquals(name)) {
                func_ptr = libbase + st_value;
                break;
            }
        }

        fin.close();

        return func_ptr;
    }
}