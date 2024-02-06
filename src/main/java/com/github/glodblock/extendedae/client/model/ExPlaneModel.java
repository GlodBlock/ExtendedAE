package com.github.glodblock.extendedae.client.model;

import appeng.client.render.BasicUnbakedModel;
import appeng.core.AppEng;
import appeng.parts.automation.PlaneModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ExPlaneModel implements BasicUnbakedModel {

    private final PlaneModel delegate;

    public ExPlaneModel(ResourceLocation frontTexture) {
        this.delegate = new PlaneModel(frontTexture, AppEng.makeId("part/plane_sides"), AppEng.makeId("part/transition_plane_back"));
    }

    @Override
    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        return this.delegate.bake(baker, spriteGetter, modelTransform, modelLocation);
    }

}
