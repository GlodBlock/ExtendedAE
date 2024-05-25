package com.glodblock.github.extendedae.xmod.appliede.tileentities;

import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.xmod.appliede.containers.ContainerExEMCInterface;
import com.glodblock.github.glodium.util.GlodUtil;
import gripe._90.appliede.block.EMCInterfaceBlockEntity;
import gripe._90.appliede.me.misc.EMCInterfaceLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileExEMCInterface extends EMCInterfaceBlockEntity {

    public TileExEMCInterface(BlockPos pos, BlockState state) {
        super(GlodUtil.getTileType(TileExEMCInterface.class, TileExEMCInterface::new, EPPItemAndBlock.EX_EMC_INTERFACE), pos, state);
    }

    @Override
    protected EMCInterfaceLogic createLogic() {
        return new EMCInterfaceLogic(getMainNode(), this, this.getItemFromBlockEntity(), 36);
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        MenuOpener.open(ContainerExEMCInterface.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExEMCInterface.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EPPItemAndBlock.EX_EMC_INTERFACE);
    }

}
