package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.ExtendedAE;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import com.glodblock.github.extendedae.util.WirelessHelpers;
import com.mojang.datafixers.util.Pair;
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
        if (world instanceof ServerLevel) {
            var item = stack.getItem();

            if (item == EPPItemAndBlock.WIRELESS_TOOL) {
                var nbt = stack.getOrCreateTag();

                if (nbt.getLong("freq") != 0) {
                    return WirelessHelpers.connectWireless(tile::setFrequency, nbt, world, thisPos, p, () -> stack.setTag(null));
                } else {
                    WirelessHelpers.bindWireless(nbt, tile.getNewFreq(), GlobalPos.of(world.dimension(), thisPos));
                    p.displayClientMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            } else if (item == EPPItemAndBlock.WIRELESS_ADVANCED_TOOL) {
                var nbt = stack.getOrCreateTag();

                if (nbt.getBoolean("addMode")) {
                    if (!nbt.contains("connections")) nbt.put("connections", new ListTag());

                    var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);
                    var duplicate = false;

                    for (int i = 0; i < connections.size(); i++) {
                        var connection = connections.getCompound(i);

                        var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, connection.get("bind"))
                            .resultOrPartial(Util.prefix("Connector position", ExtendedAE.LOGGER::error))
                            .map(Pair::getFirst)
                            .orElse(null);

                        if (globalPos.pos().equals(thisPos) && globalPos.dimension().equals(world.dimension())) {
                            duplicate = true;

                            break;
                        }
                    }

                    if (duplicate) {
                        p.displayClientMessage(Component.translatable("chat.wireless_advanced_duplicate"), true);
                    } else {
                        CompoundTag newConnection = new CompoundTag();

                        WirelessHelpers.bindWireless(newConnection, tile.getNewFreq(), GlobalPos.of(world.dimension(), thisPos));

                        connections.add(newConnection);

                        p.displayClientMessage(Component.translatable("chat.wireless_added", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                    }

                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    if (!nbt.contains("connections")) return InteractionResult.FAIL;

                    var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);
                    var firstConnection = connections.getCompound(0);

                    return WirelessHelpers.connectWireless(tile::setFrequency, firstConnection, world, thisPos, p, () -> connections.remove(0));
                }
            }
        }

        return null;
    }

    @Override
    public void openGui(TileWirelessConnector tile, Player p) {
        MenuOpener.open(ContainerWirelessConnector.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
