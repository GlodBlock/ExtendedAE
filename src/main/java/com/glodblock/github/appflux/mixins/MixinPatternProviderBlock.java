package com.glodblock.github.appflux.mixins;

import appeng.block.AEBaseBlock;
import appeng.block.crafting.PatternProviderBlock;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderBlock.class)
public abstract class MixinPatternProviderBlock extends AEBaseBlock {

    @Inject(
            method = "neighborChanged",
            at = @At("HEAD")
    )
    private void injectChange(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, CallbackInfo ci) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PatternProviderLogicHost te) {
            AFUtil.notifyNeighbor((INeighborListener) te.getLogic(), pos, fromPos);
        }
    }

    @Override
    public void onNeighborChange(BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PatternProviderLogicHost te) {
            AFUtil.notifyNeighbor((INeighborListener) te.getLogic(), pos, neighbor);
        }
    }

    protected MixinPatternProviderBlock(Properties props) {
        super(props);
    }

}
