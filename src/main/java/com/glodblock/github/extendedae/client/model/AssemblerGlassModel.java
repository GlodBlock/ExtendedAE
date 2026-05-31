package com.glodblock.github.extendedae.client.model;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.jetbrains.annotations.NotNull;

public class AssemblerGlassModel implements CustomUnbakedBlockStateModel {

    public static final MapCodec<AssemblerGlassModel> MAP_CODEC = MapCodec.unit(AssemblerGlassModel::new);

    @Override
    public @NotNull BlockStateModel bake(@NotNull ModelBaker baker) {
        return new AssemblerGlassBakedModel(baker.materials());
    }

    @Override
    public void resolveDependencies(@NotNull Resolver resolver) {

    }

    @Override
    public @NotNull MapCodec<AssemblerGlassModel> codec() {
        return MAP_CODEC;
    }

}
