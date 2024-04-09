package com.realgodcoremod.core;

import cpw.mods.modlauncher.LaunchPluginHandler;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.serviceapi.ILaunchPluginService;
import org.jetbrains.annotations.NotNull;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.*;

public final class RealGodTransformationService implements ITransformationService {
    public static final MethodHandles.Lookup LOOKUP;

    @Override
    public @NotNull String name() {
        return "Real God TransformationService";
    }

    @Override
    public void initialize(IEnvironment iEnvironment) {
    }

    @Override
    public void onLoad(IEnvironment iEnvironment, Set<String> set) {
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return List.of();
    }

    static {
        MethodHandles.Lookup lookup;
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            lookup = (MethodHandles.Lookup) unsafe.getObject(unsafe.staticFieldBase(field),
                    unsafe.staticFieldOffset(field));
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }

        LaunchPluginHandler launchPlugins;
        VarHandle pluginsVar;
        try {
            launchPlugins = (LaunchPluginHandler) lookup.findVarHandle(Launcher.class, "launchPlugins", LaunchPluginHandler.class).get(Launcher.INSTANCE);
            pluginsVar = lookup.findVarHandle(LaunchPluginHandler.class, "plugins", Map.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        Map<String, ILaunchPluginService> newMap = new HashMap<>() {
            @Override
            public Collection<ILaunchPluginService> values() {
                List<ILaunchPluginService> values = new ArrayList<>(super.values());
                values.removeIf(value -> value instanceof RealGodLaunchPluginService);
                values.add(new RealGodLaunchPluginService());
                return values;
            }
        };
        newMap.putAll(((Map<String, ILaunchPluginService>) pluginsVar.get(launchPlugins)));
        pluginsVar.set(launchPlugins, newMap);
        LOOKUP = lookup;
    }
}
