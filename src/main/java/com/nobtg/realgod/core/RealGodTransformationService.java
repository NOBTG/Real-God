package com.nobtg.realgod.core;

import com.nobtg.realgod.Launch;
import com.nobtg.realgod.utils.clazz.Method;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class RealGodTransformationService implements ITransformationService {
    public static final List<Method> targetMethods = List.of(new Method("invoke", "java.lang.invoke.MethodHandle", "([Ljava/lang/Object;)Ljava/lang/Object;"), new Method("invoke", "java.lang.reflect.Method", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;"));

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
        Launch.inject();
    }
}
