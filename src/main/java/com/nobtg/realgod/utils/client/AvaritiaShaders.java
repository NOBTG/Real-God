package com.nobtg.realgod.utils.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.nobtg.realgod.ModMain;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AvaritiaShaders {
    private static final float[] COSMIC_UVS = new float[40];
    private static int renderTime;
    private static float renderFrame;
    public static final RenderType COSMIC_RENDER_TYPE = RenderType.create(ModMain.modID + ":cosmic", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder().setShaderState(new RenderStateShard.ShaderStateShard(() -> AvaritiaShaders.cosmicShader)).setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST).setLightmapState(RenderStateShard.LIGHTMAP).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED).createCompositeState(true));
    public static ShaderInstance cosmicShader;
    public static Uniform cosmicTime;
    public static Uniform cosmicYaw;
    public static Uniform cosmicPitch;
    public static Uniform cosmicExternalScale;
    public static Uniform cosmicOpacity;
    public static Uniform cosmicUVs;
    public static final List<Material> textures = new ArrayList<>();

    static {
        for (int i = 0; i <= 9; i++) {
            textures.add(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(ModMain.modID, "shader/cosmic_" + i)));
        }
    }

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(AvaritiaShaders::onRegisterShaders);
        MinecraftForge.EVENT_BUS.addListener(AvaritiaShaders::onRenderTick);
        MinecraftForge.EVENT_BUS.addListener(AvaritiaShaders::clientTick);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(ModMain.modID, "cosmic"), DefaultVertexFormat.BLOCK), (e) -> {
                cosmicShader = e;
                cosmicTime = Objects.requireNonNull(cosmicShader.getUniform("time"));
                cosmicYaw = Objects.requireNonNull(cosmicShader.getUniform("yaw"));
                cosmicPitch = Objects.requireNonNull(cosmicShader.getUniform("pitch"));
                cosmicExternalScale = Objects.requireNonNull(cosmicShader.getUniform("externalScale"));
                cosmicOpacity = Objects.requireNonNull(cosmicShader.getUniform("opacity"));
                cosmicUVs = Objects.requireNonNull(cosmicShader.getUniform("cosmicuvs"));
                cosmicTime.set((float) renderTime + renderFrame);
                cosmicShader.apply();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            renderFrame = event.renderTickTime;

            for (int i = 0; i < textures.size(); ++i) {
                TextureAtlasSprite sprite = textures.get(i).sprite();
                COSMIC_UVS[i * 4] = sprite.getU0();
                COSMIC_UVS[i * 4 + 1] = sprite.getV0();
                COSMIC_UVS[i * 4 + 2] = sprite.getU1();
                COSMIC_UVS[i * 4 + 3] = sprite.getV1();
            }

            if (cosmicUVs != null) {
                cosmicUVs.set(COSMIC_UVS);
            }
        }
    }

    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ++renderTime;
        }
    }
}