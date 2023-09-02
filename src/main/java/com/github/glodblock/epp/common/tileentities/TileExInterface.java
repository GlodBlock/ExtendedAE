package com.github.glodblock.epp.common.tileentities;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileExInterface extends InterfaceBlockEntity {

    public TileExInterface(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileExInterface.class, TileExInterface::new, EPPItemAndBlock.EX_INTERFACE), pos, blockState);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getItemFromBlockEntity().asItem(), 18);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(ContainerExInterface.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExInterface.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.EX_INTERFACE);
    }

}
