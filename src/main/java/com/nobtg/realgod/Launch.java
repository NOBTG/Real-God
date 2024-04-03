package com.nobtg.realgod;

import com.nobtg.realgod.items.TestItem;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.sun.tools.attach.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

@Mod(Launch.modID)
public final class Launch {
    public static final String modID = "real_god";
    public static Instrumentation inst;

    public Launch() {
        DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, modID);
        items.register("real_god", () -> new TestItem(new Item.Properties()));
        items.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.err.println("114514");
        Launch.inst = inst;
    }

    public static void main(String[] args) throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        for (VirtualMachineDescriptor descriptor : VirtualMachine.list()) {
            if (descriptor.id().equals(args[0])) {
                descriptor.provider().attachVirtualMachine(descriptor).loadAgent(ClassHelper.getJarPath(Launch.class));
                return;
            }
        }
    }

    public static void inject() {
        try {
            new ProcessBuilder("java", "-jar", ClassHelper.getJarPath(Launch.class), String.valueOf(ProcessHandle.current().pid())).start().waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
