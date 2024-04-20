package com.nobtg.realgod.utils.client.render.avaritia;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.nobtg.realgod.ModMain;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Objects;

public class AvaritiaShaders {
    private static final float[] COSMIC_UVS = new float[40];
    private static int renderTime;
    private static float renderFrame;
    public static RenderType COSMIC_RENDER_TYPE;
    public static CCShaderInstance cosmicShader;
    public static CCUniform cosmicTime;
    public static CCUniform cosmicYaw;
    public static CCUniform cosmicPitch;
    public static CCUniform cosmicExternalScale;
    public static CCUniform cosmicOpacity;
    public static CCUniform cosmicUVs;
    public static TextureAtlas sprites;

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(AvaritiaShaders::onRegisterShaders);
        bus.addListener(AvaritiaShaders::onTextureStitchPost);
        bus.addListener(AvaritiaShaders::registerLoaders);
        MinecraftForge.EVENT_BUS.addListener(AvaritiaShaders::onRenderTick);
        MinecraftForge.EVENT_BUS.addListener(AvaritiaShaders::onClientTick);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceProvider(), new ResourceLocation(ModMain.modID, "cosmic"), DefaultVertexFormat.BLOCK), (e) -> {
            cosmicShader = (CCShaderInstance) e;
            ;
            cosmicTime = Objects.requireNonNull(cosmicShader.getUniform("time"));
            cosmicYaw = Objects.requireNonNull(cosmicShader.getUniform("yaw"));
            cosmicPitch = Objects.requireNonNull(cosmicShader.getUniform("pitch"));
            cosmicExternalScale = Objects.requireNonNull(cosmicShader.getUniform("externalScale"));
            cosmicOpacity = Objects.requireNonNull(cosmicShader.getUniform("opacity"));
            cosmicUVs = Objects.requireNonNull(cosmicShader.getUniform("cosmicuvs"));
            cosmicShader.onApply(() -> cosmicTime.glUniform1f((float) renderTime + renderFrame));
        });
    }

    private static void onRenderTick(RenderTickEvent event) {
        if (event.phase == Phase.START) {
            for (int i = 0; i < 10; ++i) {
                ResourceLocation res = new ResourceLocation(ModMain.modID, "shader/cosmic_" + i);
                TextureAtlasSprite sprite = sprites.getSprite(res);
                COSMIC_UVS[i * 4] = sprite.getU0();
                COSMIC_UVS[i * 4 + 1] = sprite.getV0();
                COSMIC_UVS[i * 4 + 2] = sprite.getU1();
                COSMIC_UVS[i * 4 + 3] = sprite.getV1();
            }

            if (cosmicUVs != null) {
                cosmicUVs.glUniformF(false, COSMIC_UVS);
            }

            renderFrame = event.renderTickTime;
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ++renderTime;
        }
    }

    public static void registerLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("cosmic", CosmicModelLoader.INSTANCE);
    }

    public static void onTextureStitchPost(TextureStitchEvent.Post event) {
        sprites = event.getAtlas();
    }
}