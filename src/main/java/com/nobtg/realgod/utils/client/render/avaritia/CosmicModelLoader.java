package com.nobtg.realgod.utils.client.render.avaritia;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * Name: Avaritia-forge / HaloItemModelLoader
 * Author: cnlimiter
 * CreateTime: 2023/9/18 23:45
 * Description:
 */

public class CosmicModelLoader implements IGeometryLoader<CosmicModelLoader.CosmicGeometry> {
    public static final CosmicModelLoader INSTANCE = new CosmicModelLoader();

    @Override
    public CosmicGeometry read(JsonObject modelContents, JsonDeserializationContext deserializationContext) throws JsonParseException {
        JsonObject cosmicObj = modelContents.getAsJsonObject("cosmic");
        if (cosmicObj == null) {
            throw new IllegalStateException("Missing 'cosmic' object.");
        } else {
            String maskTexture = GsonHelper.getAsString(cosmicObj, "mask");
            JsonObject clean = modelContents.deepCopy();
            clean.remove("cosmic");
            clean.remove("loader");
            BlockModel baseModel = deserializationContext.deserialize(clean, BlockModel.class);
            return new CosmicModelLoader.CosmicGeometry(baseModel, maskTexture);
        }
    }

    public static class CosmicGeometry implements IUnbakedGeometry<CosmicGeometry>{
        private final BlockModel baseModel;
        private final String maskTexture;

        public CosmicGeometry(BlockModel baseModel, String maskTexture) {
            this.baseModel = baseModel;
            this.maskTexture = maskTexture;
        }

        @Override
        public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
            BakedModel baseBakedModel = this.baseModel.bake(baker, this.baseModel, spriteGetter, modelState, modelLocation, true);
            Material maskMaterial = context.getMaterial(maskTexture);
            return new CosmicBakeModel(baseBakedModel, spriteGetter.apply(maskMaterial));
        }
    }
}