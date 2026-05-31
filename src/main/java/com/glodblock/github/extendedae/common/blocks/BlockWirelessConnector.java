package com.glodblock.github.extendedae.common.blocks;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.common.me.wireless.WirelessFail;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessConnector;
import com.glodblock.github.extendedae.common.tileentities.TileWirelessHub;
import com.glodblock.github.extendedae.config.EAEConfig;
import com.glodblock.github.extendedae.container.ContainerWirelessConnector;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
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
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;

public class BlockWirelessConnector extends BlockBaseGui<TileWirelessConnector> {

    private static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    private static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 16);

    public BlockWirelessConnector(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CONNECTED, false));
        this.registerDefaultState(this.defaultBlockState().setValue(COLOR, 16));
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<@NotNull Block, @NotNull BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
        builder.add(COLOR);
    }

    @Override
    protected BlockState updateBlockStateFromBlockEntity(BlockState currentState, TileWirelessConnector be) {
        return currentState.setValue(CONNECTED, be.isConnected()).setValue(COLOR, be.getColor().ordinal());
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        var te = this.getBlockEntity(level, pos);
        if (te != null) {
            te.reactive();
        }
    }

    @Override
    public InteractionResult check(TileWirelessConnector tile, ItemStack stack, Level world, BlockPos thisPos, BlockHitResult hit, Player p) {
        if (stack.getItem() == EAESingletons.WIRELESS_TOOL.get() && world instanceof ServerLevel server) {
            var locator = stack.get(EAESingletons.WIRELESS_LOCATOR);
            if (locator != null) {
                var f = locator.left();
                var globalPos = locator.right();
                if (f != 0) {
                    if (globalPos == null) {
                        p.sendOverlayMessage(WirelessFail.MISSING.getTranslation());
                        return InteractionResult.FAIL;
                    }
                    var otherPos = globalPos.pos();
                    var otherWorld = globalPos.dimension();
                    var thisWorld = world.dimension();
                    if (otherPos.equals(thisPos) && otherWorld.equals(thisWorld)) {
                        p.sendOverlayMessage(WirelessFail.SELF_REFERENCE.getTranslation());
                        return InteractionResult.FAIL;
                    }
                    if (!otherWorld.equals(thisWorld)) {
                        p.sendOverlayMessage(WirelessFail.CROSS_DIMENSION.getTranslation());
                        return InteractionResult.FAIL;
                    }
                    if (Math.sqrt(otherPos.distSqr(thisPos)) > EAEConfig.wirelessMaxRange) {
                        p.sendOverlayMessage(WirelessFail.OUT_OF_RANGE.getTranslation());
                        return InteractionResult.FAIL;
                    }
                    var otherWorldInstance = server.getServer().getLevel(otherWorld);
                    if (otherWorldInstance == null) {
                        p.sendOverlayMessage(WirelessFail.MISSING.getTranslation());
                        return InteractionResult.FAIL;
                    }
                    var otherTile = otherWorldInstance.getBlockEntity(otherPos);
                    if (otherTile instanceof TileWirelessConnector otherConnector) {
                        otherConnector.setFrequency(f);
                        tile.setFrequency(f);
                        stack.remove(EAESingletons.WIRELESS_LOCATOR);
                        p.sendOverlayMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()));
                        return InteractionResult.SUCCESS;
                    } if (otherTile instanceof TileWirelessHub otherHub) {
                        int port = otherHub.allocatePort();
                        if (port < 0) {
                            p.sendOverlayMessage(WirelessFail.OUT_OF_PORT.getTranslation());
                            return InteractionResult.FAIL;
                        } else {
                            otherHub.setFrequency(f, port);
                            tile.setFrequency(f);
                            stack.remove(EAESingletons.WIRELESS_LOCATOR);
                            p.sendOverlayMessage(Component.translatable("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()));
                            return InteractionResult.SUCCESS;
                        }
                    } else {
                        p.sendOverlayMessage(WirelessFail.MISSING.getTranslation());
                        return InteractionResult.FAIL;
                    }
                }
            }
            var freq = tile.getNewFreq();
            var globalPos = GlobalPos.of(world.dimension(), thisPos);
            stack.set(EAESingletons.WIRELESS_LOCATOR, Pair.of(freq, globalPos));
            p.sendOverlayMessage(Component.translatable("chat.wireless_bind", thisPos.getX(), thisPos.getY(), thisPos.getZ()));
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    @Override
    public void openGui(TileWirelessConnector tile, Player p) {
        MenuOpener.open(ContainerWirelessConnector.TYPE, p, MenuLocators.forBlockEntity(tile));
    }

}
