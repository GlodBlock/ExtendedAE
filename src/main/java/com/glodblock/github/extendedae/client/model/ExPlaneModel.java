package com.glodblock.github.extendedae.client.model;

import appeng.core.AppEng;
import appeng.parts.automation.PlaneModel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

public class ExPlaneModel implements IUnbakedGeometry<ExPlaneModel> {

    private final PlaneModel delegate;

    public ExPlaneModel(ResourceLocation frontTexture, ResourceLocation sidesTexture, ResourceLocation backTexture) {
        this.delegate = new PlaneModel(frontTexture, sidesTexture, backTexture);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return this.delegate.bake(baker, spriteGetter, modelState, modelLocation);
    }

    public record Loader(ResourceLocation frontTexture) implements IGeometryLoader<ExPlaneModel> {

        @Override
        public ExPlaneModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            return new ExPlaneModel(frontTexture, AppEng.makeId("part/plane_sides"), AppEng.makeId("part/transition_plane_back"));
        }

    }

}
