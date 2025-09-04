package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerWirelessHub;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

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
    public ItemInteractionResult check(TileWirelessHub tile, ItemStack stack, Level world, BlockPos thisPos, BlockHitResult hit, Player p) {
        if (stack.getItem() == EAESingletons.WIRELESS_TOOL && world instanceof ServerLevel server) {
            var locator = stack.get(EAESingletons.WIRELESS_LOCATOR);
            int port = tile.allocatePort();
            if (port < 0) {
                p.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);
                return ItemInteractionResult.FAIL;
            }
            if (locator != null) {
                var f = locator.left();
                var globalPos = locator.right();
                if (f != 0) {
                    if (globalPos == null) {
                        p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                    var otherPos = globalPos.pos();
                    var otherWorld = globalPos.dimension();
                    var thisWorld = world.dimension();
                    if (otherPos.equals(thisPos) && otherWorld.equals(thisWorld)) {
                        p.displayClientMessage(WirelessFail.SELF_REFERENCE.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                    if (!otherWorld.equals(thisWorld)) {
                        p.displayClientMessage(WirelessFail.CROSS_DIMENSION.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                    if (Math.sqrt(otherPos.distSqr(thisPos)) > EAEConfig.wirelessMaxRange) {
                        p.displayClientMessage(WirelessFail.OUT_OF_RANGE.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                    var otherWorldInstance = server.getServer().getLevel(otherWorld);
                    if (otherWorldInstance == null) {
                        p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                    var otherTile = otherWorldInstance.getBlockEntity(otherPos);
                    if (otherTile instanceof TileWirelessConnector otherConnector) {
                        otherConnector.setFrequency(f);
                        tile.setFrequency(f, port);
                        stack.remove(EAESingletons.WIRELESS_LOCATOR);
                        p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                        return ItemInteractionResult.sidedSuccess(world.isClientSide);
                    } if (otherTile instanceof TileWirelessHub otherHub) {
                        int otherPort = otherHub.allocatePort();
                        if (otherPort < 0) {
                            p.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);
                            return ItemInteractionResult.FAIL;
                        } else {
                            otherHub.setFrequency(f, otherPort);
                            tile.setFrequency(f, port);
                            stack.remove(EAESingletons.WIRELESS_LOCATOR);
                            p.displayClientMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
                            return ItemInteractionResult.sidedSuccess(world.isClientSide);
                        }
                    } else {
                        p.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);
                        return ItemInteractionResult.FAIL;
                    }
                }
            }
            var freq = tile.getNewFreq();
            var globalPos = GlobalPos.of(world.dimension(), thisPos);
            stack.set(EAESingletons.WIRELESS_LOCATOR, Pair.of(freq, globalPos));
            p.displayClientMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }
        return null;
    }

    @Override
    public void openGui(TileWirelessHub tile, Player p) {
        MenuOpener.open(ContainerWirelessHub.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
