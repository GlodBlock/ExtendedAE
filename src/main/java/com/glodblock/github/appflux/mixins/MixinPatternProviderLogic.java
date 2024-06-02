package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.AFItemAndBlock;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PatternProviderLogic.class, remap = false)
public abstract class MixinPatternProviderLogic implements IUpgradeableObject, IEnergyDistributor {

    @Unique
    private IUpgradeInventory af_$upgrades = UpgradeInventories.empty();
    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Final
    @Shadow
    private IManagedGridNode mainNode;

    @Final
    @Shadow
    private IActionSource actionSource;

    @Unique
    private void af_$onUpgradesChanged() {
        this.host.saveChanges();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.af_$upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void initUpgrade(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        af_$upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::af_$onUpgradesChanged);
        this.mainNode.addService(IEnergyDistributor.class, this);
    }

    @Inject(
            method = "writeToNBT",
            at = @At("TAIL")
    )
    private void saveUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.af_$upgrades.writeToNBT(tag, "upgrades");
    }

    @Inject(
            method = "readFromNBT",
            at = @At("TAIL")
    )
    private void loadUpgrade(CompoundTag tag, CallbackInfo ci) {
        this.af_$upgrades.readFromNBT(tag, "upgrades");
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void dropUpgrade(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.af_$upgrades) {
            if (!is.isEmpty()) {
                drops.add(is);
            }
        }
    }

    @Inject(
            method = "clearContent",
            at = @At("TAIL")
    )
    private void clearUpgrade(CallbackInfo ci) {
        this.af_$upgrades.clear();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void distribute() {
        if (this.af_$upgrades.isInstalled(AFItemAndBlock.INDUCTION_CARD)) {
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
