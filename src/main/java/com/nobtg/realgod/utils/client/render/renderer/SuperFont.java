package com.nobtg.realgod.utils.client.render.renderer;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.awt.*;
import java.util.function.Function;

@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public final class SuperFont extends Font {
    private static float tick = 0.0F;
    private static SuperFont INSTANCE;

    public static SuperFont getInstance() {
        if (INSTANCE == null) {
            FontManager manager = Minecraft.instance.fontManager;
            INSTANCE = new SuperFont(p_284586_ -> manager.fontSets.getOrDefault(manager.renames.getOrDefault(p_284586_, p_284586_), manager.missingFontSet), false);
        }
        return INSTANCE;
    }

    public SuperFont(Function<ResourceLocation, FontSet> p_243253_, boolean p_243245_) {
        super(p_243253_, p_243245_);
    }

    @Override
    public int drawInBatch(FormattedCharSequence p_273262_, float x, float y, int color, boolean p_273674_, Matrix4f p_273525_, MultiBufferSource p_272624_, DisplayMode p_273418_, int p_273330_, int p_272981_) {
        StringBuilder builder = new StringBuilder();
        p_273262_.accept((p_13746_, p_13747_, p_13748_) -> {
            builder.appendCodePoint(p_13748_);
            return true;
        });
        return this.drawInternal(builder.toString(), x, y, color, p_273674_, p_273525_, p_272624_, p_273418_, p_273330_, p_272981_, this.isBidirectional());
    }

    @Override
    public int drawInternal(String p_273658_, float p_273086_, float p_272883_, int p_273547_, boolean p_272778_, Matrix4f p_272662_, MultiBufferSource p_273012_, DisplayMode p_273381_, int p_272855_, int p_272745_, boolean p_272785_) {
        if (p_272785_) {
            p_273658_ = this.bidirectionalShaping(p_273658_);
        }

        p_273547_ = adjustColor(p_273547_);
        Matrix4f matrix4f = new Matrix4f(p_272662_);
        if (p_272778_) {
            this.renderText(p_273658_, p_273086_, p_272883_, p_273547_, true, p_272662_, p_273012_, p_273381_, p_272855_, p_272745_);
            matrix4f.translate(SHADOW_OFFSET);
        }

        p_273086_ = this.renderText(p_273658_, p_273086_, p_272883_, p_273547_, false, matrix4f, p_273012_, p_273381_, p_272855_, p_272745_);
        return (int) p_273086_ + (p_272778_ ? 1 : 0);
    }

    @Override
    public float renderText(String p_273765_, float p_273532_, float p_272783_, int p_273217_, boolean p_273583_, Matrix4f p_272734_, MultiBufferSource p_272595_, DisplayMode p_273610_, int p_273727_, int p_273199_) {
        StringRenderOutput font$stringrenderoutput = new StringRenderOutput(p_272595_, p_273532_, p_272783_, p_273217_, p_273583_, p_272734_, p_273610_, p_273199_);
        iterateFormatted(p_273765_, Style.EMPTY, font$stringrenderoutput, p_273583_);
        return font$stringrenderoutput.finish(p_273727_, p_273532_);
    }

    public static void iterateFormatted(String p_14312_, Style p_14314_, StringRenderOutput p_14316_, boolean renderShadow) {
        int $$5 = p_14312_.length();
        Style $$6 = p_14314_;

        for (int $$7 = 0; $$7 < $$5; ++$$7) {
            char $$8 = p_14312_.charAt($$7);
            char $$11;
            if ($$8 == 167) {
                if ($$7 + 1 >= $$5) {
                    break;
                }

                $$11 = p_14312_.charAt($$7 + 1);
                ChatFormatting $$10 = ChatFormatting.getByCode($$11);
                if ($$10 != null) {
                    $$6 = $$10 == ChatFormatting.RESET ? p_14314_ : $$6.applyLegacyFormat($$10);
                }

                ++$$7;
            } else if (Character.isHighSurrogate($$8)) {
                if ($$7 + 1 >= $$5) {
                    if (!accept(p_14316_, $$6, 65533, $$7, renderShadow)) {
                        return;
                    }
                    break;
                }

                $$11 = p_14312_.charAt($$7 + 1);
                if (Character.isLowSurrogate($$11)) {
                    if (!accept(p_14316_, $$6, Character.toCodePoint($$8, $$11), $$7, renderShadow)) {
                        return;
                    }

                    ++$$7;
                } else if (!accept(p_14316_, $$6, 65533, $$7, renderShadow)) {
                    return;
                }
            } else if (!feedChar($$6, p_14316_, $$8, $$7, renderShadow)) {
                return;
            }
        }
    }

    private static boolean feedChar(Style p_14333_, StringRenderOutput p_14334_, char p_14336_, int current, boolean renderShadow) {
        return Character.isSurrogate(p_14336_) ? accept(p_14334_, p_14333_, 65533, current, renderShadow) : accept(p_14334_, p_14333_, p_14336_, current, renderShadow);
    }

    public static boolean accept(StringRenderOutput output, Style p_92968_, int p_92969_, int current, boolean renderShadow) {
        FontSet fontset = getInstance().getFontSet(p_92968_.getFont());
        GlyphInfo glyphinfo = fontset.getGlyphInfo(p_92969_, getInstance().filterFishyGlyphs);
        BakedGlyph bakedglyph = p_92968_.isObfuscated() && p_92969_ != 32 ? fontset.getRandomGlyph(glyphinfo) : fontset.getGlyph(p_92969_);
        boolean flag = p_92968_.isBold();
        float f3 = output.a;
        TextColor textcolor = p_92968_.getColor();
        float f;
        float f1;
        float f2;
        if (textcolor != null) {
            int i = textcolor.getValue();
            f = (float) (i >> 16 & 255) / 255.0F * output.dimFactor;
            f1 = (float) (i >> 8 & 255) / 255.0F * output.dimFactor;
            f2 = (float) (i & 255) / 255.0F * output.dimFactor;
        } else {
            f = output.r;
            f1 = output.g;
            f2 = output.b;
        }

        float f7;
        float f6;
        if (!(bakedglyph instanceof EmptyGlyph)) {
            f6 = flag ? glyphinfo.getBoldOffset() : 0.0F;
            f7 = output.dropShadow ? glyphinfo.getShadowOffset() : 0.0F;
            VertexConsumer vertexconsumer = output.bufferSource.getBuffer(bakedglyph.renderType(output.mode));
            renderChar(bakedglyph, flag, p_92968_.isItalic(), f6, output.x + f7, output.y + f7, output.pose, vertexconsumer, output.packedLightCoords, current, f, f1, f2, f3, renderShadow);
        }

        f6 = glyphinfo.getAdvance(flag);
        f7 = output.dropShadow ? 1.0F : 0.0F;
        if (p_92968_.isStrikethrough()) {
            output.addEffect(new BakedGlyph.Effect(output.x + f7 - 1.0F, output.y + f7 + 4.5F, output.x + f7 + f6, output.y + f7 + 4.5F - 1.0F, 0.01F, f, f1, f2, f3));
        }

        if (p_92968_.isUnderlined()) {
            output.addEffect(new BakedGlyph.Effect(output.x + f7 - 1.0F, output.y + f7 + 9.0F, output.x + f7 + f6, output.y + f7 + 9.0F - 1.0F, 0.01F, f, f1, f2, f3));
        }

        output.x += f6;
        return true;
    }

    public static void renderChar(BakedGlyph p_254105_, boolean p_254001_, boolean p_254262_, float p_254256_, float p_253753_, float p_253629_, Matrix4f p_254014_, VertexConsumer p_253852_, int p_253905_, int current, float p_95232_, float p_95233_, float p_95234_, float p_95235_, boolean renderShadow) {
        render(p_254105_, renderShadow, p_254262_, p_253753_, p_253629_, p_254014_, p_253852_, p_253905_, current, p_95232_, p_95233_, p_95234_, p_95235_);
        if (p_254001_) {
            render(p_254105_, renderShadow, p_254262_, p_253753_ + p_254256_, p_253629_, p_254014_, p_253852_, p_253905_, current, p_95232_, p_95233_, p_95234_, p_95235_);
        }
    }

    public static void render(BakedGlyph glyph, boolean renderShadow, boolean p_95227_, float x, float y, Matrix4f matrix4f, VertexConsumer vertexConsumer, int uv2, int current, float colorR, float colorG, float colorB, float colorA) {
        float $$11 = x + glyph.left;
        float $$12 = x + glyph.right;
        float $$13 = glyph.up - 3.0F;
        float $$14 = glyph.down - 3.0F;
        float $$15 = y + $$13;
        float $$16 = y + $$14;
        float $$17 = p_95227_ ? 1.0F - 0.25F * $$13 : 0.0F;
        float $$18 = p_95227_ ? 1.0F - 0.25F * $$14 : 0.0F;
        if (renderShadow) {
//            vertexConsumer.vertex(matrix4f, $$11 + $$17, $$15, 0.0F).color(colorR, colorG, colorB, colorA).uv(glyph.u0, glyph.v0).uv2(uv2).endVertex();
//            vertexConsumer.vertex(matrix4f, $$11 + $$18, $$16, 0.0F).color(colorR, colorG, colorB, colorA).uv(glyph.u0, glyph.v1).uv2(uv2).endVertex();
//            vertexConsumer.vertex(matrix4f, $$12 + $$18, $$16, 0.0F).color(colorR, colorG, colorB, colorA).uv(glyph.u1, glyph.v1).uv2(uv2).endVertex();
//            vertexConsumer.vertex(matrix4f, $$12 + $$17, $$15, 0.0F).color(colorR, colorG, colorB, colorA).uv(glyph.u1, glyph.v0).uv2(uv2).endVertex();
        } else {
            current = 8 + (current / 2);
            Color color1 = calculateColor(current);
            Color color2 = calculateColor(current * current);
            Color color3 = calculateColor(current * current * current);
            vertexConsumer.vertex(matrix4f, $$11 + $$17, $$15, 0.0F).color(color1.getRed(), color1.getGreen(), color1.getBlue(), color1.getAlpha()).uv(glyph.u0, glyph.v0).uv2(uv2).endVertex();
            vertexConsumer.vertex(matrix4f, $$11 + $$18, $$16, 0.0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).uv(glyph.u0, glyph.v1).uv2(uv2).endVertex();
            vertexConsumer.vertex(matrix4f, $$12 + $$18, $$16, 0.0F).color(color3.getRed(), color3.getGreen(), color3.getBlue(), color3.getAlpha()).uv(glyph.u1, glyph.v1).uv2(uv2).endVertex();
            vertexConsumer.vertex(matrix4f, $$12 + $$17, $$15, 0.0F).color(color2.getRed(), color2.getGreen(), color2.getBlue(), color2.getAlpha()).uv(glyph.u1, glyph.v0).uv2(uv2).endVertex();
        }
    }

    public static Color calculateColor(int index) {
        return Color.getHSBColor(
        (
                (tick + index) % 720.0f >= 360.0f
                ? 720.0f - (tick + index) % 720.0f
                : (tick + index) % 720.0f
        ) / 100.0f, 1.0f, 0.8f);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            tick += 1F;
            if (tick >= 720.0f) {
                tick = 0.0F;
            }
        }
    }
}
