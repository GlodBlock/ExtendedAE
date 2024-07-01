package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.server.level.ServerLevel;
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
public abstract class MixinInterfaceLogic implements IEnergyDistributor {

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
    private EnergyCapCache af_cacheApi;

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
            at = @At("TAIL"),
            remap = false
    )
    private void expendUpgrades(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci) {
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
        this.mainNode.addService(IEnergyDistributor.class, this);
    }

    @Inject(
            method = "onUpgradesChanged",
            at = @At("TAIL"),
            remap = false
    )
    private void notifyUpgrade(CallbackInfo ci) {
        this.host.getBlockEntity().invalidateCapabilities();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void distribute() {
        var self = this.host.getBlockEntity();
        if (self.getLevel() == null) {
            return;
        }
        if (this.af_cacheApi == null) {
            this.af_initCache();
        }
        if (this.upgrades.isInstalled(AFSingletons.INDUCTION_CARD)) {
            var storage = this.af_getStorage();
            if (storage != null) {
                for (var d : AFUtil.getSides(this.host)) {
                    EnergyHandler.send(this.af_cacheApi, d, storage, this.actionSource);
                }
            }
        }
    }

    @Unique
    private IStorageService af_getStorage() {
        if (this.mainNode.getGrid() != null) {
            return this.mainNode.getGrid().getStorageService();
        }
        return null;
    }

    @Unique
    private void af_initCache() {
        this.af_cacheApi = new EnergyCapCache((ServerLevel) this.host.getBlockEntity().getLevel(), this.host.getBlockEntity().getBlockPos(), this::af_getGrid);
    }

    @Unique
    private IGrid af_getGrid() {
        return this.mainNode.getGrid();
    }

}
