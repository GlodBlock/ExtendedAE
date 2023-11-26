package com.github.glodblock.extendedae.common.tileentities;

import appeng.api.storage.cells.CellState;
import appeng.blockentity.storage.DriveBlockEntity;
import appeng.core.AELog;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.github.glodblock.extendedae.common.EAEItemAndBlock;
import com.github.glodblock.extendedae.container.ContainerExDrive;
import com.github.glodblock.extendedae.util.Ae2Reflect;
import com.github.glodblock.extendedae.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class TileExDrive extends DriveBlockEntity {

    public TileExDrive(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileExDrive.class, TileExDrive::new, EAEItemAndBlock.EX_DRIVE), pos, blockState);
    }

    @Override
    public int getCellCount() {
        return 20;
    }

    @Override
    protected void writeToStream(FriendlyByteBuf data) {
        Ae2Reflect.updateDriveClientSideState(this);

        long packedState = 0;
        for (int i = 0; i < getCellCount(); i++) {
            packedState |= ((long) Ae2Reflect.getCellState(this)[i].ordinal()) << (i * 3L);
        }
        if (Ae2Reflect.getClientOnline(this)) {
            packedState |= 1L << 61;
        }
        data.writeLong(packedState);

        for (int i = 0; i < getCellCount(); i++) {
            data.writeVarInt(BuiltInRegistries.ITEM.getId(getCellItem(i)));
        }
    }

    @Override
    protected boolean readFromStream(FriendlyByteBuf data) {
        var changed = false;

        var packedState = data.readLong();
        for (int i = 0; i < getCellCount(); i++) {
            var cellStateOrdinal = (packedState >> (i * 3)) & 0b111;
            var cellState = CellState.values()[(int) cellStateOrdinal];
            if (Ae2Reflect.getCellState(this)[i] != cellState) {
                Ae2Reflect.getCellState(this)[i] = cellState;
                changed = true;
            }
        }

        var online = (packedState & (1L << 61)) != 0;
        if (Ae2Reflect.getClientOnline(this) != online) {
            Ae2Reflect.setClientOnline(this, online);
            changed = true;
        }

        for (int i = 0; i < getCellCount(); i++) {
            var itemId = data.readVarInt();
            Item item = itemId == 0 ? null : BuiltInRegistries.ITEM.byId(itemId);
            if (itemId != 0 && item == Items.AIR) {
                AELog.warn("Received unknown item id from server for disk drive %s: %d", this, itemId);
            }
            if (Ae2Reflect.getCellItem(this)[i] != item) {
                Ae2Reflect.getCellItem(this)[i] = item;
                changed = true;
            }
        }

        return changed;
    }

    @Override
    public void openMenu(Player player) {
        MenuOpener.open(ContainerExDrive.TYPE, player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExDrive.TYPE, player, MenuLocators.forBlockEntity(this));
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EAEItemAndBlock.EX_DRIVE);
    }

}
