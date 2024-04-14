package com.nobtg.realgod.utils.client.render.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public final class SuperFont extends Font {
    public static float tick = 0.0F;
    public static final SuperFont INSTANCE;

    static {
        FontManager manager = Minecraft.instance.fontManager;
        INSTANCE = new SuperFont(p_284586_ -> manager.fontSets.getOrDefault(manager.renames.getOrDefault(p_284586_, p_284586_), manager.missingFontSet), false);
    }

    public SuperFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    @Override
    public int drawInBatch(FormattedCharSequence p_273262_, float x, float y, int p_273375_, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, DisplayMode p_273418_, int p_273330_, int p_272981_) {
        StringBuilder builder = new StringBuilder();
        p_273262_.accept((p_13746_, p_13747_, p_13748_) -> {
            builder.appendCodePoint(p_13748_);
            return true;
        });
        String text = builder.toString();
        for (int index = 0; index < text.length(); index++) {
            String s = String.valueOf(text.charAt(index));
            super.drawInternal(s, x, y, Color.HSBtoRGB(((tick + index) % 720.0f >= 360.0f ? 720.0f - (tick + index) % 720.0f : (tick + index) % 720.0f) / 100.0f, 0.8f, 0.8f), p_273674_, p_273525_, p_272624_, p_273418_, p_273330_, p_272981_, this.isBidirectional());
            x += this.width(s);
        }
        return (int) x;
    }
}
