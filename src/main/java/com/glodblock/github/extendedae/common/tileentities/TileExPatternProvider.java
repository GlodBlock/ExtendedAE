package com.glodblock.github.extendedae.common.tileentities;

import appeng.api.stacks.AEItemKey;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import com.glodblock.github.extendedae.api.caps.IGenericInvHost;
import com.glodblock.github.extendedae.common.EAESingletons;
import com.glodblock.github.extendedae.container.ContainerExPatternProvider;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class TileExPatternProvider extends PatternProviderBlockEntity implements IGenericInvHost {

    public TileExPatternProvider(BlockPos pos, BlockState blockState) {
        super(GlodUtil.getTileType(TileExPatternProvider.class, TileExPatternProvider::new, EAESingletons.EX_PATTERN_PROVIDER), pos, blockState);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(this.getMainNode(), this, 36);
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        MenuOpener.open(ContainerExPatternProvider.TYPE, player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.returnTo(ContainerExPatternProvider.TYPE, player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(EAESingletons.EX_PATTERN_PROVIDER);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(EAESingletons.EX_PATTERN_PROVIDER);
    }

    @Override
    public GenericStackInv getGenericInv() {
        return this.getLogic().getReturnInv();
    }

}
