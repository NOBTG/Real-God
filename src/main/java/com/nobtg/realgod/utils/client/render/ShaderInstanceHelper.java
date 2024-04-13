package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL20C;

@OnlyIn(Dist.CLIENT)
public final class ShaderInstanceHelper {
    public static void setSampler(ShaderInstance instance, String p_173351_, Object p_173352_) {
        instance.samplerMap.put(p_173351_, p_173352_);
        markDirty(instance);
    }

    public static void markDirty(ShaderInstance instance) {
        instance.dirty = true;
    }

    public static void apply(ShaderInstance instance) {
        instance.dirty = false;
        ShaderInstance.lastAppliedShader = instance;

        BlendModeHelper.apply(instance.blend);

        if (instance.programId != ShaderInstance.lastProgramId) {
            GL20C.glUseProgram(instance.programId);
            ShaderInstance.lastProgramId = instance.programId;
        }

        int i = GlStateManagerHelper._getActiveTexture();

        for (int j = 0; j < instance.samplerLocations.size(); ++j) {
            String s = instance.samplerNames.get(j);
            if (instance.samplerMap.get(s) != null) {
                int k = GlStateManagerHelper._glGetUniformLocation(instance.programId, s);

                GL20C.glUniform1i(k, j);

                GlStateManagerHelper._activeTexture('\u84c0' + j);

                Object object = instance.samplerMap.get(s);

                int l = -1;
                if (object instanceof RenderTarget target) {
                    l = target.colorTextureId;
                } else if (object instanceof AbstractTexture texture) {
                    l = AbstractTextureHelper.getId(texture);
                } else if (object instanceof Integer integer) {
                    l = integer;
                }

                if (l != -1) {
                    GlStateManagerHelper._bindTexture(l);
                }
            }
        }

        GlStateManagerHelper._activeTexture(i);

        for (Uniform uniform : instance.uniforms) {
            UniformHelper.upload(uniform);
        }
    }

    public static void clear(ShaderInstance instance) {
        GL20C.glUseProgram(0);
        ShaderInstance.lastProgramId = -1;
        ShaderInstance.lastAppliedShader = null;
        int i = GlStateManagerHelper._getActiveTexture();

        for(int j = 0; j < instance.samplerLocations.size(); ++j) {
            if (instance.samplerMap.get(instance.samplerNames.get(j)) != null) {
                GlStateManagerHelper._activeTexture('\u84c0' + j);
                GlStateManagerHelper._bindTexture(0);
            }
        }
        GlStateManagerHelper._activeTexture(i);
    }
}
