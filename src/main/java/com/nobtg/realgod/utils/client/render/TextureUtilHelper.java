package com.nobtg.realgod.utils.client.render;

import net.minecraft.SharedConstants;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.ThreadLocalRandom;

@OnlyIn(Dist.CLIENT)
public final class TextureUtilHelper {
    public static int generateTextureId() {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            int[] aint = new int[ThreadLocalRandom.current().nextInt(15) + 1];
            GlStateManagerHelper._genTextures(aint);
            int i = GlStateManagerHelper._genTexture();
            GlStateManagerHelper._deleteTextures(aint);
            return i;
        } else {
            return GlStateManagerHelper._genTexture();
        }
    }
}
