package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.config.EPPConfig;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import com.glodblock.github.extendedae.util.WirelessHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
        if (world instanceof ServerLevel) {
            var item = stack.getItem();

            if (item == EPPItemAndBlock.WIRELESS_TOOL) {
                if (WirelessHelpers.hasNoPorts(tile, p)) return InteractionResult.FAIL;

                var nbt = stack.getOrCreateTag();

                if (nbt.getLong("freq") != 0) {
                    return WirelessHelpers.connectWireless(freq -> {
                        var port = tile.allocatePort();

                        tile.setFrequency(freq, port);
                    }, nbt, world, thisPos, p, () -> stack.setTag(null));
                } else {
                    WirelessHelpers.bindWireless(nbt, tile.getNewFreq(), GlobalPos.of(world.dimension(), thisPos));
                    p.displayClientMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                    return InteractionResult.sidedSuccess(world.isClientSide);
                }
            } else if (item == EPPItemAndBlock.WIRELESS_ADVANCED_TOOL) {
                if (WirelessHelpers.hasNoPorts(tile, p)) return InteractionResult.FAIL;

                var nbt = stack.getOrCreateTag();

                if (nbt.getBoolean("addMode")) {
                    if (!nbt.contains("connections")) nbt.put("connections", new ListTag());

                    var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);

                    if (connections.size() >= EPPConfig.wirelessMaxQueueSize) {
                        p.displayClientMessage(Component.translatable("chat.wireless_advanced_queue_limit", EPPConfig.wirelessMaxQueueSize), true);

                        return InteractionResult.sidedSuccess(world.isClientSide);
                    }

                    CompoundTag newConnection = new CompoundTag();

                    WirelessHelpers.bindWireless(newConnection, tile.getNewFreq(), GlobalPos.of(world.dimension(), thisPos));

                    connections.add(newConnection);

                    p.displayClientMessage(Component.translatable("chat.wireless_added", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

                    return InteractionResult.sidedSuccess(world.isClientSide);
                } else {
                    if (!nbt.contains("connections")) return InteractionResult.FAIL;

                    var connections = nbt.getList("connections", CompoundTag.TAG_COMPOUND);
                    var firstConnection = connections.getCompound(0);

                    return WirelessHelpers.connectWireless(freq -> {
                        var port = tile.allocatePort();

                        tile.setFrequency(freq, port);
                    }, firstConnection, world, thisPos, p, () -> connections.remove(0));
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
