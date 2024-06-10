package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InterfaceLogic.class)
public abstract class MixinInterfaceLogic {

    @Final
    @Mutable
    @Shadow(remap = false)
    private IUpgradeInventory upgrades;

    @Shadow(remap = false)
    protected abstract void onUpgradesChanged();

    @Final
    @Shadow(remap = false)
    protected InterfaceLogicHost host;

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
            at = @At("TAIL"),
            remap = false
    )
    private void expendUpgrades(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci) {
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
    }

    @Inject(
            method = "onUpgradesChanged",
            at = @At("TAIL"),
            remap = false
    )
    private void notifyUpgrade(CallbackInfo ci) {
        this.host.getBlockEntity().invalidateCapabilities();
    }

}
