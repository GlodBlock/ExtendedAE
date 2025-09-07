package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
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
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlockWirelessHub extends BlockBaseGui<TileWirelessHub> {
    private static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BlockWirelessHub() {
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileWirelessHub be) {
        return currentState.setValue(CONNECTED, be.isConnected());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        var te = this.getBlockEntity(level, pos);
        if (te != null) {
            for (int i = 0; i < TileWirelessHub.MAX_PORT; i ++) {
                te.reactive(i);
            }
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
    public InteractionResult check(TileWirelessHub tile, ItemStack stack, Level world, BlockPos thisPos, BlockHitResult hit, Player p) {
        if (stack.getItem() == EPPItemAndBlock.WIRELESS_TOOL && world instanceof ServerLevel server) {
            var port = tile.allocatePort();
            if (port < 0) {
                p.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);
                return InteractionResult.FAIL;
            }
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
                    otherConnector.setFrequency(f);
                    tile.setFrequency(f, port);
                    stack.setTag(null);
                    p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                    return InteractionResult.sidedSuccess(world.isClientSide);
                } if (otherTile instanceof TileWirelessHub otherHub) {
                    int otherPort = otherHub.allocatePort();
                    if (otherPort < 0) {
                        p.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);
                        return InteractionResult.FAIL;
                    } else {
                        otherHub.setFrequency(f, otherPort);
                        tile.setFrequency(f, port);
                        stack.setTag(null);
                        p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }
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
    public void openGui(TileWirelessHub tile, Player p) {
        MenuOpener.open(ContainerWirelessHub.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
