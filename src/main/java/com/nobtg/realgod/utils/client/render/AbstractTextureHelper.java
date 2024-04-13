package com.nobtg.realgod.utils.client.render;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class AbstractTextureHelper {
    public static int getId(AbstractTexture texture) {
        if (texture.id == -1) {
            texture.id = TextureUtilHelper.generateTextureId();
        }
        return texture.id;
    }
}
