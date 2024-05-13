package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.tileentities.TileIngredientBuffer;
import com.glodblock.github.extendedae.container.ContainerIngredientBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockIngredientBuffer extends BlockBaseGui<TileIngredientBuffer> {

    public BlockIngredientBuffer() {
        super(glassProps().noOcclusion().isViewBlocking((a, b, c) -> false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos) {
        return 0.5f;
    }

    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos p4) {
        return true;
    }

    @Override
    public void openGui(TileIngredientBuffer tile, Player p) {
        MenuOpener.open(ContainerIngredientBuffer.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
