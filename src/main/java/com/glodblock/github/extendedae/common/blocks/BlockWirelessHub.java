package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import com.glodblock.github.extendedae.util.WirelessChecks;
import com.mojang.datafixers.util.Pair;
import javax.annotation.Nonnull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

public class BlockWirelessHub extends BlockBaseGui<TileWirelessHub> {

    private static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 16);

    public BlockWirelessHub() {
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
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileWirelessHub be) {
        return currentState.setValue(CONNECTED, be.isConnected()).setValue(COLOR, be.getColor().ordinal());
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
        if (world instanceof ServerLevel server) {
            var item = stack.getItem();

            if (item == EPPItemAndBlock.WIRELESS_TOOL) {
                if (WirelessChecks.hasNoPorts(tile, p)) return InteractionResult.FAIL;

                var nbt = stack.getOrCreateTag();

                if (nbt.getLong("freq") != 0) {
                    if (WirelessChecks.hasNoNBT(nbt, p)) return InteractionResult.FAIL;

                    var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, nbt.get("bind"))
                        .resultOrPartial(Util.prefix("Connector position", ExtendedAE.LOGGER::error))
                        .map(Pair::getFirst)
                        .orElse(null);

                    if (WirelessChecks.hasNoGlobalPos(globalPos, p)) return InteractionResult.FAIL;
                    if (WirelessChecks.positionChecks(globalPos, thisPos, world, server, p)) return InteractionResult.FAIL;

                    var otherWorldInstance = server.getServer().getLevel(globalPos.dimension());
                    var otherTile = otherWorldInstance.getBlockEntity(globalPos.pos());
                    var freq = nbt.getLong("freq");
                    var port = tile.allocatePort();

                    if (otherTile instanceof TileWirelessConnector otherConnector) {
                        otherConnector.setFrequency(freq);
                        tile.setFrequency(freq, port);
                        stack.setTag(null);
                        p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                        return InteractionResult.sidedSuccess(world.isClientSide);
                    } else if (otherTile instanceof TileWirelessHub otherHub) {
                        int otherPort = otherHub.allocatePort();

                        if (WirelessChecks.hasNoPorts(otherHub, p)) return InteractionResult.FAIL;

                        otherHub.setFrequency(freq, otherPort);
                            tile.setFrequency(freq, port);
                            stack.setTag(null);
                            p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                            return InteractionResult.sidedSuccess(world.isClientSide);
                    } else {
                        p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

                        return InteractionResult.FAIL;
                    }
                } else {
                    nbt.putLong("freq", tile.getNewFreq());

                    GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, GlobalPos.of(world.dimension(), thisPos))
                        .result()
                        .ifPresent(tag -> stack.getOrCreateTag().put("bind", tag));

                    p.displayClientMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            } else if (item == EPPItemAndBlock.WIRELESS_ADVANCED_TOOL) {
                if (WirelessChecks.hasNoPorts(tile, p)) return InteractionResult.FAIL;

                var nbt = stack.getOrCreateTag();

                if (nbt.getBoolean("addMode")) {
                    CompoundTag newConnection = new CompoundTag();

                    newConnection.putLong("freq", tile.getNewFreq());

                    GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, GlobalPos.of(world.dimension(), thisPos))
                        .result()
                        .ifPresent(tag -> newConnection.put("bind", tag));

                    if (!nbt.contains("connections")) nbt.put("connections", new ListTag());

                    nbt.getList("connections", CompoundTag.TAG_COMPOUND).add(newConnection);

                    p.displayClientMessage(Component.translatable("chat.wireless_added", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    if (!nbt.contains("connections")) return InteractionResult.FAIL;

                    var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);
                    var firstConnection = connections.getCompound(0);

                    if (WirelessChecks.hasNoNBT(firstConnection, p)) return InteractionResult.FAIL;

                    var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, firstConnection.get("bind"))
                        .resultOrPartial(Util.prefix("Connector position", ExtendedAE.LOGGER::error))
                        .map(Pair::getFirst)
                        .orElse(null);

                    if (WirelessChecks.hasNoGlobalPos(globalPos, p)) return InteractionResult.FAIL;
                    if (WirelessChecks.positionChecks(globalPos, thisPos, world, server, p)) return InteractionResult.FAIL;

                    var otherWorldInstance = server.getServer().getLevel(globalPos.dimension());
                    var otherTile = otherWorldInstance.getBlockEntity(globalPos.pos());
                    var freq = firstConnection.getLong("freq");
                    var port = tile.allocatePort();

                    if (otherTile instanceof TileWirelessConnector otherConnector) {
                        otherConnector.setFrequency(freq);
                        tile.setFrequency(freq, port);
                        connections.remove(0);
                        p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                        return InteractionResult.sidedSuccess(world.isClientSide);
                    } else if (otherTile instanceof TileWirelessHub otherHub) {
                        int otherPort = otherHub.allocatePort();

                        if (WirelessChecks.hasNoPorts(otherHub, p)) return InteractionResult.FAIL;

                        otherHub.setFrequency(freq, otherPort);
                        tile.setFrequency(freq, port);
                        connections.remove(0);
                        p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                        return InteractionResult.sidedSuccess(world.isClientSide);
                    } else {
                        p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

                        return InteractionResult.FAIL;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void openGui(TileWirelessHub tile, Player p) {
        MenuOpener.open(ContainerWirelessHub.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
