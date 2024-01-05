package com.glodblock.github.extendedae.common.tileentities;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocator;
import com.glodblock.github.extendedae.api.IPage;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import com.glodblock.github.extendedae.container.ContainerExInterface;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileExInterface extends InterfaceBlockEntity implements IPage {

    private int page = 0;

    public TileExInterface(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileExInterface.class, TileExInterface::new, EPPItemAndBlock.EX_INTERFACE), pos, blockState);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getItemFromBlockEntity().asItem(), 36);
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

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }
}
