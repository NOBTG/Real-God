package com.nobtg.realgod.utils.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class UniformHelper {
    public static void upload(Uniform uniform) {
        uniform.dirty = false;
        if (uniform.type <= 3) {
            uploadAsInteger(uniform);
        } else if (uniform.type <= 7) {
            uploadAsFloat(uniform);
        } else {
            if (uniform.type > 10) {
                return;
            }
            uploadAsMatrix(uniform);
        }
    }

    private static void uploadAsInteger(Uniform uniform) {
        BufferHelper.rewind(uniform.intValues);
        switch (uniform.type) {
            case 0:
                GlStateManagerHelper._glUniform1(uniform.location, uniform.intValues);
                break;
            case 1:
                GlStateManagerHelper._glUniform2(uniform.location, uniform.intValues);
                break;
            case 2:
                GlStateManagerHelper._glUniform3(uniform.location, uniform.intValues);
                break;
            case 3:
                GlStateManagerHelper._glUniform4(uniform.location, uniform.intValues);
                break;
        }
    }

    private static void uploadAsFloat(Uniform uniform) {
        BufferHelper.rewind(uniform.floatValues);
        switch (uniform.type) {
            case 4:
                GlStateManagerHelper._glUniform1(uniform.location, uniform.floatValues);
                break;
            case 5:
                GlStateManagerHelper._glUniform2(uniform.location, uniform.floatValues);
                break;
            case 6:
                GlStateManagerHelper._glUniform3(uniform.location, uniform.floatValues);
                break;
            case 7:
                GlStateManagerHelper._glUniform4(uniform.location, uniform.floatValues);
                break;
        }
    }

    private static void uploadAsMatrix(Uniform uniform) {
        BufferHelper.clear(uniform.floatValues);
        switch (uniform.type) {
            case 8:
                GlStateManagerHelper._glUniformMatrix2(uniform.location, false, uniform.floatValues);
                break;
            case 9:
                GlStateManagerHelper._glUniformMatrix3(uniform.location, false, uniform.floatValues);
                break;
            case 10:
                GlStateManagerHelper._glUniformMatrix4(uniform.location, false, uniform.floatValues);
        }
    }

    public static void set(Uniform uniform, Matrix4f p_254249_) {
        BufferHelper.position(uniform.floatValues, 0);
        Matrix4fHelper.get(p_254249_, uniform.floatValues);
        markDirty(uniform);
    }

    private static void markDirty(Uniform uniform) {
        uniform.dirty = true;

        if (uniform.parent instanceof ShaderInstance instance) {
            ShaderInstanceHelper.markDirty(instance);
        } else if (uniform.parent instanceof EffectInstance instance) {
            EffectInstanceHelper.markDirty(instance);
        }
    }
}
