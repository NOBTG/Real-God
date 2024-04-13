package com.nobtg.realgod.utils.client.render;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class EffectInstanceHelper {
    public static void markDirty(EffectInstance instance) {
        instance.dirty = true;
    }
}
