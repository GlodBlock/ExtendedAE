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
import com.glodblock.github.appflux.common.me.service.EnergyDistributeService;
import com.glodblock.github.appflux.common.me.service.IEnergyDistributor;
import com.glodblock.github.appflux.util.AFUtil;
import com.glodblock.github.appflux.util.helpers.INeighborListener;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
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
public abstract class MixinPatternProviderLogic implements IUpgradeableObject, IEnergyDistributor, INeighborListener {

    @Unique
    private IUpgradeInventory af_$upgrades = UpgradeInventories.empty();
    @Unique
    private List<Direction> af_$sides = List.of();
    @Unique
    private EnergyDistributeService af_$service = null;
    @Unique
    private final EnergyHandler.SendAction[] af_$actions = new EnergyHandler.SendAction[6];
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
        this.af_$updateSleep();
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
        var storage = this.af_getStorage();
        var gird = this.mainNode.getGrid();
        var self = this.host.getBlockEntity();
        if (storage != null && self.getLevel() != null) {
            for (var d : this.af_$sides) {
                if (this.af_$actions[d.get3DDataValue()] == null) {
                    var te = self.getLevel().getBlockEntity(self.getBlockPos().offset(d.getNormal()));
                    var thatGrid = AFUtil.getGrid(te, d.getOpposite());
                    if (te != null && thatGrid != gird && AFUtil.isWhiteListTE(te, d.getOpposite())) {
                        this.af_$actions[d.get3DDataValue()] = EnergyHandler.getHandler(te, d.getOpposite());
                    } else {
                        this.af_$actions[d.get3DDataValue()] = EnergyHandler.SendAction.NOOP;
                    }
                }
                this.af_$actions[d.get3DDataValue()].send(storage, this.actionSource);
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

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public boolean isActive() {
        return this.mainNode.isActive();
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void setServiceHost(@Nullable EnergyDistributeService service) {
        this.af_$service = service;
        this.af_$updateSleep();
        if (service != null) {
            this.af_$sides = AFUtil.getSides(this.host);
        }
    }

    @SuppressWarnings("AddedMixinMembersNamePattern")
    @Override
    public void onChange(Direction side) {
        this.af_$actions[side.get3DDataValue()] = null;
    }

    @Unique
    public void af_$updateSleep() {
        if (this.af_$service != null) {
            if (this.af_$upgrades.isInstalled(AFItemAndBlock.INDUCTION_CARD)) {
                this.af_$service.wake(this);
            } else {
                this.af_$service.sleep(this);
            }
        }
    }

}
