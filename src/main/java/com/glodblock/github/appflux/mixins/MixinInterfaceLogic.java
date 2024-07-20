package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.energy.EnergyTicker;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Final
    @Shadow(remap = false)
    protected IManagedGridNode mainNode;

    @Final
    @Shadow(remap = false)
    protected IActionSource actionSource;

    @Unique
    private EnergyTicker af_ticker;

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
            at = @At("TAIL"),
            remap = false
    )
    private void expendUpgrades(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci) {
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
        this.af_ticker = new EnergyTicker(this.host::getBlockEntity, this.host, () -> this.upgrades.isInstalled(AFSingletons.INDUCTION_CARD), this.mainNode, this.actionSource);
        this.mainNode.addService(IEnergyDistributor.class, this.af_ticker);
    }

    @Inject(
            method = "onUpgradesChanged",
            at = @At("TAIL"),
            remap = false
    )
    private void notifyUpgrade(CallbackInfo ci) {
        this.host.getBlockEntity().invalidateCapabilities();
        this.af_ticker.updateSleep();
    }

}
