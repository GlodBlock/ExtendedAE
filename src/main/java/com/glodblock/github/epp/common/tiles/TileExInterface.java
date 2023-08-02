package com.glodblock.github.epp.common.tiles;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.epp.common.EPPItemAndBlock;
import com.glodblock.github.epp.container.ContainerExInterface;
import com.glodblock.github.epp.util.Ae2Reflect;
import com.glodblock.github.epp.util.mixinutil.ExtendedInterfaceLogic;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class TileExInterface extends InterfaceBlockEntity {

    public static final BlockEntityType<TileExInterface> TYPE = BlockEntityType.Builder.create(TileExInterface::new, EPPItemAndBlock.EX_INTERFACE).build(null);

    public TileExInterface(BlockPos pos, BlockState blockState) {
        super(TYPE, pos, blockState);
        var l = new InterfaceLogic(this.getMainNode(), this, EPPItemAndBlock.EX_INTERFACE.asItem());
        ((ExtendedInterfaceLogic) l).extend();
        Ae2Reflect.setInterfaceLogic(this, l);
    }

    @Override
    public void openMenu(PlayerEntity player, MenuLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(PlayerEntity player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.EX_INTERFACE);
    }

}
