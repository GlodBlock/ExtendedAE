package com.glodblock.github.appflux.mixins;

import appeng.api.networking.IGrid;
import appeng.api.networking.IManagedGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import com.glodblock.github.appflux.common.AFSingletons;
import com.glodblock.github.appflux.common.me.energy.EnergyCapCache;
import com.glodblock.github.appflux.common.me.energy.EnergyHandler;
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
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
    private IUpgradeInventory af_upgrades = UpgradeInventories.empty();

    @Unique
    private EnergyCapCache af_cacheApi;

    @Unique
    private EnergyDistributeService af_service;

    @Unique
    private void af_onUpgradesChanged() {
        this.host.saveChanges();
        this.host.getBlockEntity().invalidateCapabilities();
        if (this.af_service != null) {
            if (this.af_upgrades.isInstalled(AFSingletons.INDUCTION_CARD)) {
                this.af_service.wake(this);
            } else {
                this.af_service.sleep(this);
            }
        }
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.af_upgrades;
    }

    @Inject(
            method = "<init>(Lappeng/api/networking/IManagedGridNode;Lappeng/helpers/patternprovider/PatternProviderLogicHost;I)V",
            at = @At("TAIL")
    )
    private void initUpgrade(IManagedGridNode mainNode, PatternProviderLogicHost host, int patternInventorySize, CallbackInfo ci) {
        af_upgrades = UpgradeInventories.forMachine(host.getTerminalIcon().getItem(), 1, this::af_onUpgradesChanged);
        this.mainNode.addService(IEnergyDistributor.class, this);
    }

    @Inject(
            method = "writeToNBT",
            at = @At("TAIL")
    )
    private void saveUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.af_upgrades.writeToNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "readFromNBT",
            at = @At("TAIL")
    )
    private void loadUpgrade(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        this.af_upgrades.readFromNBT(tag, "upgrades", registries);
    }

    @Inject(
            method = "addDrops",
            at = @At("TAIL")
    )
    private void dropUpgrade(List<ItemStack> drops, CallbackInfo ci) {
        for (var is : this.af_upgrades) {
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
        this.af_upgrades.clear();
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
        if (this.af_upgrades.isInstalled(AFSingletons.INDUCTION_CARD)) {
            var storage = this.af_getStorage();
            if (storage != null) {
                for (var d : AFUtil.getSides(this.host)) {
                    EnergyHandler.send(this.af_cacheApi, d, storage, this.actionSource);
                }
            }
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setServiceHost(@Nullable EnergyDistributeService service) {
        this.af_service = service;
        if (this.af_service != null) {
            if (this.af_upgrades.isInstalled(AFSingletons.INDUCTION_CARD)) {
                this.af_service.wake(this);
            } else {
                this.af_service.sleep(this);
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
