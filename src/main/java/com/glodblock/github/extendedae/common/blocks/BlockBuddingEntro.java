package com.glodblock.github.extendedae.common.blocks;

import appeng.block.AEBaseBlock;
import appeng.core.definitions.AEBlocks;
import com.glodblock.github.extendedae.api.ISpecialDrop;
import com.glodblock.github.extendedae.common.EAESingletons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockBuddingEntro extends AEBaseBlock implements ISpecialDrop {

    public static final int GROWTH_CHANCE = 3;
    public static final int DECAY_CHANCE = 10;
    private static final Direction[] DIRECTIONS = Direction.values();

    public BlockBuddingEntro(Properties props) {
        super(stoneProps(props).strength(3, 8).requiresCorrectToolForDrops().randomTicks());
    }

    @Override
    public PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(GROWTH_CHANCE) != 0) {
            return;
        }
        // Try to grow cluster
        Direction direction = Util.getRandom(DIRECTIONS, randomSource);
        BlockPos targetPos = pos.relative(direction);
        BlockState targetState = level.getBlockState(targetPos);
        Block newCluster;
        if (canClusterGrowAtState(targetState)) {
            newCluster = EAESingletons.ENTRO_BUD_SMALL.get();
        } else {
            newCluster = canClusterGrow(targetState, direction);
        }
        if (newCluster == null) {
            return;
        }
        // Grow entro crystal
        BlockState newClusterState = newCluster.defaultBlockState()
                .setValue(AmethystClusterBlock.FACING, direction)
                .setValue(AmethystClusterBlock.WATERLOGGED, targetState.getFluidState().getType() == Fluids.WATER);
        level.setBlockAndUpdate(targetPos, newClusterState);

        if (randomSource.nextInt(DECAY_CHANCE) != 0) {
            return;
        }

        Block newBlock = this.degradeBudding();
        level.setBlockAndUpdate(pos, newBlock.defaultBlockState());
    }

    public static boolean canClusterGrowAtState(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }

    @Nullable
    public static Block canClusterGrow(BlockState state, Direction side) {
        var cluster = state.getBlock();
        if (cluster instanceof BlockEntroCluster && cluster != EAESingletons.ENTRO_CLUSTER.get()) {
            if (state.getValue(AmethystClusterBlock.FACING) == side) {
                if (cluster == EAESingletons.ENTRO_BUD_SMALL.get()) {
                    return EAESingletons.ENTRO_BUD_MEDIUM.get();
                }
                if (cluster == EAESingletons.ENTRO_BUD_MEDIUM.get()) {
                    return EAESingletons.ENTRO_BUD_LARGE.get();
                }
                if (cluster == EAESingletons.ENTRO_BUD_LARGE.get()) {
                    return EAESingletons.ENTRO_CLUSTER.get();
                }
            }
        }
        return null;
    }

    public Block degradeBudding() {
        if (this == EAESingletons.FULLY_ENTROIZED_FLUIX_BUDDING.get()) {
            return EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get();
        }
        if (this == EAESingletons.MOSTLY_ENTROIZED_FLUIX_BUDDING.get()) {
            return EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get();
        }
        if (this == EAESingletons.HALF_ENTROIZED_FLUIX_BUDDING.get()) {
            return EAESingletons.HARDLY_ENTROIZED_FLUIX_BUDDING.get();
        }
        return AEBlocks.QUARTZ_BLOCK.block();
    }

}
