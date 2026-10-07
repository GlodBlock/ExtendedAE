package com.glodblock.github.extendedae.client.render.tesr;

import com.glodblock.github.extendedae.client.render.VacuumAreaHandler;
import com.glodblock.github.extendedae.common.tileentities.TileVacuumInterface;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class VacuumInterfaceTESR implements BlockEntityRenderer<TileVacuumInterface> {


    public VacuumInterfaceTESR(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(@NotNull TileVacuumInterface tile, float partialTicks, @NotNull PoseStack ms, @NotNull MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (tile.isDisplayArea()) {
            var area = tile.getWorkArea();
            if (area != null) {
                area = area.inflate(0.001);
                VacuumAreaHandler.addJob(tile.getLevel(), area);
            }
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull TileVacuumInterface tile) {
        return tile.getWorkArea();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileVacuumInterface tile) {
        return true;
    }

}
