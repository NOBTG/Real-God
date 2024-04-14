package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.shaders.BlendMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL14C;

@OnlyIn(Dist.CLIENT)
public final class BlendModeHelper {
    public static void apply(BlendMode mode) {
        if (!equals(mode, BlendMode.lastApplied)) {
            if (BlendMode.lastApplied == null || mode.opaque != BlendMode.lastApplied.opaque) {
                BlendMode.lastApplied = mode;
                if (mode.opaque) {
                    GlStateManagerHelper._disableBlend();
                    return;
                }

                GlStateManagerHelper._enableBlend();
            }

            GL14C.glBlendEquation(mode.blendFunc);

            if (mode.separateBlend) {
                GlStateManagerHelper._blendFuncSeparate(mode.srcColorFactor, mode.dstColorFactor, mode.srcAlphaFactor, mode.dstAlphaFactor);
            } else {
                GlStateManagerHelper._blendFunc(mode.srcColorFactor, mode.dstColorFactor);
            }
        }
    }

    public static boolean equals(BlendMode mode, Object p_85533_) {
        if (mode == p_85533_) {
            return true;
        } else if (!(p_85533_ instanceof BlendMode blendmode)) {
            return false;
        } else {
            if (mode.blendFunc != blendmode.blendFunc) {
                return false;
            } else if (mode.dstAlphaFactor != blendmode.dstAlphaFactor) {
                return false;
            } else if (mode.dstColorFactor != blendmode.dstColorFactor) {
                return false;
            } else if (mode.opaque != blendmode.opaque) {
                return false;
            } else if (mode.separateBlend != blendmode.separateBlend) {
                return false;
            } else if (mode.srcAlphaFactor != blendmode.srcAlphaFactor) {
                return false;
            } else {
                return mode.srcColorFactor == blendmode.srcColorFactor;
            }
        }
    }
}
