package com.nobtg.realgod;

import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.sun.tools.attach.*;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public final class Launch {
    public static void agentmain(String agentArgs, Instrumentation inst) {
        ClassHelper.inst = inst;
    }

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (descriptor.id().equals(args[0])) {
                descriptor.provider().attachVirtualMachine(descriptor).loadAgent(ClassHelper.getJarPath());
                return;
            }
        }
    }
}
