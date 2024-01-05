package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class BlockWirelessConnector extends BlockBaseGui<TileWirelessConnector> {

    private static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 16);

    public BlockWirelessConnector() {
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED, false));
        this.registerDefaultState(this.defaultBlockState().setValue(COLOR, 16));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
        builder.add(COLOR);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileWirelessConnector be) {
        return currentState.setValue(CONNECTED, be.isConnected()).setValue(COLOR, be.getColor().ordinal());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        var te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.reactive();
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != state.getBlock()) {
            var te = this.getBlockEntity(level, pos);
            if (te != null) {
                te.breakOnRemove();
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public InteractionResult check(TileWirelessConnector tile, ItemStack stack, Level world, BlockPos thisPos, BlockHitResult hit, Player p) {
        if (stack.getItem() == EPPItemAndBlock.WIRELESS_TOOL && world instanceof ServerLevel server) {
            var nbt = stack.hasTag() ? stack.getTag() : new CompoundTag();
            assert nbt != null;
            if (nbt.getLong("freq") != 0) {
                var f = nbt.getLong("freq");
                if (!nbt.contains("bind")) {
                    p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, nbt.get("bind"))
                        .resultOrPartial(Util.prefix("Connector position", ExtendedAE.LOGGER::error))
                        .map(Pair::getFirst)
                        .orElse(null);
                if (globalPos == null) {
                    p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                var otherPos = globalPos.pos();
                var otherWorld = globalPos.dimension();
                var thisWorld = world.dimension();
                if (otherPos.equals(thisPos) && otherWorld.equals(thisWorld)) {
                    p.displayClientMessage(WirelessFail.SELF_REFERENCE.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                if (!otherWorld.equals(thisWorld)) {
                    p.displayClientMessage(WirelessFail.CROSS_DIMENSION.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                if (Math.sqrt(otherPos.distSqr(thisPos)) > EPPConfig.wirelessMaxRange) {
                    p.displayClientMessage(WirelessFail.OUT_OF_RANGE.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                var otherWorldInstance = server.getServer().getLevel(otherWorld);
                if (otherWorldInstance == null) {
                    p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
                var otherTile = otherWorldInstance.getBlockEntity(otherPos);
                if (otherTile instanceof TileWirelessConnector otherConnector) {
                    otherConnector.setFreq(f);
                    tile.setFreq(f);
                    stack.setTag(null);
                    p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                    return InteractionResult.FAIL;
                }
            } else {
                stack.getOrCreateTag().putLong("freq", tile.getNewFreq());
                var globalPos = GlobalPos.of(world.dimension(), thisPos);
                GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, globalPos)
                        .result()
                        .ifPresent(tag -> stack.getOrCreateTag().put("bind", tag));
                p.displayClientMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return null;
    }

    @Override
    public void openGui(TileWirelessConnector tile, Player p) {
        MenuOpener.open(ContainerWirelessConnector.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
