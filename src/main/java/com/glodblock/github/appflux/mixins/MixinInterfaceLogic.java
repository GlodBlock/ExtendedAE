package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.InterfaceLogic;
import appeng.helpers.InterfaceLogicHost;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InterfaceLogic.class, remap = false)
public abstract class MixinInterfaceLogic implements IEnergyDistributor {

    @Final
    @Mutable
    @Shadow
    private IUpgradeInventory upgrades;

    @Shadow
    protected abstract void onUpgradesChanged();

    @Final
    @Shadow
    protected InterfaceLogicHost host;

    @Final
    @Shadow
    protected IActionSource actionSource;

    @Final
    @Shadow
    protected IManagedGridNode mainNode;

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/InterfaceLogicHost;Lnet/minecraft/world/item/Item;I)V",
            at = @At("TAIL")
    )
    private void expendUpgrades(IManagedGridNode gridNode, InterfaceLogicHost host, Item is, int slots, CallbackInfo ci) {
        this.upgrades = UpgradeInventories.forMachine(is, 2, this::onUpgradesChanged);
        this.mainNode.addService(IEnergyDistributor.class, this);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void distribute() {
        if (this.upgrades.isInstalled(AFItemAndBlock.INDUCTION_CARD)) {
            var storage = this.af_getStorage();
            var gird = this.mainNode.getGrid();
            var self = this.host.getBlockEntity();
            if (storage != null && self.getLevel() != null) {
                for (var d : AFUtil.getSides(this.host)) {
                    var te = self.getLevel().getBlockEntity(self.getBlockPos().offset(d.getNormal()));
                    var thatGrid = AFUtil.getGrid(te, d.getOpposite());
                    if (te != null && thatGrid != gird && !AFUtil.isBlackListTE(te, d.getOpposite())) {
                        EnergyHandler.send(te, d.getOpposite(), storage, this.actionSource);
                    }
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

}
