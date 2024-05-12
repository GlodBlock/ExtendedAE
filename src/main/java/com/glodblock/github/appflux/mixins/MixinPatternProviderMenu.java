package com.glodblock.github.appflux.mixins;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ToolboxMenu;
import appeng.menu.implementations.PatternProviderMenu;
import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternProviderMenu.class)
public abstract class MixinPatternProviderMenu extends AEBaseMenu implements IUpgradableMenu {

    @Final
    @Shadow(remap = false)
    protected PatternProviderLogic logic;
    @Unique
    private ToolboxMenu af_$toolbox;

    @Inject(
            method = "<init>(Lnet/minecraft/world/inventory/MenuType;ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/patternprovider/PatternProviderLogicHost;)V",
            at = @At("TAIL"),
            remap = false
    )
    private void initToolbox(MenuType menuType, int id, Inventory playerInventory, PatternProviderLogicHost host, CallbackInfo ci) {
        this.af_$toolbox = new ToolboxMenu(this);
        this.setupUpgrades(((IUpgradeableObject) host).getUpgrades());
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public ToolboxMenu getToolbox() {
        return this.af_$toolbox;
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public IUpgradeInventory getUpgrades() {
        return ((IUpgradeableObject) this.logic).getUpgrades();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean hasUpgrade(ItemLike upgradeCard) {
        return getUpgrades().isInstalled(upgradeCard);
    }

    @Inject(
            method = "broadcastChanges",
            at = @At("TAIL")
    )
    public void tickToolbox(CallbackInfo ci) {
        this.af_$toolbox.tick();
    }

    public MixinPatternProviderMenu(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
        super(menuType, id, playerInventory, host);
    }

}