package com.nobtg.realgod.utils.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyMappingLookup;
import net.minecraftforge.client.settings.KeyModifier;

import java.lang.invoke.VarHandle;
import java.util.*;

public final class KeyMappingHelper {
    private static final VarHandle map;

    static {
        try {
            map = ClassHelper.lookup.findStaticVarHandle(KeyMappingLookup.class, "map", EnumMap.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(InputConstants.Key p_90838_, boolean p_90839_) {
        for (KeyMapping keymapping : getAll(p_90838_)) {
            if (keymapping != null) {
                keymapping.isDown = p_90839_;
            }
        }
    }

    public static List<KeyMapping> getAll(InputConstants.Key keyCode) {
        List<KeyMapping> matchingBindings = new ArrayList<>();
        for (Map<InputConstants.Key, Collection<KeyMapping>> bindingsMap : ((EnumMap<KeyModifier, Map<InputConstants.Key, Collection<KeyMapping>>>) map.get()).values()) {
            Collection<KeyMapping> bindings = bindingsMap.get(keyCode);
            if (bindings != null) {
                matchingBindings.addAll(bindings);
            }
        }
        return matchingBindings;
    }

    public static void click(InputConstants.Key p_90836_) {
        for (KeyMapping keymapping : getAll(p_90836_)) {
            if (keymapping != null) {
                ++keymapping.clickCount;
            }
        }
    }
}
