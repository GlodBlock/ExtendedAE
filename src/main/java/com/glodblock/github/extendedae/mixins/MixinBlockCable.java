package com.glodblock.github.extendedae.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.cyclops.cyclopscore.block.BlockWithEntity;
import org.cyclops.cyclopscore.blockentity.CyclopsBlockEntity;
import org.cyclops.integrateddynamics.block.BlockCable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.BiFunction;

@Mixin(BlockCable.class)
public abstract class MixinBlockCable extends BlockWithEntity {

    @SuppressWarnings("deprecation")
    @Inject(
            method = "updateShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkLevel(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos, CallbackInfoReturnable<BlockState> cir) {
        if (!(worldIn instanceof Level)) {
            if (stateIn.getValue(BlockCable.WATERLOGGED)) {
                worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
            }
            cir.setReturnValue(super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos));
            cir.cancel();
        }
    }

    public MixinBlockCable(Properties properties, BiFunction<BlockPos, BlockState, CyclopsBlockEntity> blockEntitySupplier) {
        super(properties, blockEntitySupplier);
    }

}
